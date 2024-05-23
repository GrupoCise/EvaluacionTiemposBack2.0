package com.web.back.controllers;

import com.web.back.filters.PermissionsFilter;
import com.web.back.mappers.UserDtoMapper;
import com.web.back.model.dto.UserDto;
import com.web.back.model.responses.CustomResponse;
import com.web.back.model.requests.RegisterUserRequest;
import com.web.back.model.requests.UserUpdateRequest;
import com.web.back.services.JwtService;
import com.web.back.services.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

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
    public Mono<CustomResponse<UserDto>> register(@RequestHeader("Authorization") String bearerToken, @RequestBody RegisterUserRequest request) {
        if (!PermissionsFilter.canCreate(jwtService.getPermissionsFromToken(bearerToken))) {
            return Mono.just(new CustomResponse<UserDto>().forbidden());
        }

        return userService.register(request)
                .map(user -> new CustomResponse<UserDto>().ok(UserDtoMapper.mapFrom(user)))
                .doOnError(ex -> new CustomResponse<UserDto>().badRequest(ex.getMessage()));
    }

    @PutMapping("update/{id}")
    public Mono<CustomResponse<UserDto>> update(@RequestHeader("Authorization") String bearerToken, @PathVariable Integer id, @RequestBody UserUpdateRequest request) {
        if (!PermissionsFilter.canEdit(jwtService.getPermissionsFromToken(bearerToken))) {
            return Mono.just(new CustomResponse<UserDto>().forbidden());
        }

        return userService.update(id, request)
                .map(user -> new CustomResponse<UserDto>().ok(UserDtoMapper.mapFrom(user)))
                .doOnError(ex -> new CustomResponse<UserDto>().badRequest(ex.getMessage()));
    }

    @PutMapping("disable/{userName}")
    public Mono<CustomResponse<UserDto>> disable(@RequestHeader("Authorization") String bearerToken, @PathVariable String userName) {
        if (!PermissionsFilter.canEdit(jwtService.getPermissionsFromToken(bearerToken))) {
            return Mono.just(new CustomResponse<UserDto>().forbidden());
        }

        return userService.updateStatus(userName, false)
                .map(user -> new CustomResponse<UserDto>().ok(UserDtoMapper.mapFrom(user)));
    }

    @PutMapping("enable/{userName}")
    public Mono<CustomResponse<UserDto>> enable(@RequestHeader("Authorization") String bearerToken, @PathVariable String userName) {
        if (!PermissionsFilter.canEdit(jwtService.getPermissionsFromToken(bearerToken))) {
            return Mono.just(new CustomResponse<UserDto>().forbidden());
        }

        return userService.updateStatus(userName, true)
                .map(user -> new CustomResponse<UserDto>().ok(UserDtoMapper.mapFrom(user)));
    }

    @GetMapping(value = "getAll")
    public Mono<CustomResponse<List<UserDto>>> getAll(@RequestHeader("Authorization") String bearerToken) {
        if (!PermissionsFilter.canRead(jwtService.getPermissionsFromToken(bearerToken))) {
            return Mono.just(new CustomResponse<List<UserDto>>().forbidden());
        }
        return userService.getAll()
                .map(users -> users.stream().map(UserDtoMapper::mapFrom).toList())
                .map(usersDto -> new CustomResponse<List<UserDto>>().ok(usersDto));
    }

    @GetMapping("getOne/{userName}")
    public Mono<CustomResponse<UserDto>> getOne(@RequestHeader("Authorization") String bearerToken, @PathVariable String userName) {
        if (!PermissionsFilter.canRead(jwtService.getPermissionsFromToken(bearerToken))) {
            return Mono.just(new CustomResponse<UserDto>().forbidden());
        }

        return Mono.just(new CustomResponse<UserDto>().ok(
                UserDtoMapper.mapFrom(userService.getByUserName(userName))));
    }

    @DeleteMapping("delete/{id}")
    public Mono<CustomResponse<String>> enable(@RequestHeader("Authorization") String bearerToken, @PathVariable Integer id) {
        if (!PermissionsFilter.canDelete(jwtService.getPermissionsFromToken(bearerToken))) {
            return Mono.just(new CustomResponse<String>().forbidden());
        }

        return userService.deleteUser(id)
                .map(user -> new CustomResponse<String>().ok("Eliminado!"));
    }
}
