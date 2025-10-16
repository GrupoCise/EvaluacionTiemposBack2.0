package com.web.back.services;

import com.web.back.model.dto.TimesheetTimeRuleDto;
import com.web.back.model.entities.TimesheetTimeRule;
import com.web.back.model.entities.TimesheetTimeRuleId;
import com.web.back.model.requests.TimesheetTimeRuleRequest;
import com.web.back.repositories.TimeRuleRepository;
import com.web.back.repositories.TimesheetTimeRuleRepository;
import com.web.back.repositories.TimesheetsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TimesheetTimeRuleService {
    private final TimesheetTimeRuleRepository timesheetTimeRuleRepository;
    private final TimesheetsRepository timesheetsRepository;
    private final TimeRuleRepository timeRuleRepository;

    public TimesheetTimeRuleService(TimesheetTimeRuleRepository timesheetTimeRuleRepository,
                                    TimesheetsRepository timesheetsRepository,
                                    TimeRuleRepository timeRuleRepository) {
        this.timesheetTimeRuleRepository = timesheetTimeRuleRepository;
        this.timesheetsRepository = timesheetsRepository;
        this.timeRuleRepository = timeRuleRepository;
    }

    public List<TimesheetTimeRuleDto> create(TimesheetTimeRuleRequest request) {
        List<TimesheetTimeRule> entities = request.timeRuleId().stream().map(timeRuleId -> {
            TimesheetTimeRule entity = new TimesheetTimeRule();
            TimesheetTimeRuleId id = new TimesheetTimeRuleId();
            id.setTimesheetId(UUID.fromString(request.timesheetId()));
            id.setTimeRuleId(UUID.fromString(timeRuleId));
            entity.setId(id);
            entity.setTimesheet(timesheetsRepository.getReferenceById(id.getTimesheetId()));
            entity.setTimeRule(timeRuleRepository.getReferenceById(id.getTimeRuleId()));
            return entity;
        }).toList();
        return timesheetTimeRuleRepository.saveAll(entities)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public List<TimesheetTimeRuleDto> readAllByTimesheetId(String timesheetId) {
        return timesheetTimeRuleRepository.findAllByTimesheetId(UUID.fromString(timesheetId))
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public List<TimesheetTimeRuleDto> readAllByTimeRuleId(String timeRuleId) {
        return timesheetTimeRuleRepository.findAllByTimeRuleId(UUID.fromString(timeRuleId))
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public List<TimesheetTimeRuleDto> update(TimesheetTimeRuleRequest request) {
        UUID timesheetId = UUID.fromString(request.timesheetId());
        timesheetTimeRuleRepository.deleteAllByTimesheetId(timesheetId);
        return create(request);
    }

    public void deleteAllByTimesheetId(String timesheetId) {
        timesheetTimeRuleRepository.deleteAllByTimesheetId(UUID.fromString(timesheetId));
    }

    public void deleteAllByTimeRuleId(String timeRuleId) {
        timesheetTimeRuleRepository.deleteAllByTimeRuleId(UUID.fromString(timeRuleId));
    }

    private TimesheetTimeRuleDto mapToDto(TimesheetTimeRule entity) {
        return new TimesheetTimeRuleDto(
                entity.getId().getTimesheetId(),
                entity.getTimesheet().getTimesheetIdentifier(),
                entity.getId().getTimeRuleId(),
                entity.getTimeRule().getDescription());
    }
}
