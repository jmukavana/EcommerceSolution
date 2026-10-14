package com.jamlsolution.backend.identity.role.request;

import jakarta.validation.constraints.NotBlank;

public record CreateRoleRequest(
        @NotBlank(message = "Role name is required")
        String name
) {}
