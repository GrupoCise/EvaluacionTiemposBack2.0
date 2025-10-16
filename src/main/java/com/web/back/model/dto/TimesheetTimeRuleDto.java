package com.web.back.model.dto;

import java.util.UUID;

public record TimesheetTimeRuleDto(UUID timesheetId, String timesheetIdentifier, UUID timeRuleId, String timeRuleDescription) {
}
