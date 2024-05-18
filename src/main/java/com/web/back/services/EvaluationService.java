package com.web.back.services;

import com.web.back.clients.ZWSHREvaluacioClient;
import com.web.back.mappers.EvaluationDtoMapper;
import com.web.back.mappers.EvaluationMapper;
import com.web.back.model.dto.EvaluationDto;
import com.web.back.model.entities.Evaluation;
import com.web.back.model.entities.User;
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

import java.util.List;
import java.util.Objects;

@Service
public class EvaluationService {
    private final UserRepository userRepository;
    private final EvaluationRepository evaluationRepository;
    private final ZWSHREvaluacioClient zwshrEvaluacioClient;

    public EvaluationService(UserRepository userRepository, EvaluationRepository evaluationRepository, ZWSHREvaluacioClient zwshrEvaluacioClient) {
        this.userRepository = userRepository;
        this.evaluationRepository = evaluationRepository;
        this.zwshrEvaluacioClient = zwshrEvaluacioClient;
    }

    @Transactional(rollbackFor = {Exception.class})
    public CustomResponse<List<EvaluationDto>> updateEvaluations(UpdateEvaluationRequest request) {

//        List<EvaluationDto> updatedEvaluations = request.getUpdatedEvaluations();
//
//        if (updatedEvaluations == null || updatedEvaluations.isEmpty()) {
//            return new CustomResponse<List<EvaluationDto>>().ok(null, "No hay cambios que actualizar");
//        }
//
//        var optionalUser = userRepository.findByUsername(request.getUserName());
//
//        User user = optionalUser.orElse(null);
//
//        var employees = zwshrEvaluacioClient.getEmployees(request.getUserName(), request.getBeginDate(), request.getEndDate(), request.getSociedad(), request.getAreaNomina()).block();
//
//        var cambioHorariosResponse = applyCambiosDeHorario(employees, updatedEvaluations, request.getBeginDate(), request.getEndDate());
//
//        if (cambioHorariosResponse.isError()) {
//            return new CustomResponse<List<EvaluationDto>>().badRequest("Error al aplicar cambio de Horario");
//        }
//
//        for (EvaluationDto evaluationDto : updatedEvaluations) {
//            evaluationDto.setStatusRegistro("1");
//
//            applyAuthorizedCambiosHorario(cambioHorariosResponse.getData(), evaluationDto);
//
//            registrosTrabajoRepository.updateRegistrowLog(evaluationDto.getId(),
//                    registrosTrabajo.getHoraEntrada(),
//                    registrosTrabajo.getHoraSalida(),
//                    registrosTrabajo.getResultadoGeneral(),
//                    registrosTrabajo.getComentario(),
//                    registrosTrabajo.getEnlace(),
//                    registrosTrabajo.getEmpleado().getNum_empleado(),
//                    user.getId(),
//                    registrosTrabajo.getHorario().getIdHorario(),
//                    0L,
//                    0L);
//        }
//
//        if (!java.util.Objects.equals(request.getVacaciones(), null)) {
//            if (!(request.getVacaciones().isEmpty())) {
//                updatedEvaluations = saveVacations(updatedEvaluations, request.getVacaciones(), user);
//            }
//        }
//
//        if (!java.util.Objects.equals(request.getIncapacidades(), null)) {
//            if (!(request.getIncapacidades().isEmpty())) {
//                updatedEvaluations = saveIncapacidad(updatedEvaluations, request.getIncapacidades(), user);
//            }
//        }
//
//        var evaluationEntities = updatedEvaluations.stream().map(updated -> {
//            var original = evaluationRepository.findById(updated.getId());
//
//            return EvaluationMapper.mapFrom(updated, original);
//        }).toList();
//
//        evaluationRepository.saveAll(evaluationEntities);
//
//        if (!java.util.Objects.equals(request.getApprovedEvaluations(), null)) {
//            for (Long idRegistro : request.getApprovedEvaluations()) {
//
//                evaluationRepository.aprobarRegistrowLog(idRegistro, user.getId());
//
//            }
//        }
//
        //return new CustomResponse<List<EvaluationDto>>().ok(updatedEvaluations);
        return new CustomResponse<List<EvaluationDto>>().ok(null);
    }

//    private CustomResponse<List<CambioHorarioResponse>> applyCambiosDeHorario(List<EmployeeApiResponse> employees, List<EvaluationDto> updatedEmployees, String beginDate, String endDate){
//        var updatesToApply = employees.stream()
//                .flatMap(current -> updatedEmployees.stream()
//                        .filter(updated -> current.getEmployeeNumber().equals(updated.getNumEmpleado()))
//                        .filter(updated -> !current.getHorario().equals(updated.getHorario()))
//                        .filter(updated -> DateUtil.toStringYYYYMMDD(current.getFecha())
//                                .equals(DateUtil.toStringYYYYMMDD(updated.getFecha())))
//                        .limit(1)).map(employee ->
//                        new CambioHorarioRequest(employee.getNumEmpleado(),
//                                DateUtil.toStringYYYYMMDD(employee.getFecha()),
//                                employee.getHorario())).toList();
//
//        if(updatesToApply.isEmpty()){
//            return new CustomResponse<List<CambioHorarioResponse>>().ok(null);
//        }
//
//        return applyHorarioUpdate(updatesToApply, beginDate, endDate);
//    }
//
//    private CustomResponse<List<CambioHorarioResponse>> applyHorarioUpdate(List<CambioHorarioRequest> updatesToApply, String beginDate, String endDate){
//        try {
//            return zwshrEvaluacioClient.postCambioHorario(beginDate, endDate)
//                    .map(result -> new CustomResponse<List<CambioHorarioResponse>>().ok(result)).block();
//        } catch (Exception e) {
//            return new CustomResponse<List<CambioHorarioResponse>>().internalError(e.getMessage());
//        }
//    }
//
//    private void applyAuthorizedCambiosHorario(List<CambioHorarioResponse> cambioHorarios, EvaluationDto evaluationDto){
//        if(cambioHorarios ==null || cambioHorarios.isEmpty()) return;
//
//        var affectedRegistryForEmployee = cambioHorarios.stream()
//                .filter(f -> Objects.equals(f.empleado(), evaluationDto.getNumEmpleado()))
//                .filter(f -> f.fecha().equals(DateUtil.toStringYYYYMMDD(evaluationDto.getFecha())))
//                .findFirst();
//
//        if(affectedRegistryForEmployee.isPresent()){
//            evaluationDto.setHorario(affectedRegistryForEmployee.get().horario());
//            evaluationDto.setResultadoGeneral(affectedRegistryForEmployee.get().estatusGen());
//        }
//    }
//
//    public List<EvaluationDto> saveVacations(List<EvaluationDto> registrosTrabajosmodificados, List<Vacacion> vacaciones, User user) {
//        for (Vacacion vacacion:vacaciones) {
//
//            List<Evaluation> evaluations =evaluationRepository.findByFechaAndEmpleado(DateUtil.toStringYYYYMMDD(vacacion.getBeginDate()), DateUtil.toStringYYYYMMDD(vacacion.getEndDate()), vacacion.getNumEmpleado());
//
//            for (Evaluation evaluation : evaluations) {
//                if(evaluation.getResultadoGeneral()==null){
//                    evaluation.setResultadoGeneral("");
//                }
//
//                if(!evaluation.getResultadoGeneral().equals("D")) {
//
//                    registrosTrabajoRepository.updateRegistrowLog(evaluation.getId(),
//                            evaluation.getHoraEntrada(),
//                            evaluation.getHoraSalida(),
//                            "V",
//                            evaluation.getComentario(),
//                            evaluation.getEnlace(),
//                            evaluation.getNumEmpleado(),
//                            user.getId(),
//                            evaluation.getHorario(),
//                            0L,
//                            0L);
//
//                    evaluation.setResultadoGeneral("V");
//                    evaluation.setStatusRegistro("1");
//                }
//            }
//
//            registrosTrabajosmodificados.addAll(evaluations.stream().map(EvaluationDtoMapper::mapFrom).toList());
//        }
//        return registrosTrabajosmodificados;
//    }
//
//    public List<EvaluationDto> saveIncapacidad(List<EvaluationDto> registrosTrabajosmodificados, List<Incapacidad> incapacidades, User user){
//
//        for (Incapacidad incapacidad:incapacidades) {
//            List<Evaluation> evaluations =evaluationRepository.findByFechaAndEmpleado(DateUtil.toStringYYYYMMDD(incapacidad.getBeginDate()), DateUtil.toStringYYYYMMDD(incapacidad.getEndDate()), incapacidad.getNumEmpleado());
//
//            for (Evaluation evaluation :evaluations) {
//                if(evaluation.getResultadoGeneral()==null){
//                    evaluation.setResultadoGeneral("");
//                }
//
//                if(!evaluation.getResultadoGeneral().equals("D")) {
//
//                    registrosTrabajoRepository.updateRegistrowLog(evaluation.getId(),
//                            evaluation.getHoraEntrada(),
//                            evaluation.getHoraSalida(),
//                            "I",
//                            evaluation.getComentario(),
//                            evaluation.getEnlace(),
//                            evaluation.getNumEmpleado(),
//                            user.getId(),
//                            evaluation.getHorario(),
//                            0L,
//                            0L);
//
//                    evaluation.setIncapacidad(incapacidad.getIncapacidadId());
//                    evaluation.setResultadoGeneral("I");
//                    evaluation.setStatusRegistro("1");
//                }
//
//                for(Evaluation evaluation1 :evaluations){
//                    if(evaluation1.getId().equals(evaluation.getId())){
//                        evaluation1.setIncapacidad(incapacidad.getIncapacidadId());
//                        break;
//                    }
//                }
//            }
//        }
//
//        return registrosTrabajosmodificados;
//    }
}
