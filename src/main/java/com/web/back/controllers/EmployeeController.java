package com.web.back.controllers;

import com.web.back.filters.PermissionsFilter;
import com.web.back.model.dto.EvaluationsDataDto;
import com.web.back.model.dto.GetEmployeesRequestDto;
import com.web.back.model.dto.RegistroTiemposDto;
import com.web.back.model.requests.GenerateXlsRequest;
import com.web.back.model.responses.CustomResponse;
import com.web.back.services.EmployeeService;
import com.web.back.services.JwtService;
import com.web.back.utils.DateUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping(value = "getAll")
    public ResponseEntity<CustomResponse<EvaluationsDataDto>> getEmployeesEvaluations(@RequestHeader("Authorization") String bearerToken, @RequestBody GetEmployeesRequestDto requestDto) {
        if (!PermissionsFilter.canRead(jwtService.getPermissionsFromToken(bearerToken))) {
            return ResponseEntity.ok(new CustomResponse<EvaluationsDataDto>().forbidden("No cuentas con los permisos para utilizar esta función"));
        }

        String username = jwtService.getUsernameFromToken(bearerToken);

        return ResponseEntity.ok(employeeService.getEmployeesByFilters(requestDto.beginDate(), requestDto.endDate(), requestDto.sociedad(), requestDto.areaNomina(), username, requestDto.extraEmployeesData()));
    }

    @PostMapping(value = "getAll/sync")
    public ResponseEntity<CustomResponse<EvaluationsDataDto>> getEmployeesEvaluationsAndSync(@RequestHeader("Authorization") String bearerToken, @RequestBody GetEmployeesRequestDto requestDto) {
        if (!PermissionsFilter.canCreate(jwtService.getPermissionsFromToken(bearerToken)) && !PermissionsFilter.canEdit(jwtService.getPermissionsFromToken(bearerToken))) {
            return ResponseEntity.ok(new CustomResponse<EvaluationsDataDto>().forbidden("No cuentas con los permisos para utilizar esta función"));
        }

        String username = jwtService.getUsernameFromToken(bearerToken);

        return ResponseEntity.ok(employeeService.getEmployeesCleanSync(requestDto.beginDate(), requestDto.endDate(), requestDto.sociedad(), requestDto.areaNomina(), username, requestDto.extraEmployeesData()));
    }

    @GetMapping(value = "getTimesheetInfo")
    public ResponseEntity<CustomResponse<List<RegistroTiemposDto>>> getTimesheetInfo(@RequestHeader("Authorization") String bearerToken, String beginDate, String endDate) {
        if (!PermissionsFilter.canCreate(jwtService.getPermissionsFromToken(bearerToken)) && !PermissionsFilter.canRead(jwtService.getPermissionsFromToken(bearerToken))) {
            return ResponseEntity.ok(new CustomResponse<List<RegistroTiemposDto>>().forbidden("No cuentas con los permisos para utilizar esta función"));
        }

        String username = jwtService.getUsernameFromToken(bearerToken);

        return ResponseEntity.ok(employeeService.getRegistroTiempos(beginDate, endDate, username));
    }

    @PostMapping(value = "timesheets")
    public ResponseEntity<CustomResponse<Void>> sendTimeSheetChanges(@RequestHeader("Authorization") String bearerToken, @RequestBody List<RegistroTiemposDto> registroTiemposDtos) {
        if (!PermissionsFilter.canEdit(jwtService.getPermissionsFromToken(bearerToken))) {
            return ResponseEntity.ok(new CustomResponse<Void>().forbidden("No cuentas con los permisos para utilizar esta función"));
        }

        var response = employeeService.sendTimeSheetChanges(registroTiemposDtos);

        if (response != null && response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.ok(new CustomResponse<Void>().ok(null, "Cambios aplicados exitosamente!"));
        }

        return ResponseEntity.ok(new CustomResponse<Void>().internalError("Algo fallo al enviar los cambios. Contacta al administrador!"));
    }

    @PostMapping(value = "/incidencesReportToXls")
    public ResponseEntity<byte[]> logToExcel(@RequestHeader("Authorization") String bearerToken, @RequestBody GenerateXlsRequest request) {
        try {
            final byte[] data = employeeService.getLogsXlsData(request);

            HttpHeaders header = new HttpHeaders();
            header.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8"));
            header.setContentLength(data.length);
            header.setContentDispositionFormData("filename", getFileName(request));

            return new ResponseEntity<>(data, header, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String getFileName(GenerateXlsRequest request) {
        return String.format("%s-%s-%s-%s.xlsx",
                DateUtil.clearSymbols(request.beginDate()),
                DateUtil.clearSymbols(request.endDate()),
                request.sociedad().replace(" ", ""),
                request.areaNomina().replace(" ", ""));
    }
}
