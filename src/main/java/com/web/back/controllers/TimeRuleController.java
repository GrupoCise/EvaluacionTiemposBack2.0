package com.web.back.controllers;

import com.web.back.model.dto.TimeRuleDto;
import com.web.back.model.requests.TimeRuleRequest;
import com.web.back.services.TimeRuleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/time-rule")
@RestController
@Tag(name = "Time Rule")
public class TimeRuleController {
    private final TimeRuleService timeRuleService;

    public TimeRuleController(TimeRuleService timeRuleService) {
        this.timeRuleService = timeRuleService;
    }

    @GetMapping
    public ResponseEntity<List<TimeRuleDto>> readAll() {
        return ResponseEntity.ok(timeRuleService.readAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TimeRuleDto> readById(@PathVariable String id) {
        var dto = timeRuleService.readById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<TimeRuleDto> create(@RequestBody TimeRuleRequest request) {
        var dto = timeRuleService.create(request);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TimeRuleDto> update(@PathVariable String id, @RequestBody TimeRuleRequest request) {
        var dto = timeRuleService.update(id, request);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable String id) {
        timeRuleService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
