package com.web.back.controllers;

import com.web.back.model.dto.TimeRecordDto;
import com.web.back.model.requests.TimeRecordRequest;
import com.web.back.services.TimeRecordService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/time-record")
@RestController
@Tag(name = "Time Record")
public class TimeRecordController {
    private final TimeRecordService timeRecordService;

    public TimeRecordController(TimeRecordService timeRecordService) {
        this.timeRecordService = timeRecordService;
    }

    @GetMapping
    public ResponseEntity<List<TimeRecordDto>> readAll() {
        return ResponseEntity.ok(timeRecordService.readAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TimeRecordDto> readById(@PathVariable String id) {
        var dto = timeRecordService.readById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<TimeRecordDto> create(@RequestBody TimeRecordRequest request) {
        var dto = timeRecordService.create(request);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TimeRecordDto> update(@PathVariable String id, @RequestBody TimeRecordRequest request) {
        var dto = timeRecordService.update(id, request);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable String id) {
        timeRecordService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
