package com.jamlsolution.backend.product.product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, String> {
    boolean existsByName(String name);
    Optional<ProductAttribute> findByName(String name);
}
