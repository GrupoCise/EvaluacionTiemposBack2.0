package com.web.back.controllers;

import com.web.back.model.dto.PeriodDto;
import com.web.back.model.requests.PeriodRequest;
import com.web.back.services.PeriodService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/period/")
@Tag(name = "Periods")
public class PeriodController {
    private final PeriodService periodService;

    public PeriodController(PeriodService periodService) {
        this.periodService = periodService;
    }

    @PostMapping
    public ResponseEntity<PeriodDto> createPeriod(@RequestBody PeriodRequest period) {
        return ResponseEntity.status(201).body(periodService.createPeriod(period));
    }

    @GetMapping("{id}")
    public ResponseEntity<PeriodDto> getPeriod(@PathVariable Long id) {
        PeriodDto period = periodService.getPeriodById(id);
        if (period != null) {
            return ResponseEntity.ok(period);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<PeriodDto>> getAllPeriods() {
        List<PeriodDto> periods = periodService.getAllPeriods();
        return ResponseEntity.ok(periods);
    }

    @PutMapping("{id}")
    public ResponseEntity<PeriodDto> updatePeriod(@PathVariable Long id, @RequestBody PeriodRequest period) {
        PeriodDto updated = periodService.updatePeriod(id, period);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deletePeriod(@PathVariable Long id) {
        periodService.deletePeriod(id);

        return ResponseEntity.noContent().build();
    }
}
