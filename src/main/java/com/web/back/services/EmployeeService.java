package com.web.back.services;

import com.web.back.clients.ZWSHREvaluacioClient;
import com.web.back.mappers.EvaluationDtoMapper;
import com.web.back.mappers.EvaluationMapper;
import com.web.back.mappers.RegistroTiemposDtoMapper;
import com.web.back.model.dto.EvaluationDto;
import com.web.back.model.dto.RegistroTiemposDto;
import com.web.back.model.requests.RegistroTiemposRequest;
import com.web.back.model.responses.CustomResponse;
import com.web.back.repositories.EvaluationRepository;
import com.web.back.utils.DateUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class EmployeeService {
    private final ZWSHREvaluacioClient zwshrEvaluacioClient;
    private final EvaluationRepository evaluationRepository;

    public EmployeeService(ZWSHREvaluacioClient zwshrEvaluacioClient, EvaluationRepository evaluationRepository) {
        this.zwshrEvaluacioClient = zwshrEvaluacioClient;
        this.evaluationRepository = evaluationRepository;
    }

    public CustomResponse<List<EvaluationDto>> getEmployeesByFilters(String beginDate, String endDate, String sociedad, String areaNomina, String userName) {
        var savedEmployeesOnEvaluation = evaluationRepository.findByFechaAndAreaNominaAndSociedad(beginDate, endDate, sociedad, areaNomina);

        if (!savedEmployeesOnEvaluation.isEmpty()) {
            return new CustomResponse<List<EvaluationDto>>().ok(
                    savedEmployeesOnEvaluation.stream().map(EvaluationDtoMapper::mapFrom).toList()
            );
        }

        return getEmployeesByFiltersFromService(beginDate, endDate, sociedad, areaNomina, userName);
    }

    public CustomResponse<List<EvaluationDto>> getEmployeesByFiltersFromService(String beginDate, String endDate, String sociedad, String areaNomina, String userName) {
        List<EvaluationDto> evaluationDtos = new ArrayList<>();

        Objects.requireNonNull(zwshrEvaluacioClient.getEmployees(userName, beginDate, endDate, sociedad, areaNomina).block())
                .forEach(employee -> {
                    var optionalEmployee = evaluationRepository.findByFechaAndAreaNominaAndSociedadAndEmpleado(
                            DateUtil.toStringYYYYMMDD(employee.getFecha()), sociedad, areaNomina, employee.getEmpleado());

                    if (optionalEmployee.isPresent()) {
                        return;
                    }

                    var evaluation = EvaluationMapper.mapFrom(employee, sociedad, areaNomina);

                    evaluationRepository.save(evaluation);

                    evaluationDtos.add(EvaluationDtoMapper.mapFrom(evaluation));
                });

        return new CustomResponse<List<EvaluationDto>>().ok(evaluationDtos);
    }

    public CustomResponse<List<RegistroTiemposDto>> getRegistroTiempos(String beginDate, String endDate, String userName) {
        List<RegistroTiemposDto> registroTiemposList = zwshrEvaluacioClient.getRegistroTiempos(userName, beginDate, endDate)
                .map(RegistroTiemposDtoMapper::mapFrom)
                .block();

        return new CustomResponse<List<RegistroTiemposDto>>().ok(registroTiemposList);
    }

    public ResponseEntity<Void>  sendTimeSheetChanges(List<RegistroTiemposDto> registroTiemposDtos) {
        List<RegistroTiemposRequest> request = RegistroTiemposDtoMapper.mapToRequestFrom(registroTiemposDtos);

        return zwshrEvaluacioClient.postRegistroTiempos(request).block();
    }
}
