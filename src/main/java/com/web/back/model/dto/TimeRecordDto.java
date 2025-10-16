package com.web.back.model.dto;

import java.time.Instant;
import java.time.LocalTime;

public record TimeRecordDto(String id, String employeeId, String employeeNumber, Integer turn, LocalTime entryTime, LocalTime breakDepartureTime, LocalTime breakReturnTime, LocalTime departureTime, Instant date) {
}
