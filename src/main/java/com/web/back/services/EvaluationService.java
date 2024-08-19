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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class EvaluationService {
    private final UserRepository userRepository;
    private final EvaluationRepository evaluationRepository;
    private final ZWSHREvaluacioClient zwshrEvaluacioClient;
    private final ChangeLogService changeLogService;

    public EvaluationService(UserRepository userRepository, EvaluationRepository evaluationRepository, ZWSHREvaluacioClient zwshrEvaluacioClient, ChangeLogService changeLogService) {
        this.userRepository = userRepository;
        this.evaluationRepository = evaluationRepository;
        this.zwshrEvaluacioClient = zwshrEvaluacioClient;
        this.changeLogService = changeLogService;
    }

    public List<EvaluationDto> getAllEvaluations() {
        return evaluationRepository.findAll().stream()
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
    public ResponseEntity<Void> sendApprovedEvaluationsToSap(String beginDate, String endDate, String sociedad, String areaNomina) {
        var evaluations = evaluationRepository.findByFechaAndAreaNominaAndSociedad(beginDate, endDate, sociedad, areaNomina);

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
            evaluations.forEach(evaluation -> evaluation.setStatusRegistro(StatusRegistroEnum.SENT_TO_SAP.name()));

            evaluationRepository.saveAll(evaluations);
        }

        return response;
    }

    @Transactional(rollbackFor = {Exception.class})
    public CustomResponse<List<EvaluationDto>> updateEvaluations(EvaluationRequest request) {
        List<EvaluationDto> updatedEvaluations = request.getUpdatedEvaluations();
        var user = userRepository.findByUsername(request.getUserName()).orElseThrow();

        if (updatedEvaluations == null || updatedEvaluations.isEmpty()) {
            return new CustomResponse<List<EvaluationDto>>().ok(null, "No hay cambios que actualizar");
        }

        var persistedEmployees = evaluationRepository.findAllById(updatedEvaluations.stream().map(EvaluationDto::getId).toList());

        var cambioHorariosResponse = applyCambiosDeHorario(persistedEmployees, updatedEvaluations, request.getBeginDate(), request.getEndDate());

        if (cambioHorariosResponse.isError()) {
            return new CustomResponse<List<EvaluationDto>>().badRequest("Error al aplicar cambio de Horario");
        }

        mapAuthorizedCambiosHorario(cambioHorariosResponse.getData(), updatedEvaluations);

        List<ChangeLog> changesSummary = new ArrayList<>();

        var evaluationEntities = updatedEvaluations.stream().map(updated -> {
            var original = persistedEmployees.stream().filter(f -> f.getId().equals(updated.getId())).findFirst().orElseThrow();

            changesSummary.addAll(ChangeLogBuilder.buildFrom(original, updated, user));

            return EvaluationMapper.mapFrom(updated, original);
        }).toList();

        evaluationRepository.saveAll(evaluationEntities);

        changeLogService.LogUpdateEvaluationsChanges(changesSummary);

        return new CustomResponse<List<EvaluationDto>>().ok(updatedEvaluations);
    }

    private CustomResponse<List<CambioHorarioResponse>> applyCambiosDeHorario(List<Evaluation> employees, List<EvaluationDto> updatedEmployees, String beginDate, String endDate) {
        var updatesToApply = employees.stream()
                .flatMap(current -> updatedEmployees.stream()
                        .filter(updated -> current.getNumEmpleado().equals(updated.getNumEmpleado()))
                        .filter(updated -> (current.getHorario() == null && updated.getHorario() != null) ||
                                (current.getHorario() != null &&
                                        !current.getHorario().equals(updated.getHorario())
                                ))
                        .filter(updated -> DateUtil.toStringYYYYMMDD(current.getFecha())
                                .equals(DateUtil.toStringYYYYMMDD(updated.getFecha())))
                        .filter(updated -> current.getTurn() != null &&
                                current.getTurn().equals(updated.getTurn()))
                        .limit(1)).map(employee ->
                        new CambioHorarioRequest(employee.getNumEmpleado(),
                                DateUtil.toStringYYYYMMDD(employee.getFecha()),
                                employee.getHorario())).toList();

        if (updatesToApply.isEmpty()) {
            return new CustomResponse<List<CambioHorarioResponse>>().ok(null);
        }

        return zwshrEvaluacioClient.postCambioHorario(beginDate, endDate, updatesToApply)
                .map(result -> new CustomResponse<List<CambioHorarioResponse>>().ok(
                        result.stream().filter(f -> f.empleado() != null && f.fecha() != null &&
                                updatesToApply.stream().anyMatch(a ->
                                        a.empleado() != null && a.fecha() != null &&
                                                a.empleado().equals(f.empleado()) &&
                                                a.fecha().equals(f.empleado()))).toList())).block();
    }

    private void mapAuthorizedCambiosHorario(List<CambioHorarioResponse> cambioHorarios, List<EvaluationDto> updatedEvaluations) {
        if (cambioHorarios == null || cambioHorarios.isEmpty()) return;

        for (EvaluationDto evaluationDto : updatedEvaluations) {
            var affectedRegistryForEmployee = cambioHorarios.stream()
                    .filter(f -> Objects.equals(f.empleado(), evaluationDto.getNumEmpleado()))
                    .filter(f -> f.fecha().equals(DateUtil.toStringYYYYMMDD(evaluationDto.getFecha())))
                    .findFirst();

            if (affectedRegistryForEmployee.isPresent()) {
                evaluationDto.setHorario(affectedRegistryForEmployee.get().horario());
                evaluationDto.setResultadoGeneral(affectedRegistryForEmployee.get().estatusGen());
            }
        }
    }
}
