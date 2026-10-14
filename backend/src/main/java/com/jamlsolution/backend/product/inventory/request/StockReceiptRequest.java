package com.jamlsolution.backend.product.inventory.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record StockReceiptRequest(
        @NotBlank String variantId,
        @NotBlank String warehouseId,
        @Min(1) int quantity,
        String notes,
        String referenceId
) {}
