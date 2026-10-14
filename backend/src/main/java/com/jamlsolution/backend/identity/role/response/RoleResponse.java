package com.jamlsolution.backend.identity.role.response;

import com.jamlsolution.backend.identity.permission.response.PermissionResponse;
import com.jamlsolution.backend.identity.role.Role;

import java.util.List;

public record RoleResponse(
        String id,
        String name,
        List<PermissionResponse> permissions
) {
    public static RoleResponse from(Role role) {
        List<PermissionResponse> perms = role.getPermissions() == null ? List.of() :
                role.getPermissions().stream().map(PermissionResponse::from).toList();
        return new RoleResponse(role.getId(), role.getName(), perms);
    }
}
