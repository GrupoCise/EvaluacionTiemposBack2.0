package com.web.back.services;

import com.web.back.builders.ChangeLogBuilder;
import com.web.back.clients.ZWSHREvaluacioClient;
import com.web.back.mappers.EvaluationDtoMapper;
import com.web.back.mappers.EvaluationMapper;
import com.web.back.mappers.PostEvaluationApiRequestMapper;
import com.web.back.model.dto.EvaluationDto;
import com.web.back.model.entities.ChangeLog;
import com.web.back.model.entities.Evaluation;
import com.web.back.model.enumerators.StatusRegistroEnum;
import com.web.back.model.requests.CambioHorarioRequest;
import com.web.back.model.requests.PostEvaluationApiRequest;
import com.web.back.model.requests.EvaluationRequest;
import com.web.back.model.responses.CambioHorarioResponse;
import com.web.back.model.responses.CustomResponse;
import com.web.back.repositories.EvaluationRepository;
import com.web.back.repositories.UserRepository;
import com.web.back.utils.DateUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class SapEvaluationService {
    private final UserRepository userRepository;
    private final EvaluationRepository evaluationRepository;
    private final ZWSHREvaluacioClient zwshrEvaluacioClient;
    private final ChangeLogService changeLogService;

    public SapEvaluationService(UserRepository userRepository, EvaluationRepository evaluationRepository, ZWSHREvaluacioClient zwshrEvaluacioClient, ChangeLogService changeLogService) {
        this.userRepository = userRepository;
        this.evaluationRepository = evaluationRepository;
        this.zwshrEvaluacioClient = zwshrEvaluacioClient;
        this.changeLogService = changeLogService;
    }

    public List<EvaluationDto> getAllEvaluations() {
        return evaluationRepository.findAll().stream()
                .map(EvaluationDtoMapper::mapFrom).toList();
    }

    public List<EvaluationDto> getAllEvaluationsByFilters(String beginDate, String endDate, String sociedad, String areaNomina) {
        return evaluationRepository.findByFechaAndAreaNominaAndSociedad(beginDate, endDate, sociedad, areaNomina)
                .stream()
                .map(EvaluationDtoMapper::mapFrom).toList();
    }

    @Transactional(rollbackFor = {Exception.class})
    public Void deleteEvaluations(List<Integer> evaluationsToRemove) {
        if (evaluationsToRemove.isEmpty()) {
            return null;
        }

        var entitiesToRemove = evaluationRepository.findAllById(evaluationsToRemove);

        evaluationRepository.deleteAll(entitiesToRemove);

        return null;
    }

    @Transactional(rollbackFor = {Exception.class})
    public void sendApprovedEvaluationsToSap(String username, String beginDate, String endDate, String sociedad, String areaNomina) {
        var evaluations = evaluationRepository.findByFechaAndAreaNominaAndSociedad(beginDate, endDate, sociedad, areaNomina);
        var user = userRepository.findByUsername(username).orElseThrow();

        evaluations = evaluations.stream()
                .filter(evaluation -> evaluation.getApprobationLevel() != null &&
                        evaluation.getApprobationLevel() == 0 &&
                        !Objects.equals(evaluation.getStatusRegistro(), StatusRegistroEnum.SENT_TO_SAP.name()))
                .toList();

        List<PostEvaluationApiRequest> request = evaluations.stream().map(PostEvaluationApiRequestMapper::mapFrom).toList();

        if (request.isEmpty()) {
            throw new RuntimeException("No hay evaluaciones pendientes de ser enviadas!");
        }

        var response = zwshrEvaluacioClient.postEvaluation(request).block();

        if (response != null && response.getStatusCode().is2xxSuccessful()) {
            List<ChangeLog> changesSummary = new ArrayList<>();

            evaluations.forEach(evaluation -> {
                var previousValue = ObjectUtils.isNotEmpty(evaluation.getStatusRegistro()) ? evaluation.getStatusRegistro() : null;
                evaluation.setStatusRegistro(StatusRegistroEnum.SENT_TO_SAP.name());

                ChangeLog changeLog = new ChangeLog();

                changeLog.setField("Status Registro");
                changeLog.setOriginal(previousValue);
                changeLog.setUpdated(evaluation.getStatusRegistro());
                changeLog.setNumEmpleado(evaluation.getNumEmpleado());
                changeLog.setEmpleadoName(evaluation.getEmployeeName());
                changeLog.setEvaluationId(evaluation.getId());
                changeLog.setEditorUserName(user.getUsername());
                changeLog.setEditorName(user.getName());
                changeLog.setSociedad(evaluation.getSociedad());
                changeLog.setAreaNomina(evaluation.getAreaNomina());
                changeLog.setUpdatedOn(Instant.now());

                changesSummary.add(changeLog);
            });

            evaluationRepository.saveAll(evaluations);

            changeLogService.LogUpdateEvaluationsChanges(changesSummary);
        }

        if (response != null && !response.getStatusCode().is2xxSuccessful()) {
            throw new HttpClientErrorException(response.getStatusCode());
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    public CustomResponse<List<EvaluationDto>> updateEvaluations(EvaluationRequest request) {
        List<EvaluationDto> updatedEvaluations = request.getUpdatedEvaluations();
        var user = userRepository.findByUsername(request.getUserName()).orElseThrow();

        if (updatedEvaluations == null || updatedEvaluations.isEmpty()) {
            return new CustomResponse<List<EvaluationDto>>().ok(null, "No hay cambios que actualizar");
        }

        List<Evaluation> persistedEmployees;
        List<Evaluation> evaluationEntities;
        List<ChangeLog> changesSummary = new ArrayList<>();

        if (request.getIsTimesheetUpdateOnly()) {
            persistedEmployees = evaluationRepository.findAllByEmployeeNumber(updatedEvaluations.stream().map(EvaluationDto::getNumEmpleado).toList());
            var cambioHorariosResponse = applyCambiosDeHorario(updatedEvaluations);

            if (cambioHorariosResponse.isError()) {
                return new CustomResponse<List<EvaluationDto>>().badRequest("Error al aplicar cambio de Horario");
            }

            evaluationEntities = mapAuthorizedCambiosHorario(cambioHorariosResponse.getData(), persistedEmployees);

            evaluationEntities.forEach(updated -> {
                var original = persistedEmployees.stream().filter(f -> f.getId().equals(updated.getId())).findFirst().orElseThrow();

                changesSummary.addAll(ChangeLogBuilder.buildFrom(original, updated, user));
            });
        } else {
            persistedEmployees = evaluationRepository.findAllById(updatedEvaluations.stream().map(EvaluationDto::getId).toList());
            evaluationEntities = updatedEvaluations.stream().map(updated -> {
                var original = persistedEmployees.stream().filter(f -> f.getId().equals(updated.getId())).findFirst().orElseThrow();

                changesSummary.addAll(ChangeLogBuilder.buildFrom(original, updated, user));

                return EvaluationMapper.mapFrom(updated, original);
            }).toList();
        }

        evaluationRepository.saveAll(evaluationEntities);
        changeLogService.LogUpdateEvaluationsChanges(changesSummary);

        return new CustomResponse<List<EvaluationDto>>().ok(
                evaluationEntities.stream().map(EvaluationDtoMapper::mapFrom).toList()
        );
    }

    private CustomResponse<List<CambioHorarioResponse>> applyCambiosDeHorario(List<EvaluationDto> updatedEmployees) {
        var employee = updatedEmployees.get(0);
        var dateTimesheetChange = DateUtil.toStringYYYYMMDD(employee.getFecha());

        var timeSheetUpdate = new CambioHorarioRequest(employee.getNumEmpleado(), dateTimesheetChange, employee.getHorario());

        var updatesToApply = List.of(timeSheetUpdate);

        return zwshrEvaluacioClient.postCambioHorario(dateTimesheetChange, dateTimesheetChange, updatesToApply)
                .map(result -> new CustomResponse<List<CambioHorarioResponse>>().ok(result)).block();
    }

    private List<Evaluation> mapAuthorizedCambiosHorario(List<CambioHorarioResponse> cambioHorarios, List<Evaluation> persistedEmployees) {
        List<Evaluation> affectedEmployees = new ArrayList<>();

        if (cambioHorarios == null || cambioHorarios.isEmpty()) return affectedEmployees;

        for (Evaluation evaluation : persistedEmployees) {
            var affectedRegistryForEmployee = cambioHorarios.stream()
                    .filter(f -> Objects.equals(f.empleado(), evaluation.getNumEmpleado()))
                    .filter(f -> f.fecha().equals(DateUtil.toStringYYYYMMDD(evaluation.getFecha())))
                    .findFirst();

            affectedRegistryForEmployee.ifPresent(cambioHorarioResponse ->
                    affectedEmployees.add(
                            EvaluationMapper.mapFromTimeSheetUpdate(
                                    evaluation,
                                    cambioHorarioResponse.estatusGen(),
                                    cambioHorarioResponse.horario()
                            )
                    ));
        }

        return affectedEmployees;
    }
}
