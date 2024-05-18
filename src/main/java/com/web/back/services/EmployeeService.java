package com.web.back.services;

import com.web.back.clients.ZWSHREvaluacioClient;
import com.web.back.mappers.EvaluationDtoMapper;
import com.web.back.mappers.EvaluationMapper;
import com.web.back.model.dto.EvaluationDto;
import com.web.back.model.responses.CustomResponse;
import com.web.back.repositories.EvaluationRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeService {
    private final ZWSHREvaluacioClient zwshrEvaluacioClient;
    private final EvaluationRepository evaluationRepository;

    public EmployeeService(ZWSHREvaluacioClient zwshrEvaluacioClient, EvaluationRepository evaluationRepository) {
        this.zwshrEvaluacioClient = zwshrEvaluacioClient;
        this.evaluationRepository = evaluationRepository;
    }

    public Mono<CustomResponse<List<EvaluationDto>>> getEmployeesByFilters(String beginDate, String endDate, String sociedad, String areaNomina, String userName) {
        var savedEmployeesOnEvaluation = evaluationRepository.findByFechaAndAreaNominaAndSociedad(beginDate, endDate, sociedad, areaNomina);

        if (!savedEmployeesOnEvaluation.isEmpty()) {
            return Mono.just(new CustomResponse<List<EvaluationDto>>().ok(
                    savedEmployeesOnEvaluation.stream().map(EvaluationDtoMapper::mapFrom).toList()
            ));
        }

        return getEmployeesByFiltersFromService(beginDate, endDate, sociedad, areaNomina, userName);
    }

    public Mono<CustomResponse<List<EvaluationDto>>> getEmployeesByFiltersFromService(String beginDate, String endDate, String sociedad, String areaNomina, String userName) {
        return zwshrEvaluacioClient.getEmployees(beginDate, endDate, sociedad, areaNomina, userName)
                .map(employees -> {
                    List<EvaluationDto> evaluationDtos = new ArrayList<>();

                    employees.forEach(employee -> {
                        var evaluation = EvaluationMapper.mapFrom(employee, sociedad, areaNomina);

                        var optionalEmployee = evaluationRepository.findByFechaAndAreaNominaAndSociedadAndEmpleado(beginDate, endDate, sociedad, areaNomina, employee.getEmployeeNumber());

                        if (optionalEmployee.isEmpty()) {
                            evaluationRepository.updateOrInsert(evaluation);
                        }
                        evaluationDtos.add(EvaluationDtoMapper.mapFrom(evaluation));
                    });

                    return new CustomResponse<List<EvaluationDto>>().ok(evaluationDtos);
                });
    }
}
