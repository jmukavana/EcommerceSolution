package com.jamlsolution.backend.product.brand;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, String>,
        JpaSpecificationExecutor<Brand> {
    boolean existsByName(String name);
    boolean existsBySlug(String slug);
    Optional<Brand> findBySlug(String slug);
}
