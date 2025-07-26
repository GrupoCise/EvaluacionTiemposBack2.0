package com.web.back.controllers;

import com.web.back.filters.PermissionsFilter;
import com.web.back.model.dto.ABCEmployeeDTO;
import com.web.back.model.enumerators.PermissionsEnum;
import com.web.back.model.requests.EntryExitMarkRequest;
import com.web.back.model.responses.CustomResponse;
import com.web.back.services.EmployeeService;
import com.web.back.services.EntryAndExitService;
import com.web.back.services.JwtService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/entrada-salida")
@Tag(name = "Entrada y salida")
public class EntryAndExitController {
    private static final Logger logger = LoggerFactory.getLogger(EntryAndExitController.class);

    private final EntryAndExitService entryAndExitService;
    private final EmployeeService employeeService;
    private final JwtService jwtService;

    public EntryAndExitController(EntryAndExitService entryAndExitService, EmployeeService employeeService, JwtService jwtService) {
        this.entryAndExitService = entryAndExitService;
        this.employeeService = employeeService;
        this.jwtService = jwtService;
    }

    @PostMapping
    public CustomResponse<Void> registerEntryAndExitMarks(@RequestHeader("Authorization") String bearerToken, @RequestBody List<EntryExitMarkRequest> entryAndExitMarks) {
        logger.info("POST /entrada-salida called with body: {}", entryAndExitMarks);
        if (!PermissionsFilter.hasPermission(jwtService.getPermissionsFromToken(bearerToken), PermissionsEnum.CARGAR_MARCAJES)) {
            logger.error("No tienes permisos para realizar esta acción");
            return new CustomResponse<Void>().forbidden("No tienes permisos para realizar esta acción");
        }

        return entryAndExitService.registerEntryAndExitMarks(entryAndExitMarks);
    }

    @GetMapping(value = "/ABCEmpleado")
    public CustomResponse<List<ABCEmployeeDTO>> getABCEmployees(@RequestHeader("Authorization") String bearerToken,
                                                                                String carga, String beginDate, String endDate) {
        if (!PermissionsFilter.hasPermission(jwtService.getPermissionsFromToken(bearerToken), PermissionsEnum.CARGAR_MARCAJES)) {
            return new CustomResponse<List<ABCEmployeeDTO>>().forbidden("No tienes permisos para realizar esta acción");
        }

        return new CustomResponse<List<ABCEmployeeDTO>>().ok(employeeService.getABCEmployees(carga, beginDate, endDate));
    }
}
