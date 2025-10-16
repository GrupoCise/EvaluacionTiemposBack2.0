package com.web.back.model.dto;

public record TimeRuleDto(String id, String description, Integer level, Integer sequence, String rule, String resultMeets, boolean exclusive) {
}
