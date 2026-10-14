package com.jamlsolution.backend.product.product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, String> {
    boolean existsBySku(String sku);
    Optional<ProductVariant> findBySku(String sku);
    List<ProductVariant> findByProductId(String productId);
}
