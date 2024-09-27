package com.web.back.controllers;

import com.web.back.filters.PermissionsFilter;
import com.web.back.model.requests.ImpersonateRequest;
import com.web.back.model.responses.CustomResponse;
import com.web.back.model.responses.ImpersonateResponse;
import com.web.back.services.ImpersonateService;
import com.web.back.services.JwtService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/impersonate/")
@Tag(name = "Actua Como")
public class ImpersonateController {
    private final ImpersonateService impersonateService;
    private final JwtService jwtService;

    public ImpersonateController(ImpersonateService impersonateService, JwtService jwtService) {
        this.impersonateService = impersonateService;
        this.jwtService = jwtService;
    }

    @PostMapping("/insert")
    public ResponseEntity<CustomResponse<ImpersonateResponse>> insert(@RequestHeader("Authorization") String bearerToken, @RequestBody ImpersonateRequest request) {
        if (!PermissionsFilter.canCreate(jwtService.getPermissionsFromToken(bearerToken))) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(impersonateService.saveImpersonation(request));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<CustomResponse<Boolean>> delete(@RequestHeader("Authorization") String bearerToken, @PathVariable Integer id) {
        if (!PermissionsFilter.canDelete(jwtService.getPermissionsFromToken(bearerToken))) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(impersonateService.deleteImpersonation(id));
    }

    @GetMapping("/getAll")
    public ResponseEntity<CustomResponse<List<ImpersonateResponse>>> getAll(@RequestHeader("Authorization") String bearerToken) {
        if (!PermissionsFilter.canRead(jwtService.getPermissionsFromToken(bearerToken))) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(impersonateService.getImpersonations());
    }

    @GetMapping("/getByUser")
    public ResponseEntity<CustomResponse<List<ImpersonateResponse>>> getByUser(@RequestHeader("Authorization") String bearerToken) {
        String username = jwtService.getUsernameFromToken(bearerToken);
        return ResponseEntity.ok(impersonateService.getByUser(username));
    }
}
