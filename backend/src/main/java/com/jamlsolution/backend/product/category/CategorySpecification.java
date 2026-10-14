package com.jamlsolution.backend.product.category;

import org.springframework.data.jpa.domain.Specification;

public final class CategorySpecification {

    private CategorySpecification() {}

    public static Specification<Category> build(String search) {
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
