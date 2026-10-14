package com.jamlsolution.backend.product.product.request;

import jakarta.validation.constraints.NotBlank;

public record AddImageRequest(
        @NotBlank String url,
        String altText,
        boolean isPrimary,
        int position,
        String variantId
) {}
