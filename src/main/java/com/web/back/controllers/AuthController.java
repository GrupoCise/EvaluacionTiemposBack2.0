package com.web.back.controllers;

import com.web.back.model.requests.LoginRequest;
import com.web.back.model.responses.AuthResponse;
import com.web.back.model.responses.CustomResponse;
import com.web.back.services.AuthService;
import com.web.back.services.ImpersonateService;
import com.web.back.services.JwtService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth")
public class AuthController {
    private final AuthService authService;
    private final ImpersonateService impersonateService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, ImpersonateService impersonateService, JwtService jwtService) {
        this.authService = authService;
        this.impersonateService = impersonateService;
        this.jwtService = jwtService;
    }

    @PostMapping(value = "/login")
    public ResponseEntity<CustomResponse<AuthResponse>> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping(value = "/impersonate")
    public ResponseEntity<CustomResponse<AuthResponse>> getAuthForImpersonation(String targetUserName) {
        var userName = jwtService.getCurrentUserName();
        var impersonations = impersonateService.getByUser(userName);

        if(impersonations.isError() && impersonations.getData().stream().noneMatch(impersonation -> impersonation.targetUserName().equals(targetUserName))) {
            return ResponseEntity.ok(new CustomResponse<AuthResponse>().badRequest("El usuario actual no puede actuar como el usuario deseado"));
        }

        return ResponseEntity.ok(authService.getTokenForUser(userName, targetUserName));
    }


    @PostMapping("/changePassword")
    public CustomResponse<String> changePassword(@RequestBody LoginRequest request) {
        return authService.changePassword(request);
    }
}
