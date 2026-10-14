package com.jamlsolution.backend.identity.user;

import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public final class
UserSpecification {

    private UserSpecification() {}

    /** Full-text search across firstName, lastName, email, phoneNumber. */
    public static Specification<User> search(String term) {
        return (root, query, cb) -> {
            String pattern = "%" + term.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("firstName")), pattern),
                    cb.like(cb.lower(root.get("lastName")), pattern),
                    cb.like(cb.lower(root.get("email")), pattern),
                    cb.like(cb.lower(root.get("phoneNumber")), pattern)
            );
        };
    }

    public static Specification<User> isEnabled(boolean enabled) {
        return (root, query, cb) -> cb.equal(root.get("enabled"), enabled);
    }

    public static Specification<User> isLocked(boolean locked) {
        return (root, query, cb) -> cb.equal(root.get("locked"), locked);
    }

    public static Specification<User> isEmailVerified(boolean emailVerified) {
        return (root, query, cb) -> cb.equal(root.get("emailVerified"), emailVerified);
    }

    /** Filter by role name (case-insensitive). Uses LEFT JOIN to avoid duplicates with distinct. */
    public static Specification<User> hasRole(String roleName) {
        return (root, query, cb) -> {
            query.distinct(true);
            var roleJoin = root.join("roles", JoinType.LEFT);
            return cb.equal(cb.lower(roleJoin.get("name")), roleName.toLowerCase());
        };
    }

    public static Specification<User> build(
            String search,
            Boolean enabled,
            Boolean locked,
            Boolean emailVerified,
            String role) {

        Specification<User> spec = (root, query, cb) -> null;
        if (search != null && !search.isBlank())          spec = spec.and(search(search));
        if (enabled != null)                              spec = spec.and(isEnabled(enabled));
        if (locked != null)                               spec = spec.and(isLocked(locked));
        if (emailVerified != null)                        spec = spec.and(isEmailVerified(emailVerified));
        if (role != null && !role.isBlank())              spec = spec.and(hasRole(role));
        return spec;
    }
}
