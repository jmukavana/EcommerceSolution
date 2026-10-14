package com.jamlsolution.backend.order.cart.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record AddCartItemRequest(
        @NotBlank String variantId,
        @Min(1) int quantity
) {}
