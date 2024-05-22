package com.web.back.mappers;

import com.web.back.model.dto.PermissionDto;
import com.web.back.model.entities.Permission;

public final class PermissionDtoMapper {
    private PermissionDtoMapper() {}

    public static PermissionDto mapFrom(Permission permission){
        return new PermissionDto(permission.getId(), permission.getKeyName(), permission.getDescription());
    }
}
