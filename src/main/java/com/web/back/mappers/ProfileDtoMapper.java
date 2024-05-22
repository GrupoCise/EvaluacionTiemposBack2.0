package com.web.back.mappers;

import com.web.back.model.dto.ProfileDto;
import com.web.back.model.entities.Profile;
import com.web.back.model.entities.ProfilePermission;

import java.util.List;

public final class ProfileDtoMapper {
    private ProfileDtoMapper() {}

    public static ProfileDto mapFrom(Profile profile) {
        return new ProfileDto(profile.getId(), profile.getDescription(),
                profile.getPermissions().stream()
                        .map(PermissionDtoMapper::mapFrom)
                        .toList());
    }

    public static ProfileDto mapFrom(Profile profile, List<ProfilePermission> profilePermissions) {
        return new ProfileDto(
                profile.getId(),
                profile.getDescription(),
                profilePermissions.stream()
                        .map(profilePermission ->
                                PermissionDtoMapper.mapFrom(profilePermission.getPermission()))
                        .toList());
    }
}
