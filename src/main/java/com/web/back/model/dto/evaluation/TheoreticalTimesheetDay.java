package com.web.back.model.dto.evaluation;

import java.time.Instant;
import java.time.LocalTime;
import java.util.UUID;

public record TheoreticalTimesheetDay(Instant date, UUID timeSheetId, LocalTime entryTime, LocalTime breakDepartureTime,
                                      LocalTime breakReturnTime, LocalTime departureTime, String generalResult) {
    public void withGeneralResult(String generalResult) {
        new TheoreticalTimesheetDay(this.date, this.timeSheetId, this.entryTime, this.breakDepartureTime,
                this.breakReturnTime, this.departureTime, generalResult);
    }
}
