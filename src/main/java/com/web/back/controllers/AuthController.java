package com.web.back.controllers;

import com.web.back.model.responses.AuthResponse;
import com.web.back.model.requests.LoginRequest;
import com.web.back.model.responses.CustomResponse;
import com.web.back.services.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(value = "/login")
    public ResponseEntity<CustomResponse<AuthResponse>> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }


    @PostMapping("/changePassword")
    public Mono<CustomResponse<String>> changePassword(@RequestBody LoginRequest request) {
        return authService.changePassword(request);
    }
}
