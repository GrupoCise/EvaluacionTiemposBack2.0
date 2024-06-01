package com.web.back.services;

import com.web.back.builders.ChangeLogBuilder;
import com.web.back.clients.ZWSHREvaluacioClient;
import com.web.back.mappers.EvaluationDtoMapper;
import com.web.back.mappers.EvaluationMapper;
import com.web.back.mappers.PostEvaluationApiRequestMapper;
import com.web.back.model.dto.EvaluationDto;
import com.web.back.model.entities.ChangeLog;
import com.web.back.model.entities.Evaluation;
import com.web.back.model.enumerators.IncidencesEnum;
import com.web.back.model.enumerators.StatusRegistroEnum;
import com.web.back.model.requests.CambioHorarioRequest;
import com.web.back.model.requests.PostEvaluationApiRequest;
import com.web.back.model.requests.EvaluationRequest;
import com.web.back.model.responses.CambioHorarioResponse;
import com.web.back.model.responses.CustomResponse;
import com.web.back.model.responses.EmployeeApiResponse;
import com.web.back.model.responses.evaluacion.Incapacidad;
import com.web.back.model.responses.evaluacion.Vacacion;
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

    @Transactional(rollbackFor = {Exception.class})
    public ResponseEntity<Void> sendApprovedEvaluationsToSap(String beginDate, String endDate, String sociedad, String areaNomina){
        var evaluations = evaluationRepository.findByFechaAndAreaNominaAndSociedad(beginDate, endDate, sociedad, areaNomina);

        evaluations = evaluations.stream()
                .filter(evaluation -> evaluation.getApprobationLevel() != null &&
                        evaluation.getApprobationLevel() == 0 &&
                        !Objects.equals(evaluation.getStatusRegistro(), StatusRegistroEnum.SENT_TO_SAP.name()))
                .toList();

        List<PostEvaluationApiRequest> request = evaluations.stream().map(PostEvaluationApiRequestMapper::mapFrom).toList();

        var response = zwshrEvaluacioClient.postEvaluation(request).block();

        if(response != null && response.getStatusCode().is2xxSuccessful()){
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

        var employees = zwshrEvaluacioClient.getEmployees(request.getUserName(), request.getBeginDate(), request.getEndDate(), request.getSociedad(), request.getAreaNomina()).block();

        if (employees == null || employees.isEmpty()) {
            return new CustomResponse<List<EvaluationDto>>().badRequest("No hay empleados que cumplan con los filstros actuales");
        }

        var cambioHorariosResponse = applyCambiosDeHorario(employees, updatedEvaluations, request.getBeginDate(), request.getEndDate());

        if (cambioHorariosResponse.isError()) {
            return new CustomResponse<List<EvaluationDto>>().badRequest("Error al aplicar cambio de Horario");
        }

        mapAuthorizedCambiosHorario(cambioHorariosResponse.getData(), updatedEvaluations);
        updatedEvaluations = mapVacations(updatedEvaluations, request.getVacaciones());
        updatedEvaluations = mapIncapacidades(updatedEvaluations, request.getIncapacidades());

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
                        .filter(updated -> current.getEmpleado().equals(updated.getNumEmpleado()))
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

        return zwshrEvaluacioClient.postCambioHorario(beginDate, endDate, updatesToApply)
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

    private List<EvaluationDto> mapVacations(List<EvaluationDto> updatedEvaluations, List<Vacacion> vacations) {
        if (vacations == null || vacations.isEmpty()) return updatedEvaluations;

        for (Vacacion vacacion : vacations) {

            List<Evaluation> evaluations = evaluationRepository.findByFechaAndEmpleado(vacacion.getNumEmpleado(), vacacion.getBeginDate(), vacacion.getEndDate());

            for (Evaluation evaluation : evaluations) {
                var evaluationDto = updatedEvaluations.stream()
                        .filter(f -> Objects.equals(f.getNumEmpleado(), evaluation.getNumEmpleado()))
                        .filter(f -> DateUtil.toStringYYYYMMDD(f.getFecha()).equals(DateUtil.toStringYYYYMMDD(evaluation.getFecha())))
                        .findFirst();

                if (evaluationDto.isPresent()) {
                    updatedEvaluations.remove(evaluationDto.get());
                    var newResultadoGeneral = appendIncidenceToResultadoGenera(evaluationDto.get().getResultadoGeneral(), IncidencesEnum.VACATIONS);

                    evaluationDto.get().setResultadoGeneral(newResultadoGeneral);
                    updatedEvaluations.add(evaluationDto.get());
                } else {
                    var newResultadoGeneral = appendIncidenceToResultadoGenera(evaluation.getResultadoGeneral(), IncidencesEnum.VACATIONS);

                    evaluation.setResultadoGeneral(newResultadoGeneral);
                    updatedEvaluations.add(EvaluationDtoMapper.mapFrom(evaluation));
                }
            }
        }

        return updatedEvaluations;
    }

    private String appendIncidenceToResultadoGenera(String resultadoGeneral, IncidencesEnum incidences){
        resultadoGeneral = resultadoGeneral == null ? ""  : resultadoGeneral;

        if(resultadoGeneral.isEmpty()){
            resultadoGeneral = incidences.getValue();
        }else{
            if(!resultadoGeneral.contains(incidences.getValue())) {
                resultadoGeneral = String.join("|", resultadoGeneral, incidences.getValue());
            }
        }

        return resultadoGeneral;
    }

    public List<EvaluationDto> mapIncapacidades(List<EvaluationDto> updatedEvaluations, List<Incapacidad> incapacidades) {
        if (incapacidades == null || incapacidades.isEmpty()) return updatedEvaluations;

        for (Incapacidad incapacidad : incapacidades) {

            List<Evaluation> evaluations = evaluationRepository.findByFechaAndEmpleado(incapacidad.getNumEmpleado(), incapacidad.getBeginDate(), incapacidad.getEndDate());

            for (Evaluation evaluation : evaluations) {
                var evaluationDto = updatedEvaluations.stream()
                        .filter(f -> Objects.equals(f.getNumEmpleado(), evaluation.getNumEmpleado()))
                        .filter(f -> DateUtil.toStringYYYYMMDD(f.getFecha()).equals(DateUtil.toStringYYYYMMDD(evaluation.getFecha())))
                        .findFirst();

                if (evaluationDto.isPresent()) {
                    updatedEvaluations.remove(evaluationDto.get());

                    evaluationDto.get().setResultadoGeneral(
                            appendIncidenceToResultadoGenera(evaluationDto.get().getResultadoGeneral(), IncidencesEnum.INCAPACITY));
                    evaluationDto.get().setReferencia(incapacidad.getReferencia());
                    evaluationDto.get().setConsecutivo1(incapacidad.getConsecutivo1());
                    evaluationDto.get().setConsecutivo2(incapacidad.getConsecutivo2());

                    updatedEvaluations.add(evaluationDto.get());
                } else {
                    evaluation.setResultadoGeneral(
                            appendIncidenceToResultadoGenera(evaluation.getResultadoGeneral(), IncidencesEnum.INCAPACITY));
                    evaluation.setReferencia(incapacidad.getReferencia());
                    evaluation.setConsecutivo1(incapacidad.getConsecutivo1());
                    evaluation.setConsecutivo2(incapacidad.getConsecutivo2());

                    updatedEvaluations.add(EvaluationDtoMapper.mapFrom(evaluation));
                }
            }
        }

        return updatedEvaluations;
    }
}
