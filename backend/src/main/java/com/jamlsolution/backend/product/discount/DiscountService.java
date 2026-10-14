package com.jamlsolution.backend.product.discount;

import com.jamlsolution.backend.product.discount.request.CreateCouponRequest;
import com.jamlsolution.backend.product.discount.request.CreateDiscountRequest;
import com.jamlsolution.backend.product.discount.response.CouponResponse;
import com.jamlsolution.backend.product.discount.response.CouponValidationResponse;
import com.jamlsolution.backend.product.discount.response.DiscountResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DiscountService {
    DiscountResponse createDiscount(CreateDiscountRequest request);
    Page<DiscountResponse> findAllDiscounts(Pageable pageable);
    DiscountResponse findDiscountById(String id);
    DiscountResponse updateDiscount(String id, CreateDiscountRequest request);
    void deleteDiscount(String id);

    CouponResponse createCoupon(CreateCouponRequest request);
    Page<CouponResponse> findAllCoupons(Pageable pageable);
    CouponResponse findCouponById(String id);
    void deleteCoupon(String id);
    CouponValidationResponse validateCoupon(String code);
}
