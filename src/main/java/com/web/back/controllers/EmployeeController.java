package com.web.back.controllers;

import com.web.back.filters.PermissionsFilter;
import com.web.back.model.dto.EvaluationDto;
import com.web.back.model.responses.CustomResponse;
import com.web.back.services.EmployeeService;
import com.web.back.services.JwtService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/empleado/")
@Tag(name = "Employee")
public class EmployeeController {
    private final EmployeeService employeeService;
    private final JwtService jwtService;

    public EmployeeController(EmployeeService employeeService, JwtService jwtService) {
        this.employeeService = employeeService;
        this.jwtService = jwtService;
    }

    @GetMapping(value = "getAll")
    public ResponseEntity<CustomResponse<List<EvaluationDto>>> getEmployeesEvaluations(@RequestHeader("Authorization") String bearerToken, String beginDate, String endDate, String sociedad, String areaNomina) {
        if (!PermissionsFilter.canRead(jwtService.getPermissionsFromToken(bearerToken))) {
            return ResponseEntity.ok(new CustomResponse<List<EvaluationDto>>().forbidden("No cuentas con los permisos para utilizar esta función"));
        }

        String username = jwtService.getUsernameFromToken(bearerToken);

        return ResponseEntity.ok(employeeService.getEmployeesByFilters(beginDate, endDate, sociedad, areaNomina, username));
    }

    @GetMapping(value = "getAll/sync")
    public ResponseEntity<CustomResponse<List<EvaluationDto>>> getEmployeesEvaluationsAndSync(@RequestHeader("Authorization") String bearerToken, String beginDate, String endDate, String sociedad, String areaNomina) {
        if (!PermissionsFilter.canCreate(jwtService.getPermissionsFromToken(bearerToken)) && !PermissionsFilter.canEdit(jwtService.getPermissionsFromToken(bearerToken))) {
            return ResponseEntity.ok(new CustomResponse<List<EvaluationDto>>().forbidden("No cuentas con los permisos para utilizar esta función"));
        }

        String username = jwtService.getUsernameFromToken(bearerToken);

        return ResponseEntity.ok(employeeService.getEmployeesByFiltersFromService(beginDate, endDate, sociedad, areaNomina, username));
    }
}
