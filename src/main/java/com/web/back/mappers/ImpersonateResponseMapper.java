package com.web.back.mappers;

import com.web.back.model.entities.Impersonation;
import com.web.back.model.responses.ImpersonateResponse;

public final class ImpersonateResponseMapper {
    private ImpersonateResponseMapper() {}

    public static ImpersonateResponse mapFrom(Impersonation impersonation){
        return new ImpersonateResponse(
                impersonation.getId(),
                impersonation.getUser().getUsername(),
                impersonation.getTargetUser().getUsername()
        );
    }
}
