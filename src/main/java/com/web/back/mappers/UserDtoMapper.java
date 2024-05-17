package com.web.back.mappers;

import com.web.back.model.dto.ProfileDto;
import com.web.back.model.dto.UserDto;
import com.web.back.model.entities.User;

import java.util.Optional;

public final class UserDtoMapper {

    private UserDtoMapper(){}

    public static UserDto mapFrom(User user) {
        return new UserDto(user.getUsername(),
                user.getName(),
                user.getEmail(),
                user.getActive(),
                user.getProfiles().stream()
                        .map(profile -> new ProfileDto(profile.getId(), profile.getDescription()))
                        .toList()
        );
    }

    public static UserDto mapFrom(Optional<User> userOptional) {
        if (userOptional.isEmpty()) { return null; }

        User user = userOptional.get();

        return new UserDto(user.getUsername(),
                user.getName(),
                user.getEmail(),
                user.getActive(),
                user.getProfiles().stream()
                        .map(profile -> new ProfileDto(profile.getId(), profile.getDescription()))
                        .toList()
        );
    }
}
