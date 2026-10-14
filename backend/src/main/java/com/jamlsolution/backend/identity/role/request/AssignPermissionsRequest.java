package com.jamlsolution.backend.identity.role.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record AssignPermissionsRequest(
        @NotEmpty(message = "At least one permission ID is required")
        List<String> permissionIds
) {}
