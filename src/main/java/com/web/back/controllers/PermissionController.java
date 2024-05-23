package com.web.back.controllers;

import com.web.back.filters.PermissionsFilter;
import com.web.back.mappers.PermissionDtoMapper;
import com.web.back.model.dto.PermissionDto;
import com.web.back.model.responses.CustomResponse;
import com.web.back.repositories.PermissionRepository;
import com.web.back.services.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/permission/")
@RestController
public class PermissionController {
    private final JwtService jwtService;
    private final PermissionRepository permissionRepository;

    public PermissionController(JwtService jwtService, PermissionRepository permissionRepository) {
        this.jwtService = jwtService;
        this.permissionRepository = permissionRepository;
    }

    @GetMapping(value = "getAll")
    public ResponseEntity<CustomResponse<List<PermissionDto>>> getAll(@RequestHeader("Authorization") String bearerToken) {
        if (!PermissionsFilter.canRead(jwtService.getPermissionsFromToken(bearerToken))) {
            return ResponseEntity.ok(new CustomResponse<List<PermissionDto>>().forbidden());
        }

        return ResponseEntity.ok(new CustomResponse<List<PermissionDto>>().ok(
                permissionRepository.findAll().stream().map(PermissionDtoMapper::mapFrom).toList()));
    }
}
