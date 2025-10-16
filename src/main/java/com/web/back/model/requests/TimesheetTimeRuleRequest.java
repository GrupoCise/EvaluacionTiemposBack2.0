package com.web.back.model.requests;

import java.util.List;

public record TimesheetTimeRuleRequest(String timesheetId, List<String> timeRuleId) {
}
