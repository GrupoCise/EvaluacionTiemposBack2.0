package com.web.back.controllers;

import com.web.back.filters.PermissionsFilter;
import com.web.back.model.dto.EvaluationDto;
import com.web.back.model.requests.EvaluationRequest;
import com.web.back.model.responses.CustomResponse;
import com.web.back.services.SapEvaluationService;
import com.web.back.services.JwtService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@RequestMapping("/evaluation/")
@RestController
@Tag(name = "SAP Evaluations")
public class SapEvaluationController {
    private static final Logger logger = LoggerFactory.getLogger(SapEvaluationController.class);
    private final JwtService jwtService;
    private final SapEvaluationService sapEvaluationService;

    public SapEvaluationController(JwtService jwtService, SapEvaluationService sapEvaluationService) {
        this.jwtService = jwtService;
        this.sapEvaluationService = sapEvaluationService;
    }

    @PutMapping(value="evaluations")
    public ResponseEntity<CustomResponse<List<EvaluationDto>>> updateRegistros(@RequestBody EvaluationRequest request)
    {
        request.setUserName(jwtService.getCurrentUserName());

        return ResponseEntity.ok(sapEvaluationService.updateEvaluations(request));
    }

    @PostMapping(value="evaluations")
    public ResponseEntity<CustomResponse<Void>> sendApprovedEvaluationsToSap(@RequestBody EvaluationRequest request)
    {
        var permissions = jwtService.getCurrentUserPermissions();
        if (!PermissionsFilter.canEdit(permissions)) {
            return ResponseEntity.status(401).build();
        }

        try {
            var username = jwtService.getCurrentUserName();
            logger.info("Request to send approved evaluations to SAP from user: {}, for dates: {} - {}, sociedad: {}, areaNomina: {}",
                    username, request.getBeginDate(), request.getEndDate(), request.getSociedad(), request.getAreaNomina());

            sapEvaluationService.sendApprovedEvaluationsToSap(username, request.getBeginDate(), request.getEndDate(), request.getSociedad(), request.getAreaNomina());

            return ResponseEntity.ok(new CustomResponse<Void>().ok(null, "Evaluaciones enviadas exitosamente!"));
        }catch (Exception e) {
            String errorMessage;

            if (e instanceof HttpClientErrorException) {
                errorMessage = "Error al enviar las evaluaciones a SAP. Contacta al administrador!";
            } else {
                errorMessage = e.getMessage();
            }
            return ResponseEntity.ok(new CustomResponse<Void>().internalError(errorMessage));
        }
    }

    @GetMapping(value = "evaluations")
    public ResponseEntity<CustomResponse<List<EvaluationDto>>> getAllEmployeeEvaluations() {
        var permissions = jwtService.getCurrentUserPermissions();
        if (!PermissionsFilter.isSuperUser(permissions)) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(new CustomResponse<List<EvaluationDto>>().ok(sapEvaluationService.getAllEvaluations()));
    }

    @GetMapping(value = "evaluations/filtered")
    public ResponseEntity<CustomResponse<List<EvaluationDto>>> getAllEmployeeEvaluationsByFilters(String beginDate, String endDate, String sociedad, String areaNomina) {
        var permissions = jwtService.getCurrentUserPermissions();
        if (!PermissionsFilter.isSuperUser(permissions)) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(new CustomResponse<List<EvaluationDto>>().ok(sapEvaluationService.getAllEvaluationsByFilters(beginDate, endDate, sociedad, areaNomina)));
    }

    @DeleteMapping(value = "evaluations")
    public ResponseEntity<CustomResponse<Void>> deleteEvaluations(@RequestBody List<Integer> evaluationsToRemove) {
        var permissions = jwtService.getCurrentUserPermissions();
        if (!PermissionsFilter.isSuperUser(permissions)) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(new CustomResponse<Void>().ok(sapEvaluationService.deleteEvaluations(evaluationsToRemove)));
    }
}
