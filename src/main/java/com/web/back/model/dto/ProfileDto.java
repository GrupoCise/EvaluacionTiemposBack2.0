package com.web.back.model.dto;

import java.util.List;

public record ProfileDto(Integer id, String description, List<PermissionDto> permissions) {
}
