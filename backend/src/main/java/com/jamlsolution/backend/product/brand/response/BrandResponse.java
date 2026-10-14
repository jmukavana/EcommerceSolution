package com.jamlsolution.backend.product.brand.response;

import com.jamlsolution.backend.product.brand.Brand;

import java.time.LocalDateTime;

public record BrandResponse(
        String id,
        String name,
        String slug,
        String description,
        String logoUrl,
        String websiteUrl,
        boolean active,
        LocalDateTime createdDate
) {
    public static BrandResponse from(Brand b) {
        return new BrandResponse(b.getId(), b.getName(), b.getSlug(), b.getDescription(),
                b.getLogoUrl(), b.getWebsiteUrl(), b.isActive(), b.getCreatedDate());
    }
}
