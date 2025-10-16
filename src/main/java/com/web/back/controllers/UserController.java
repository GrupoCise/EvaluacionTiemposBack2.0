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
    public CustomResponse<UserDto> register(@RequestBody RegisterUserRequest request) {
        var permissions = jwtService.getCurrentUserPermissions();
        if (!PermissionsFilter.canCreate(permissions)) {
            return new CustomResponse<UserDto>().forbidden();
        }

        var user = userService.register(request);

        return new CustomResponse<UserDto>().ok(UserDtoMapper.mapFrom(user));
    }

    @PutMapping("update/{id}")
    public CustomResponse<UserDto> update(@PathVariable Integer id, @RequestBody UserUpdateRequest request) {
        var permissions = jwtService.getCurrentUserPermissions();
        if (!PermissionsFilter.canEdit(permissions)) {
            return new CustomResponse<UserDto>().forbidden();
        }

        try {
            var user = userService.update(id, request);
            return new CustomResponse<UserDto>().ok(UserDtoMapper.mapFrom(user));
        }catch (Exception ex){
            return new CustomResponse<UserDto>().badRequest(ex.getMessage());
        }
    }

    @PutMapping("disable/{userName}")
    public CustomResponse<UserDto> disable(@PathVariable String userName) {
        var permissions = jwtService.getCurrentUserPermissions();
        if (!PermissionsFilter.canEdit(permissions)) {
            return new CustomResponse<UserDto>().forbidden();
        }

        var user = userService.updateStatus(userName, false);
        return new CustomResponse<UserDto>().ok(UserDtoMapper.mapFrom(user));
    }

    @PutMapping("enable/{userName}")
    public CustomResponse<UserDto> enable(@PathVariable String userName) {
        var permissions = jwtService.getCurrentUserPermissions();
        if (!PermissionsFilter.canEdit(permissions)) {
            return new CustomResponse<UserDto>().forbidden();
        }

        var user = userService.updateStatus(userName, true);
        return new CustomResponse<UserDto>().ok(UserDtoMapper.mapFrom(user));
    }

    @GetMapping(value = "getAll")
    public CustomResponse<List<UserDto>> getAll() {
        var permissions = jwtService.getCurrentUserPermissions();
        if (!PermissionsFilter.canRead(permissions)) {
            return new CustomResponse<List<UserDto>>().forbidden();
        }
        var users = userService.getAll();
        var usersDto = users.stream().map(UserDtoMapper::mapFrom).toList();
        return new CustomResponse<List<UserDto>>().ok(usersDto);
    }

    @GetMapping("getOne/{userName}")
    public CustomResponse<UserDto> getOne(@PathVariable String userName) {
        var permissions = jwtService.getCurrentUserPermissions();
        if (!PermissionsFilter.canRead(permissions)) {
            return new CustomResponse<UserDto>().forbidden();
        }

        return new CustomResponse<UserDto>().ok(
                UserDtoMapper.mapFrom(userService.getByUserName(userName)));
    }

    @DeleteMapping("delete/{id}")
    public CustomResponse<String> enable(@PathVariable Integer id) {
        var permissions = jwtService.getCurrentUserPermissions();
        if (!PermissionsFilter.canDelete(permissions)) {
            return new CustomResponse<String>().forbidden();
        }

        userService.deleteUser(id);
        return new CustomResponse<String>().ok("Eliminado!");
    }
}
