package com.web.back.controllers;

import com.web.back.filters.PermissionsFilter;
import com.web.back.model.dto.EvaluationDto;
import com.web.back.model.requests.EvaluationRequest;
import com.web.back.model.responses.CustomResponse;
import com.web.back.services.EvaluationService;
import com.web.back.services.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/evaluation/")
@RestController
public class EvaluationController {
    private final JwtService jwtService;
    private final EvaluationService evaluationService;

    public EvaluationController(JwtService jwtService, EvaluationService evaluationService) {
        this.jwtService = jwtService;
        this.evaluationService = evaluationService;
    }

    @PutMapping(value="evaluations")
    public ResponseEntity<CustomResponse<List<EvaluationDto>>> updateRegistros(@RequestHeader("Authorization") String bearerToken, @RequestBody EvaluationRequest request)
    {
        request.setUserName(jwtService.getUsernameFromToken(bearerToken));

        return ResponseEntity.ok(evaluationService.updateEvaluations(request));
    }

    @PostMapping(value="evaluations")
    public ResponseEntity<CustomResponse<Void>> sendApprovedEvaluationsToSap(@RequestHeader("Authorization") String bearerToken, @RequestBody EvaluationRequest request)
    {
        if (!PermissionsFilter.canEdit(jwtService.getPermissionsFromToken(bearerToken))) {
            return ResponseEntity.ok(new CustomResponse<Void>().forbidden("No cuentas con los permisos para utilizar esta función"));
        }

        var response = evaluationService.sendApprovedEvaluationsToSap(request.getBeginDate(), request.getEndDate(), request.getSociedad(), request.getAreaNomina());

        if (response != null && response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.ok(new CustomResponse<Void>().ok(null, "Evaluaciones enviadas exitosamente!"));
        }

        return ResponseEntity.ok(new CustomResponse<Void>().internalError("Algo fallo al enviar las evaluaciones. Contacta al administrador!"));
    }

    @GetMapping(value = "evaluations")
    public ResponseEntity<CustomResponse<List<EvaluationDto>>> getAllEmployeeEvaluations(@RequestHeader("Authorization") String bearerToken) {
        if (!PermissionsFilter.isSuperUser(jwtService.getPermissionsFromToken(bearerToken))) {
            return ResponseEntity.ok(new CustomResponse<List<EvaluationDto>>().forbidden("No cuentas con los permisos para utilizar esta función"));
        }

        return ResponseEntity.ok(new CustomResponse<List<EvaluationDto>>().ok(evaluationService.getAllEvaluations()));
    }

    @DeleteMapping(value = "evaluations")
    public ResponseEntity<CustomResponse<Void>> deleteEvaluations(@RequestHeader("Authorization") String bearerToken, @RequestBody List<Integer> evaluationsToRemove) {
        if (!PermissionsFilter.isSuperUser(jwtService.getPermissionsFromToken(bearerToken))) {
            return ResponseEntity.ok(new CustomResponse<Void>().forbidden("No cuentas con los permisos para utilizar esta función"));
        }

        return ResponseEntity.ok(new CustomResponse<Void>().ok(evaluationService.deleteEvaluations(evaluationsToRemove)));
    }
}
