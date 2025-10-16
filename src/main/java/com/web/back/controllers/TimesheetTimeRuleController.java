package com.web.back.controllers;

import com.web.back.model.dto.TimesheetTimeRuleDto;
import com.web.back.model.requests.TimesheetTimeRuleRequest;
import com.web.back.services.TimesheetTimeRuleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/timesheet-time-rules")
@RestController
@Tag(name = "Timesheet Time Rule")
public class TimesheetTimeRuleController {
    private final TimesheetTimeRuleService service;

    public TimesheetTimeRuleController(TimesheetTimeRuleService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<List<TimesheetTimeRuleDto>> create(@RequestBody TimesheetTimeRuleRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @PutMapping
    public ResponseEntity<List<TimesheetTimeRuleDto>> update(@RequestBody TimesheetTimeRuleRequest request) {
        return ResponseEntity.ok(service.update(request));
    }

    @GetMapping("/by-timesheet/{timesheetId}")
    public ResponseEntity<List<TimesheetTimeRuleDto>> readAllByTimesheetId(@PathVariable String timesheetId) {
        return ResponseEntity.ok(service.readAllByTimesheetId(timesheetId));
    }

    @GetMapping("/by-time-rule/{timeRuleId}")
    public ResponseEntity<List<TimesheetTimeRuleDto>> readAllByTimeRuleId(@PathVariable String timeRuleId) {
        return ResponseEntity.ok(service.readAllByTimeRuleId(timeRuleId));
    }

    @DeleteMapping("/by-timesheet/{timesheetId}")
    public ResponseEntity<Void> deleteAllByTimesheetId(@PathVariable String timesheetId) {
        service.deleteAllByTimesheetId(timesheetId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/by-time-rule/{timeRuleId}")
    public ResponseEntity<Void> deleteAllByTimeRuleId(@PathVariable String timeRuleId) {
        service.deleteAllByTimeRuleId(timeRuleId);
        return ResponseEntity.noContent().build();
    }
}
