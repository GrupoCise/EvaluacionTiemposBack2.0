package com.web.back.services;

import com.web.back.model.dto.TimeRuleDto;
import com.web.back.model.entities.TimeRule;
import com.web.back.model.requests.TimeRuleRequest;
import com.web.back.repositories.TimeRuleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TimeRuleService {
    private final TimeRuleRepository timeRuleRepository;

    public TimeRuleService(TimeRuleRepository timeRuleRepository) {
        this.timeRuleRepository = timeRuleRepository;
    }

    public TimeRuleDto create(TimeRuleRequest request) {
        var timeRule = mapToEntity(request, null);
        var saved = timeRuleRepository.save(timeRule);

        return mapToDto(saved);
    }

    public List<TimeRuleDto> readAll() {
        return timeRuleRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public TimeRuleDto readById(String id) {
        var timeRule = timeRuleRepository.findById(UUID.fromString(id));

        return timeRule
                .map(this::mapToDto)
                .orElse(null);

    }

    public TimeRuleDto update(String id, TimeRuleRequest request) {
        var exitingTimeRule = timeRuleRepository.findById(UUID.fromString(id));

        if (exitingTimeRule.isEmpty()) {
            throw new RuntimeException("TimeRule not found");
        }

        var timeRule = mapToEntity(request, exitingTimeRule.get().getId());
        var saved = timeRuleRepository.save(timeRule);

        return mapToDto(saved);
    }

    public void deleteById(String id) {
        timeRuleRepository.deleteById(UUID.fromString(id));
    }

    private TimeRule mapToEntity(TimeRuleRequest request, UUID id) {
        var timeRule = new TimeRule();
        if (id != null) timeRule.setId(id);
        timeRule.setDescription(request.description());
        timeRule.setLevel(request.level());
        timeRule.setSequence(request.sequence());
        timeRule.setRule(request.rule());
        timeRule.setResultMeets(request.resultMeets());
        timeRule.setExclusive(request.exclusive());
        return timeRule;
    }

    private TimeRuleDto mapToDto(TimeRule entity) {
        return new TimeRuleDto(
                entity.getId().toString(),
                entity.getDescription(),
                entity.getLevel(),
                entity.getSequence(),
                entity.getRule(),
                entity.getResultMeets(),
                entity.isExclusive()
        );
    }
}
