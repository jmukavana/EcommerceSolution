package com.jamlsolution.backend.product.product.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record CreateVariantRequest(
        @NotBlank String sku,
        String barcode,
        String name,
        @NotNull BigDecimal price,
        BigDecimal compareAtPrice,
        BigDecimal costPrice,
        boolean trackInventory,
        String imageUrl,
        boolean isDefault,
        int position,
        List<AttributeValueRequest> attributeValues
) {}
