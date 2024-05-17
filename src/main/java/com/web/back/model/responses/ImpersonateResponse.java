package com.web.back.model.responses;

public record ImpersonateResponse(Long impersonationId, String actorUserName, String targetUserName){
}
