package com.jamlsolution.backend.product.category.request;

import jakarta.validation.constraints.NotBlank;

public record CreateCategoryRequest(
        @NotBlank String name,
        String slug,
        String description,
        String imageUrl,
        String parentId,
        int sortOrder
) {}
