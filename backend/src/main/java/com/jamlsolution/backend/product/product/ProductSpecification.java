package com.jamlsolution.backend.product.product;

import org.springframework.data.jpa.domain.Specification;

public final class ProductSpecification {

    private ProductSpecification() {}

    public static Specification<Product> build(String search, ProductStatus status,
                                                ProductType type, String brandId, String categoryId) {
        Specification<Product> spec = (root, query, cb) -> null;

        if (search != null && !search.isBlank()) {
            String term = "%" + search.toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("name")), term),
                    cb.like(cb.lower(root.get("slug")), term)
            ));
        }
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        if (type != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("type"), type));
        }
        if (brandId != null && !brandId.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("brand").get("id"), brandId));
        }
        if (categoryId != null && !categoryId.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.join("categories").get("id"), categoryId));
        }
        return spec;
    }
}
