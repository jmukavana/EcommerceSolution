package com.jamlsolution.backend.identity.permission.response;

import com.jamlsolution.backend.identity.permission.Permission;

public record PermissionResponse(
        String id,
        String name,
        String description
) {
    public static PermissionResponse from(Permission p) {
        return new PermissionResponse(p.getId(), p.getName(), p.getDescription());
    }
}
