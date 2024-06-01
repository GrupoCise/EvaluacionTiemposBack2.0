package com.web.back.mappers;

import com.web.back.model.dto.UserDto;
import com.web.back.model.entities.User;

import java.util.Optional;

public final class UserDtoMapper {

    private UserDtoMapper(){}

    public static UserDto mapFrom(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                user.getActive(),
                user.getProfiles().stream()
                        .map(ProfileDtoMapper::mapFrom)
                        .toList(),
                user.getUserLevel()
        );
    }

    public static UserDto mapFrom(Optional<User> userOptional) {
        if (userOptional.isEmpty()) { return null; }

        User user = userOptional.get();

        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                user.getActive(),
                user.getProfiles().stream()
                        .map(ProfileDtoMapper::mapFrom)
                        .toList(),
                user.getUserLevel()
        );
    }
}
