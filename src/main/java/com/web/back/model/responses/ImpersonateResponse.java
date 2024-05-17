package com.web.back.model.responses;

public record ImpersonateResponse(Integer impersonationId, String actorUserName, String targetUserName){
}
