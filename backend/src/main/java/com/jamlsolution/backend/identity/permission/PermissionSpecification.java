package com.jamlsolution.backend.identity.permission;

import org.springframework.data.jpa.domain.Specification;

public final class PermissionSpecification {

    private PermissionSpecification() {}

    /** Case-insensitive search across name and description (PostgreSQL ILIKE equivalent). */
    public static Specification<Permission> search(String term) {
        return (root, query, cb) -> {
            String pattern = "%" + term.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("name")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern)
            );
        };
    }

    public static Specification<Permission> build(String search) {
        if (search != null && !search.isBlank()) {
            return search(search);
        }
        return (root, query, cb) -> null;
    }
}
