package com.web.back.model.dto.evaluation;

import com.web.back.model.entities.TimeRecord;
import reactor.util.function.Tuple2;

import java.util.List;
import java.util.Optional;

public record TheoreticalScheduleDto(String employeeId, List<Tuple2<TheoreticalTimesheetDay, Optional<TimeRecord>>> scheduleAndTimeRecords) {
}

