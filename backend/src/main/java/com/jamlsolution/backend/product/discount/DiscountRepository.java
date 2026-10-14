package com.jamlsolution.backend.product.discount;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DiscountRepository extends JpaRepository<Discount, String>,
        JpaSpecificationExecutor<Discount> {
}
