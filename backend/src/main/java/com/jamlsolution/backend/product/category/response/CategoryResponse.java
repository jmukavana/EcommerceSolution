package com.jamlsolution.backend.product.category.response;

import com.jamlsolution.backend.product.category.Category;

import java.time.LocalDateTime;
import java.util.List;

public record CategoryResponse(
        String id,
        String name,
        String slug,
        String description,
        String imageUrl,
        boolean active,
        int sortOrder,
        String parentId,
        List<CategoryResponse> children,
        LocalDateTime createdDate
) {
    public static CategoryResponse from(Category c) {
        return new CategoryResponse(
                c.getId(), c.getName(), c.getSlug(), c.getDescription(), c.getImageUrl(),
                c.isActive(), c.getSortOrder(),
                c.getParent() != null ? c.getParent().getId() : null,
                c.getChildren() != null
                        ? c.getChildren().stream().map(CategoryResponse::shallow).toList()
                        : List.of(),
                c.getCreatedDate()
        );
    }

    public static CategoryResponse shallow(Category c) {
        return new CategoryResponse(
                c.getId(), c.getName(), c.getSlug(), c.getDescription(), c.getImageUrl(),
                c.isActive(), c.getSortOrder(),
                c.getParent() != null ? c.getParent().getId() : null,
                List.of(),
                c.getCreatedDate()
        );
    }
}
