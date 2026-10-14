package com.jamlsolution.backend.identity.user.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record AssignRolesRequest(
        @NotEmpty(message = "At least one role ID is required")
        List<String> roleIds
) {}
