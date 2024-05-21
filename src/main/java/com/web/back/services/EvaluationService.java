package com.web.back.services;

import com.web.back.builders.ChangeLogBuilder;
import com.web.back.clients.ZWSHREvaluacioClient;
import com.web.back.mappers.EvaluationDtoMapper;
import com.web.back.mappers.EvaluationMapper;
import com.web.back.model.dto.EvaluationDto;
import com.web.back.model.entities.ChangeLog;
import com.web.back.model.entities.Evaluation;
import com.web.back.model.enumerators.IncidencesEnum;
import com.web.back.model.requests.CambioHorarioRequest;
import com.web.back.model.requests.UpdateEvaluationRequest;
import com.web.back.model.responses.CambioHorarioResponse;
import com.web.back.model.responses.CustomResponse;
import com.web.back.model.responses.EmployeeApiResponse;
import com.web.back.model.responses.evaluacion.Incapacidad;
import com.web.back.model.responses.evaluacion.Vacacion;
import com.web.back.repositories.EvaluationRepository;
import com.web.back.repositories.UserRepository;
import com.web.back.utils.DateUtil;
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

    @Transactional(rollbackFor = {Exception.class})
    public CustomResponse<List<EvaluationDto>> updateEvaluations(UpdateEvaluationRequest request) {
        List<EvaluationDto> updatedEvaluations = request.getUpdatedEvaluations();
        var user = userRepository.findByUsername(request.getUserName()).orElseThrow();

        if (updatedEvaluations == null || updatedEvaluations.isEmpty()) {
            return new CustomResponse<List<EvaluationDto>>().ok(null, "No hay cambios que actualizar");
        }

        var employees = zwshrEvaluacioClient.getEmployees(request.getUserName(), request.getBeginDate(), request.getEndDate(), request.getSociedad(), request.getAreaNomina()).block();

        if (employees == null || employees.isEmpty()) {
            return new CustomResponse<List<EvaluationDto>>().badRequest("No hay empleados que cumplan con los filstros actuales");
        }

        var cambioHorariosResponse = applyCambiosDeHorario(employees, updatedEvaluations, request.getBeginDate(), request.getEndDate());

        if (cambioHorariosResponse.isError()) {
            return new CustomResponse<List<EvaluationDto>>().badRequest("Error al aplicar cambio de Horario");
        }

        mapAuthorizedCambiosHorario(cambioHorariosResponse.getData(), updatedEvaluations);
        mapVacations(updatedEvaluations, request.getVacaciones());
        mapIncapacidades(updatedEvaluations, request.getIncapacidades());

        List<ChangeLog> changesSummary = new ArrayList<>();

        var evaluationEntities = updatedEvaluations.stream().map(updated -> {
            var original = evaluationRepository.findById(updated.getId()).orElseThrow();

            changesSummary.addAll(ChangeLogBuilder.buildFrom(original, updated, user));

            return EvaluationMapper.mapFrom(updated, original);
        }).toList();

        evaluationRepository.saveAll(evaluationEntities);

        changeLogService.LogUpdateEvaluationsChanges(changesSummary);

        return new CustomResponse<List<EvaluationDto>>().ok(updatedEvaluations);
    }

    private CustomResponse<List<CambioHorarioResponse>> applyCambiosDeHorario(List<EmployeeApiResponse> employees, List<EvaluationDto> updatedEmployees, String beginDate, String endDate) {
        var updatesToApply = employees.stream()
                .flatMap(current -> updatedEmployees.stream()
                        .filter(updated -> current.getEmployeeNumber().equals(updated.getNumEmpleado()))
                        .filter(updated -> !current.getHorario().equals(updated.getHorario()))
                        .filter(updated -> DateUtil.toStringYYYYMMDD(current.getFecha())
                                .equals(DateUtil.toStringYYYYMMDD(updated.getFecha())))
                        .limit(1)).map(employee ->
                        new CambioHorarioRequest(employee.getNumEmpleado(),
                                DateUtil.toStringYYYYMMDD(employee.getFecha()),
                                employee.getHorario())).toList();

        if (updatesToApply.isEmpty()) {
            return new CustomResponse<List<CambioHorarioResponse>>().ok(null);
        }

        return zwshrEvaluacioClient.postCambioHorario(beginDate, endDate)
                .map(result -> new CustomResponse<List<CambioHorarioResponse>>().ok(result)).block();
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

    private void mapVacations(List<EvaluationDto> updatedEvaluations, List<Vacacion> vacations) {
        if (vacations == null || vacations.isEmpty()) return;

        for (Vacacion vacacion : vacations) {

            List<Evaluation> evaluations = evaluationRepository.findByFechaAndEmpleado(DateUtil.toStringYYYYMMDD(vacacion.getBeginDate()), DateUtil.toStringYYYYMMDD(vacacion.getEndDate()), vacacion.getNumEmpleado());

            for (Evaluation evaluation : evaluations) {
                var evaluationDto = updatedEvaluations.stream()
                        .filter(f -> Objects.equals(f.getNumEmpleado(), evaluation.getNumEmpleado()))
                        .filter(f -> DateUtil.toStringYYYYMMDD(f.getFecha()).equals(DateUtil.toStringYYYYMMDD(evaluation.getFecha())))
                        .findFirst();

                if (evaluationDto.isPresent()) {
                    evaluationDto.get().setTipoIncidencia(IncidencesEnum.VACATIONS.getValue());
                } else {
                    evaluation.setTipoIncidencia(IncidencesEnum.VACATIONS.getValue());

                    updatedEvaluations.add(EvaluationDtoMapper.mapFrom(evaluation));
                }
            }
        }
    }

    public void mapIncapacidades(List<EvaluationDto> updatedEvaluations, List<Incapacidad> incapacidades) {
        for (Incapacidad incapacidad : incapacidades) {

            List<Evaluation> evaluations = evaluationRepository.findByFechaAndEmpleado(DateUtil.toStringYYYYMMDD(incapacidad.getBeginDate()), DateUtil.toStringYYYYMMDD(incapacidad.getEndDate()), incapacidad.getNumEmpleado());

            for (Evaluation evaluation : evaluations) {
                var evaluationDto = updatedEvaluations.stream()
                        .filter(f -> Objects.equals(f.getNumEmpleado(), evaluation.getNumEmpleado()))
                        .filter(f -> DateUtil.toStringYYYYMMDD(f.getFecha()).equals(DateUtil.toStringYYYYMMDD(evaluation.getFecha())))
                        .findFirst();

                if (evaluationDto.isPresent()) {
                    evaluationDto.get().setTipoIncidencia(IncidencesEnum.VACATIONS.getValue());
                    evaluationDto.get().setReferencia(incapacidad.getReferencia());
                    evaluationDto.get().setConsecutivo1(incapacidad.getConsecutivo1());
                    evaluationDto.get().setConsecutivo2(incapacidad.getConsecutivo2());
                } else {
                    evaluation.setTipoIncidencia(IncidencesEnum.INCAPACITY.getValue());
                    evaluation.setReferencia(incapacidad.getReferencia());
                    evaluation.setConsecutivo1(incapacidad.getConsecutivo1());
                    evaluation.setConsecutivo2(incapacidad.getConsecutivo2());

                    updatedEvaluations.add(EvaluationDtoMapper.mapFrom(evaluation));
                }
            }
        }
    }
}
