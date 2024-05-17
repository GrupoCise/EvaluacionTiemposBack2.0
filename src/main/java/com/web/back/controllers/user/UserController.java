package com.web.back.controllers.user;

import com.web.back.filters.PermissionsFilter;
import com.web.back.model.entities.User;
import com.web.back.model.responses.CustomResponse;
import com.web.back.model.requests.user.RegisterUserRequest;
import com.web.back.model.requests.user.UserUpdateRequest;
import com.web.back.services.jwt.JwtService;
import com.web.back.services.user.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/su/")
@Tag(name = "User")
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;

    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping(value = "register")
    public ResponseEntity<CustomResponse<User>> register(@RequestHeader("Authorization") String bearerToken, @RequestBody RegisterUserRequest request) {
        if (!PermissionsFilter.canCreate(jwtService.getPermissionsFromToken(bearerToken))) {
            return ResponseEntity.ok(new CustomResponse<User>().forbidden());
        }

        return ResponseEntity.ok(userService.register(request));
    }

    @PutMapping("update/{id}")
    public ResponseEntity<CustomResponse<User>> update(@RequestHeader("Authorization") String bearerToken, @PathVariable String id, @RequestBody UserUpdateRequest request) {
        if (!PermissionsFilter.canEdit(jwtService.getPermissionsFromToken(bearerToken))) {
            return ResponseEntity.ok(new CustomResponse<User>().forbidden());
        }

        return ResponseEntity.ok(userService.update(id, request));
    }

    @PutMapping("disable/{id}")
    public ResponseEntity<CustomResponse<User>> disable(@RequestHeader("Authorization") String bearerToken, @PathVariable String id) {
        if (!PermissionsFilter.canEdit(jwtService.getPermissionsFromToken(bearerToken))) {
            return ResponseEntity.ok(new CustomResponse<User>().forbidden());
        }
        return ResponseEntity.ok(userService.updateStatus(id, false));
    }

    @PutMapping("enable/{id}/")
    public ResponseEntity<CustomResponse<User>> enable(@RequestHeader("Authorization") String bearerToken, @PathVariable String id) {
        if (!PermissionsFilter.canEdit(jwtService.getPermissionsFromToken(bearerToken))) {
            return ResponseEntity.ok(new CustomResponse<User>().forbidden());
        }
        return ResponseEntity.ok(userService.updateStatus(id, true));
    }

    @GetMapping(value = "getAll")
    public ResponseEntity<CustomResponse<List<User>>> getAll(@RequestHeader("Authorization") String bearerToken, Boolean isActive) {
        if (!PermissionsFilter.canRead(jwtService.getPermissionsFromToken(bearerToken))) {
            return ResponseEntity.ok(new CustomResponse<List<User>>().forbidden());
        }
        return ResponseEntity.ok(userService.getUsersByStatus(isActive));
    }

    @GetMapping("getOne/{id}")
    public ResponseEntity<CustomResponse<User>> getOne(@RequestHeader("Authorization") String bearerToken, @PathVariable String id) {
        if (!PermissionsFilter.canRead(jwtService.getPermissionsFromToken(bearerToken))) {
            return ResponseEntity.ok(new CustomResponse<User>().forbidden());
        }

        return ResponseEntity.ok(new CustomResponse<User>().ok(
                userService.getByUserName(id).orElse(null
                )));
    }
}
