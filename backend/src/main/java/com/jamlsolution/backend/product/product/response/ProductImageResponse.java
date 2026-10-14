package com.jamlsolution.backend.product.product.response;

import com.jamlsolution.backend.product.product.ProductImage;

public record ProductImageResponse(
        String id,
        String url,
        String altText,
        boolean isPrimary,
        int position,
        String variantId
) {
    public static ProductImageResponse from(ProductImage img) {
        return new ProductImageResponse(img.getId(), img.getUrl(), img.getAltText(),
                img.isPrimary(), img.getPosition(), img.getVariantId());
    }
}
