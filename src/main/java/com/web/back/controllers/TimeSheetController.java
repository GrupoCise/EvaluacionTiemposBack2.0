package com.web.back.controllers;

import com.web.back.filters.PermissionsFilter;
import com.web.back.model.enumerators.PermissionsEnum;
import com.web.back.model.requests.RegistroHorariosRequest;
import com.web.back.model.responses.CustomResponse;
import com.web.back.model.responses.RegistroHorariosResponse;
import com.web.back.services.JwtService;
import com.web.back.services.TimeSheetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/timesheet/")
@RestController
public class TimeSheetController {
    private final TimeSheetService timeSheetService;
    private final JwtService jwtService;

    public TimeSheetController(TimeSheetService timeSheetService, JwtService jwtService) {
        this.timeSheetService = timeSheetService;
        this.jwtService = jwtService;
    }

    @PostMapping(value = "register/list")
    public ResponseEntity<CustomResponse<List<RegistroHorariosResponse>>> register(@RequestHeader("Authorization") String bearerToken, @RequestBody List<RegistroHorariosRequest> registroHorariosRequests) {
        if (!PermissionsFilter.hasPermission(jwtService.getPermissionsFromToken(bearerToken), PermissionsEnum.REGISTER_TIMESHEETS)) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(new CustomResponse<List<RegistroHorariosResponse>>().ok(
                timeSheetService.registerTimeSheets(registroHorariosRequests)
        ));
    }
}
