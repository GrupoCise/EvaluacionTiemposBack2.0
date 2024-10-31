package com.web.back.controllers;

import com.web.back.filters.PermissionsFilter;
import com.web.back.model.dto.ChangeLogDto;
import com.web.back.model.requests.ChangeLogRequest;
import com.web.back.model.responses.CustomResponse;
import com.web.back.services.JwtService;
import com.web.back.services.ChangeLogService;
import com.web.back.utils.DateUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/log")
@Tag(name = "Change Log")
public class ChangeLogController {
    private final JwtService jwtService;
    private final ChangeLogService changeLogService;

    public ChangeLogController(JwtService jwtService, ChangeLogService changeLogService) {
        this.jwtService = jwtService;
        this.changeLogService = changeLogService;
    }

    @PostMapping(value = "/getAll")
    public Mono<CustomResponse<List<ChangeLogDto>>> getAll(@RequestHeader("Authorization") String bearerToken, @RequestBody ChangeLogRequest request) {
        if (!PermissionsFilter.canRead(jwtService.getPermissionsFromToken(bearerToken))) {
            return Mono.just(new CustomResponse<List<ChangeLogDto>>().forbidden());
        }

        String username = jwtService.getUsernameFromToken(bearerToken);

        return Mono.just(changeLogService.getLogs(request.beginDate(), request.endDate(), request.sociedad(), request.areaNomina(), username));
    }

    @PostMapping(value = "/logToExcel")
    public ResponseEntity<byte[]> logToExcel(@RequestHeader("Authorization") String bearerToken, @RequestBody ChangeLogRequest request) {
        if (!PermissionsFilter.canRead(jwtService.getPermissionsFromToken(bearerToken))) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        String username = jwtService.getUsernameFromToken(bearerToken);

        try {
            final byte[] data = changeLogService.getLogsXlsData(request, username);

            HttpHeaders header = new HttpHeaders();
            header.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8"));
            header.setContentLength(data.length);
            header.setContentDispositionFormData("filename", getFileName(request));

            return new ResponseEntity<>(data, header, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String getFileName(ChangeLogRequest request) {
        return String.format("%s-%s-%s-%s.xlsx",
                DateUtil.clearSymbols(request.beginDate()),
                DateUtil.clearSymbols(request.endDate()),
                request.sociedad().replace(" ", ""),
                request.areaNomina().replace(" ", ""));
    }
}
