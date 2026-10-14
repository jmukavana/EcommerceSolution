package com.jamlsolution.backend.product.product.response;

import com.jamlsolution.backend.product.product.ProductVariant;

import java.math.BigDecimal;
import java.util.List;

public record VariantResponse(
        String id,
        String sku,
        String barcode,
        String name,
        BigDecimal price,
        BigDecimal compareAtPrice,
        BigDecimal costPrice,
        boolean trackInventory,
        String imageUrl,
        boolean isDefault,
        int position,
        boolean active,
        List<AttributeValueResponse> attributeValues
) {
    public static VariantResponse from(ProductVariant v) {
        return new VariantResponse(v.getId(), v.getSku(), v.getBarcode(), v.getName(),
                v.getPrice(), v.getCompareAtPrice(), v.getCostPrice(), v.isTrackInventory(),
                v.getImageUrl(), v.isDefault(), v.getPosition(), v.isActive(),
                v.getAttributeValues() != null
                        ? v.getAttributeValues().stream().map(AttributeValueResponse::from).toList()
                        : List.of());
    }
}
