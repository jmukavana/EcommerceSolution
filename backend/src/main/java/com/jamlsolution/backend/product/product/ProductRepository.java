package com.jamlsolution.backend.product.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, String>,
        JpaSpecificationExecutor<Product> {
    boolean existsBySlug(String slug);
    Optional<Product> findBySlug(String slug);
}
