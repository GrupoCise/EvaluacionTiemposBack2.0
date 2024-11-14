package com.web.back.controllers;

import com.web.back.filters.PermissionsFilter;
import com.web.back.model.enumerators.PermissionsEnum;
import com.web.back.model.requests.EntryExitMarkRequest;
import com.web.back.model.responses.CustomResponse;
import com.web.back.services.EntryAndExitService;
import com.web.back.services.JwtService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/entrada-salida")
@Tag(name = "Entrada y salida")
public class EntryAndExitController {
    private final EntryAndExitService entryAndExitService;
    private final JwtService jwtService;

    public EntryAndExitController(EntryAndExitService entryAndExitService, JwtService jwtService) {
        this.entryAndExitService = entryAndExitService;
        this.jwtService = jwtService;
    }

    @PostMapping
    public CustomResponse<Void> getEmployeesEvaluations(@RequestHeader("Authorization") String bearerToken, @RequestBody List<EntryExitMarkRequest> entryAndExitMarks) {
        if (!PermissionsFilter.hasPermission(jwtService.getPermissionsFromToken(bearerToken), PermissionsEnum.CARGAR_MARCAJES)) {
            return new CustomResponse<Void>().forbidden("No tienes permisos para realizar esta acción");
        }

        return entryAndExitService.registerEntryAndExitMarks(entryAndExitMarks);
    }
}
