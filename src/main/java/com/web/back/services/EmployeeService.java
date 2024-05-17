package com.web.back.services;

import com.web.back.clients.ZWSHREvaluacioService;
import com.web.back.model.responses.CustomResponse;
import com.web.back.model.responses.EmployeeApiResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class EmployeeService {
    private final ZWSHREvaluacioService zwshrEvaluacioService;

    public EmployeeService(ZWSHREvaluacioService zwshrEvaluacioService) {
        this.zwshrEvaluacioService = zwshrEvaluacioService;
    }

    public Mono<CustomResponse<List<EmployeeApiResponse>>> getEmployeesByFilters(String beginDate, String endDate, String sociedad, String areaNomina, String userName) {

        return zwshrEvaluacioService.getEmployees(beginDate, endDate, sociedad, areaNomina, userName)
                .map(employees -> new CustomResponse<List<EmployeeApiResponse>>().ok(employees));
    }
}
