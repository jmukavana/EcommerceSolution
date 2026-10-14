package com.jamlsolution.backend.product.brand.request;

import jakarta.validation.constraints.NotBlank;

public record CreateBrandRequest(
        @NotBlank String name,
        String slug,
        String description,
        String logoUrl,
        String websiteUrl
) {}
