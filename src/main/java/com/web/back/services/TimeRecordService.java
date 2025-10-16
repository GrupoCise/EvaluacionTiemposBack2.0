package com.web.back.services;

import com.web.back.model.dto.TimeRecordDto;
import com.web.back.model.entities.TimeRecord;
import com.web.back.model.requests.TimeRecordRequest;
import com.web.back.repositories.EmployeeRepository;
import com.web.back.repositories.TimeRecordRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TimeRecordService {
    private final TimeRecordRepository timeRecordRepository;
    private final EmployeeRepository employeeRepository;

    public TimeRecordService(TimeRecordRepository timeRecordRepository, EmployeeRepository employeeRepository) {
        this.timeRecordRepository = timeRecordRepository;
        this.employeeRepository = employeeRepository;
    }

    public TimeRecordDto create(TimeRecordRequest request) {
        var timeRule = mapToEntity(request, null);
        var saved = timeRecordRepository.save(timeRule);

        return mapToDto(saved);
    }

    public List<TimeRecordDto> readAll() {
        return timeRecordRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public TimeRecordDto readById(String id) {
        var entity = timeRecordRepository.findById(UUID.fromString(id));

        return entity
                .map(this::mapToDto)
                .orElse(null);

    }

    public TimeRecordDto update(String id, TimeRecordRequest request) {
        var exitingEntity = timeRecordRepository.findById(UUID.fromString(id));

        if (exitingEntity.isEmpty()) {
            throw new RuntimeException("TimeRecord not found");
        }

        var timeRule = mapToEntity(request, exitingEntity.get().getId());
        var saved = timeRecordRepository.save(timeRule);

        return mapToDto(saved);
    }

    public void deleteById(String id) {
        timeRecordRepository.deleteById(UUID.fromString(id));
    }

    private TimeRecord mapToEntity(TimeRecordRequest request, UUID id) {
        var entity = new TimeRecord();
        if (id != null) entity.setId(id);

        var employee = employeeRepository.getReferenceById(UUID.fromString(request.employeeId()));

        entity.setEmployee(employee);
        entity.setTurn(request.turn());
        entity.setEntryTime(request.entryTime());
        entity.setBreakDepartureTime(request.breakDepartureTime());
        entity.setBreakReturnTime(request.breakReturnTime());
        entity.setDepartureTime(request.departureTime());
        entity.setDate(request.date());
        return entity;
    }

    private TimeRecordDto mapToDto(TimeRecord entity) {
        return new TimeRecordDto(
                entity.getId().toString(),
                entity.getEmployee().getId().toString(),
                entity.getEmployee().getNumEmployee(),
                entity.getTurn(),
                entity.getEntryTime(),
                entity.getBreakDepartureTime(),
                entity.getBreakReturnTime(),
                entity.getDepartureTime(),
                entity.getDate()
        );
    }
}
