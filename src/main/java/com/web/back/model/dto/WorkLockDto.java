package com.web.back.model.dto;

public record WorkLockDto(Integer evaluationId, Integer userId) {

    @Override
    public String toString() {
        return this.evaluationId.toString() + "|" + this.userId.toString();
    }
}
