package com.jamlsolution.backend.product.brand;

import org.springframework.data.jpa.domain.Specification;

public final class BrandSpecification {

    private BrandSpecification() {}

    public static Specification<Brand> build(String search) {
        if (search != null && !search.isBlank()) {
            String term = "%" + search.toLowerCase() + "%";
            return (root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("name")), term),
                    cb.like(cb.lower(root.get("slug")), term)
            );
        }
        return (root, query, cb) -> null;
    }
}
