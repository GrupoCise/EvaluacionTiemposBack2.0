package com.web.back.mappers;

import com.web.back.model.dto.ProfileDto;
import com.web.back.model.entities.Profile;

public final class ProfileDtoMapper {
    private ProfileDtoMapper() {}

    public static ProfileDto mapFrom(Profile profile) {
        return new ProfileDto(profile.getId(), profile.getDescription());
    }
}
