package com.web.back.controllers;

import com.web.back.filters.PermissionsFilter;
import com.web.back.mappers.ProfileDtoMapper;
import com.web.back.model.dto.ProfileDto;
import com.web.back.model.requests.ProfileRequest;
import com.web.back.model.responses.CustomResponse;
import com.web.back.services.JwtService;
import com.web.back.services.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/perfil/")
@RestController
public class ProfileController {
    private final JwtService jwtService;
    private final ProfileService profileService;

    public ProfileController(final JwtService jwtService, final ProfileService profileService) {
        this.jwtService = jwtService;
        this.profileService = profileService;
    }

    @GetMapping(value = "getAll")
    public ResponseEntity<CustomResponse<List<ProfileDto>>> getAll(@RequestHeader("Authorization") String bearerToken) {
        if (!PermissionsFilter.canRead(jwtService.getPermissionsFromToken(bearerToken))) {
            return ResponseEntity.ok(new CustomResponse<List<ProfileDto>>().forbidden());
        }

        return ResponseEntity.ok(new CustomResponse<List<ProfileDto>>().ok(profileService.getALl().stream()
                .map(ProfileDtoMapper::mapFrom).toList()));
    }

    @PostMapping(value = "register")
    public ResponseEntity<CustomResponse<ProfileDto>> register(@RequestHeader("Authorization") String bearerToken, @RequestBody ProfileRequest request) {
        if (!PermissionsFilter.canCreate(jwtService.getPermissionsFromToken(bearerToken))) {
            return ResponseEntity.ok(new CustomResponse<ProfileDto>().forbidden());
        }

        return ResponseEntity.ok(new CustomResponse<ProfileDto>().ok(
                ProfileDtoMapper.mapFrom(profileService.save(request))
        ));
    }

    @PutMapping(value = "update/{id}")
    public ResponseEntity<CustomResponse<ProfileDto>> update(@RequestHeader("Authorization") String bearerToken, @RequestBody ProfileRequest request, @PathVariable Integer id) {
        if (!PermissionsFilter.canEdit(jwtService.getPermissionsFromToken(bearerToken))) {
            return ResponseEntity.ok(new CustomResponse<ProfileDto>().forbidden());
        }

        return ResponseEntity.ok(new CustomResponse<ProfileDto>().ok(
                ProfileDtoMapper.mapFrom(profileService.update(id, request))
        ));
    }

    @DeleteMapping(value = "delete/{id}")
    public ResponseEntity<CustomResponse<Void>> delete(@RequestHeader("Authorization") String bearerToken, @PathVariable Integer id) {
        if (!PermissionsFilter.canDelete(jwtService.getPermissionsFromToken(bearerToken))) {
            return ResponseEntity.ok(new CustomResponse<Void>().forbidden());
        }

        profileService.delete(id);

        return ResponseEntity.ok(new CustomResponse<Void>().ok(null));
    }
}
