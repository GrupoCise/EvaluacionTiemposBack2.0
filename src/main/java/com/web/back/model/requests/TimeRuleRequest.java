package com.web.back.model.requests;

public record TimeRuleRequest(String description, Integer level, Integer sequence, String rule, String resultMeets, boolean exclusive) {
}
