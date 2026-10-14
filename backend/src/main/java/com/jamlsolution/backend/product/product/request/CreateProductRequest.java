package com.jamlsolution.backend.product.product.request;

import com.jamlsolution.backend.product.product.ProductType;
import com.jamlsolution.backend.tax.TaxClass;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.util.List;

public record CreateProductRequest(
        @NotBlank String name,
        String slug,
        String shortDescription,
        String description,
        ProductType type,
        String brandId,
        List<String> categoryIds,
        List<String> tags,
        boolean featured,
        boolean taxable,
        TaxClass taxClass,
        BigDecimal weight,
        String weightUnit,
        BigDecimal length,
        BigDecimal width,
        BigDecimal height,
        String dimensionUnit,
        boolean requiresShipping,
        String metaTitle,
        String metaDescription,
        String metaKeywords
) {}
