package com.jamlsolution.backend.product.product.request;

import jakarta.validation.constraints.NotBlank;

public record AttributeValueRequest(
        @NotBlank String attributeId,
        @NotBlank String value,
        String displayValue
) {}
