package com.jamlsolution.backend.product.discount;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponUsageRepository extends JpaRepository<CouponUsage, String> {
    long countByCouponId(String couponId);
    long countByCouponIdAndUserId(String couponId, String userId);
}
