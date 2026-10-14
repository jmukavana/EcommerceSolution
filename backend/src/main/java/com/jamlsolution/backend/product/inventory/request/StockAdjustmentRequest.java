package com.jamlsolution.backend.product.inventory.request;

import com.jamlsolution.backend.product.inventory.MovementType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StockAdjustmentRequest(
        @NotNull MovementType type,
        int quantity,
        String notes
) {}
