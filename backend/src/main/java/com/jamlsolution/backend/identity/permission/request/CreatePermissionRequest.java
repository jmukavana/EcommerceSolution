package com.jamlsolution.backend.identity.permission.request;

import jakarta.validation.constraints.NotBlank;

public record CreatePermissionRequest(
        @NotBlank(message = "Permission name is required")
        String name,

        String description
) {}
