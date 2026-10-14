package com.jamlsolution.backend.common;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(
        name = "ApiResponse",
        description = "Standard response wrapper used across all APIs"
)
public record ApiResponse<T>(

        @Schema(description = "Indicates if the request was successful", example = "true")
        boolean success,

        @Schema(description = "Human-readable message", example = "Operation successful")
        String message,

        @Schema(description = "Response payload (can be object, list, etc.)")
        T data,

        @Schema(description = "Validation or error details if any")
        Object errors,

        @Schema(description = "Response timestamp in UTC")
        Instant timestamp,

        @Schema(description = "Request path that generated this response", example = "/api/users")
        String path
) {
}