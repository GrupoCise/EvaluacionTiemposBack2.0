package com.web.back.services.employee;

import com.web.back.clients.ZWSHREvaluacioService;
import com.web.back.model.responses.CustomResponse;
import com.web.back.model.responses.EmployeeApiResponse;
import com.web.back.services.jwt.JwtService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Transactional
public class EmployeeService {
    private final ZWSHREvaluacioService zwshrEvaluacioService;
    private final JwtService jwtService;

    public EmployeeService(ZWSHREvaluacioService zwshrEvaluacioService,
                           JwtService jwtService) {
        this.zwshrEvaluacioService = zwshrEvaluacioService;
        this.jwtService = jwtService;
    }

    @Transactional(rollbackFor = {Exception.class})
    public Mono<CustomResponse<List<EmployeeApiResponse>>> getEmployeesByFilters(String beginDate, String endDate, String sociedad, String areaNomina, String token) {
        String username = jwtService.getUsernameFromToken(token);

        return zwshrEvaluacioService.getEmployees(beginDate, endDate, sociedad, areaNomina, username)
                .map(employees -> new CustomResponse<List<EmployeeApiResponse>>().ok(employees));
    }
}
