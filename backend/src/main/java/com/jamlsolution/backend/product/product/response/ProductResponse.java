package com.jamlsolution.backend.product.product.response;

import com.jamlsolution.backend.product.brand.response.BrandResponse;
import com.jamlsolution.backend.product.category.response.CategoryResponse;
import com.jamlsolution.backend.product.product.Product;
import com.jamlsolution.backend.product.product.ProductStatus;
import com.jamlsolution.backend.product.product.ProductType;
import com.jamlsolution.backend.tax.TaxClass;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ProductResponse(
        String id,
        String name,
        String slug,
        String shortDescription,
        String description,
        ProductStatus status,
        ProductType type,
        BrandResponse brand,
        List<CategoryResponse> categories,
        List<VariantResponse> variants,
        List<ProductImageResponse> images,
        List<String> tags,
        String metaTitle,
        String metaDescription,
        String metaKeywords,
        BigDecimal averageRating,
        int totalReviews,
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
        LocalDateTime createdDate,
        LocalDateTime lastModifiedDate
) {
    public static ProductResponse from(Product p) {
        return new ProductResponse(
                p.getId(), p.getName(), p.getSlug(), p.getShortDescription(), p.getDescription(),
                p.getStatus(), p.getType(),
                p.getBrand() != null ? BrandResponse.from(p.getBrand()) : null,
                p.getCategories() != null
                        ? p.getCategories().stream().map(CategoryResponse::shallow).toList()
                        : List.of(),
                p.getVariants() != null
                        ? p.getVariants().stream().map(VariantResponse::from).toList()
                        : List.of(),
                p.getImages() != null
                        ? p.getImages().stream().map(ProductImageResponse::from).toList()
                        : List.of(),
                p.getTags(),
                p.getMetaTitle(), p.getMetaDescription(), p.getMetaKeywords(),
                p.getAverageRating(), p.getTotalReviews(), p.isFeatured(),
                p.isTaxable(), p.getTaxClass(),
                p.getWeight(), p.getWeightUnit(),
                p.getLength(), p.getWidth(), p.getHeight(), p.getDimensionUnit(),
                p.isRequiresShipping(),
                p.getCreatedDate(), p.getLastModifiedDate()
        );
    }
}
