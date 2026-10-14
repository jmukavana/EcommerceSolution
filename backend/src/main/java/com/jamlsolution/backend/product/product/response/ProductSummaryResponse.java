package com.jamlsolution.backend.product.product.response;

import com.jamlsolution.backend.product.product.Product;
import com.jamlsolution.backend.product.product.ProductStatus;
import com.jamlsolution.backend.product.product.ProductType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductSummaryResponse(
        String id,
        String name,
        String slug,
        String shortDescription,
        ProductStatus status,
        ProductType type,
        String brandId,
        String brandName,
        BigDecimal averageRating,
        int totalReviews,
        boolean featured,
        LocalDateTime createdDate
) {
    public static ProductSummaryResponse from(Product p) {
        return new ProductSummaryResponse(p.getId(), p.getName(), p.getSlug(),
                p.getShortDescription(), p.getStatus(), p.getType(),
                p.getBrand() != null ? p.getBrand().getId() : null,
                p.getBrand() != null ? p.getBrand().getName() : null,
                p.getAverageRating(), p.getTotalReviews(), p.isFeatured(), p.getCreatedDate());
    }
}
