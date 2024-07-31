package com.web.back.filters;

import com.web.back.model.enumerators.PermissionsEnum;

import java.util.Arrays;
import java.util.Objects;

public final class PermissionsFilter {

    private PermissionsFilter() {
    }

    public static Boolean canCreate(String[] userPermissions) {
        return Arrays.stream(userPermissions)
                .anyMatch(userPermission -> Objects.equals(userPermission, PermissionsEnum.CREATE.name()));
    }

    public static Boolean canEdit(String[] userPermissions) {
        return Arrays.stream(userPermissions)
                .anyMatch(userPermission -> Objects.equals(userPermission, PermissionsEnum.UPDATE.name()));
    }

    public static Boolean canRead(String[] userPermissions) {
        return Arrays.stream(userPermissions)
                .anyMatch(userPermission -> Objects.equals(userPermission, PermissionsEnum.READ.name()));
    }

    public static Boolean canDelete(String[] userPermissions) {
        return Arrays.stream(userPermissions)
                .anyMatch(userPermission -> Objects.equals(userPermission, PermissionsEnum.DELETE.name()));
    }

    public static Boolean isSuperUser(String[] userPermissions) {
        return Arrays.stream(userPermissions)
                .anyMatch(userPermission -> Objects.equals(userPermission, PermissionsEnum.SUPER.name()));
    }
}
