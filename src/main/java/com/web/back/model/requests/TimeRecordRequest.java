package com.web.back.model.requests;

import java.time.Instant;
import java.time.LocalTime;

public record TimeRecordRequest(String employeeId, Integer turn, LocalTime entryTime, LocalTime breakDepartureTime, LocalTime breakReturnTime, LocalTime departureTime, Instant date) {
}
