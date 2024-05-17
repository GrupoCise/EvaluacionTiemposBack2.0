package com.web.back.model.dto;

import java.util.List;

public record UserDto(String userName, String name, String email, Boolean isActive, List<ProfileDto> profiles) {
}
