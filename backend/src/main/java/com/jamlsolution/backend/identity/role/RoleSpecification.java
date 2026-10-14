package com.jamlsolution.backend.identity.role;

import org.springframework.data.jpa.domain.Specification;

public final class RoleSpecification {

    private RoleSpecification() {}

    /** Case-insensitive search on role name. */
    public static Specification<Role> search(String term) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("name")), "%" + term.toLowerCase() + "%");
    }

    public static Specification<Role> build(String search) {
        if (search != null && !search.isBlank()) {
            return search(search);
        }
        return (root, query, cb) -> null;
    }
}
