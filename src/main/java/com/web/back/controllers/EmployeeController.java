package com.web.back.controllers;

import com.web.back.filters.PermissionsFilter;
import com.web.back.model.dto.EmployeeDto;
import com.web.back.model.requests.EmployeeRequest;
import com.web.back.services.EmployeeService;
import com.web.back.services.JwtService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee")
@Tag(name = "Employee")
public class EmployeeController {
    private final JwtService jwtService;
    private final EmployeeService employeeService;

    public EmployeeController(JwtService jwtService, EmployeeService employeeService) {
        this.jwtService = jwtService;
        this.employeeService = employeeService;
    }

    @GetMapping
    public ResponseEntity<List<EmployeeDto>> getAll() {
        var permissions = jwtService.getCurrentUserPermissions();
        if (!PermissionsFilter.canRead(permissions)) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(employeeService.getAll());
    }

    @GetMapping("{id}")
    public ResponseEntity<EmployeeDto> getByEmployeeId(@PathVariable String id) {
        var permissions = jwtService.getCurrentUserPermissions();
        if (!PermissionsFilter.canRead(permissions)) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @GetMapping("number/{employeeNumber}")
    public ResponseEntity<EmployeeDto> getByEmployeeNumber(@PathVariable String employeeNumber) {
        var permissions = jwtService.getCurrentUserPermissions();
        if (!PermissionsFilter.canRead(permissions)) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(employeeService.getEmployeeByEmployeeNumber(employeeNumber));
    }

    @PostMapping("bulk")
    public ResponseEntity<List<EmployeeDto>> bulkInsert(@RequestBody List<EmployeeRequest> employees) {
        var permissions = jwtService.getCurrentUserPermissions();
        if (!PermissionsFilter.canCreate(permissions) &&
                !PermissionsFilter.canEdit(permissions)) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(employeeService.upsertEmployees(employees));
    }

    @PostMapping
    public ResponseEntity<EmployeeDto> add(@RequestBody EmployeeRequest request) {
        var permissions = jwtService.getCurrentUserPermissions();
        if (!PermissionsFilter.canCreate(permissions)) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(employeeService.upsertEmployee(request));
    }

    @PutMapping("{id}")
    public ResponseEntity<EmployeeDto> add(
            @PathVariable String id,
            @RequestBody EmployeeRequest request) {
        var permissions = jwtService.getCurrentUserPermissions();
        if (!PermissionsFilter.canEdit(permissions)) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(employeeService.update(id, request));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteRelation(@PathVariable String id) {
        var permissions = jwtService.getCurrentUserPermissions();
        if (!PermissionsFilter.canDelete(permissions)) {
            return ResponseEntity.status(401).build();
        }

        employeeService.deleteEmployeeById(id);
        return ResponseEntity.noContent().build();
    }
}
