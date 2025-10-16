package com.web.back.model.requests;

import java.time.Instant;

public record PeriodRequest(String grouper1, String grouper2, String grouper3, String grouper4, String grouper5, Instant fromDate, Instant toDate) {
}
