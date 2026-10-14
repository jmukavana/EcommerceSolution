package com.jamlsolution.backend.order.returns.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ReturnItemRequest(
        @NotBlank String orderItemId,
        @Min(1) int quantity,
        String reason
) {}
