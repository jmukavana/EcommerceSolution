package com.jamlsolution.backend.product.discount.imp;

import com.jamlsolution.backend.exception.BusinessException;
import com.jamlsolution.backend.exception.ErrorCode;
import com.jamlsolution.backend.product.discount.*;
import com.jamlsolution.backend.product.discount.request.CreateCouponRequest;
import com.jamlsolution.backend.product.discount.request.CreateDiscountRequest;
import com.jamlsolution.backend.product.discount.response.CouponResponse;
import com.jamlsolution.backend.product.discount.response.CouponValidationResponse;
import com.jamlsolution.backend.product.discount.response.DiscountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Transactional
public class DiscountServiceImpl implements DiscountService {

    private final DiscountRepository discountRepository;
    private final CouponRepository couponRepository;
    private final CouponUsageRepository couponUsageRepository;

    @Override
    public DiscountResponse createDiscount(CreateDiscountRequest req) {
        Discount discount = Discount.builder()
                .name(req.name())
                .description(req.description())
                .type(req.type())
                .value(req.value())
                .maxDiscountAmount(req.maxDiscountAmount())
                .minimumOrderAmount(req.minimumOrderAmount())
                .minimumQuantity(req.minimumQuantity() > 0 ? req.minimumQuantity() : 1)
                .buyQuantity(req.buyQuantity())
                .getQuantity(req.getQuantity())
                .getDiscountPercent(req.getDiscountPercent())
                .applicableTo(req.applicableTo() != null ? req.applicableTo() : DiscountApplicability.ALL)
                .applicableProductIds(req.applicableProductIds() != null ? req.applicableProductIds() : new ArrayList<>())
                .applicableCategoryIds(req.applicableCategoryIds() != null ? req.applicableCategoryIds() : new ArrayList<>())
                .startsAt(req.startsAt())
                .expiresAt(req.expiresAt())
                .usageLimit(req.usageLimit())
                .usageCount(0)
                .active(true)
                .stackable(req.stackable())
                .build();
        return DiscountResponse.from(discountRepository.save(discount));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DiscountResponse> findAllDiscounts(Pageable pageable) {
        return discountRepository.findAll(pageable).map(DiscountResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public DiscountResponse findDiscountById(String id) {
        return DiscountResponse.from(getDiscountOrThrow(id));
    }

    @Override
    public DiscountResponse updateDiscount(String id, CreateDiscountRequest req) {
        Discount d = getDiscountOrThrow(id);
        d.setName(req.name());
        d.setDescription(req.description());
        d.setType(req.type());
        d.setValue(req.value());
        d.setMaxDiscountAmount(req.maxDiscountAmount());
        d.setMinimumOrderAmount(req.minimumOrderAmount());
        d.setMinimumQuantity(req.minimumQuantity() > 0 ? req.minimumQuantity() : 1);
        d.setBuyQuantity(req.buyQuantity());
        d.setGetQuantity(req.getQuantity());
        d.setGetDiscountPercent(req.getDiscountPercent());
        if (req.applicableTo() != null) d.setApplicableTo(req.applicableTo());
        if (req.applicableProductIds() != null) d.setApplicableProductIds(req.applicableProductIds());
        if (req.applicableCategoryIds() != null) d.setApplicableCategoryIds(req.applicableCategoryIds());
        d.setStartsAt(req.startsAt());
        d.setExpiresAt(req.expiresAt());
        d.setUsageLimit(req.usageLimit());
        d.setStackable(req.stackable());
        return DiscountResponse.from(discountRepository.save(d));
    }

    @Override
    public void deleteDiscount(String id) {
        discountRepository.delete(getDiscountOrThrow(id));
    }

    @Override
    public CouponResponse createCoupon(CreateCouponRequest req) {
        if (couponRepository.existsByCode(req.code())) {
            throw new BusinessException(ErrorCode.COUPON_CODE_EXISTS);
        }
        Discount discount = getDiscountOrThrow(req.discountId());
        Coupon coupon = Coupon.builder()
                .code(req.code().toUpperCase())
                .discount(discount)
                .perUserLimit(req.perUserLimit())
                .totalUsageLimit(req.totalUsageLimit())
                .totalUsageCount(0)
                .active(true)
                .startsAt(req.startsAt())
                .expiresAt(req.expiresAt())
                .build();
        return CouponResponse.from(couponRepository.save(coupon));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CouponResponse> findAllCoupons(Pageable pageable) {
        return couponRepository.findAll(pageable).map(CouponResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public CouponResponse findCouponById(String id) {
        return CouponResponse.from(couponRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.COUPON_NOT_FOUND)));
    }

    @Override
    public void deleteCoupon(String id) {
        couponRepository.delete(couponRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.COUPON_NOT_FOUND)));
    }

    @Override
    @Transactional(readOnly = true)
    public CouponValidationResponse validateCoupon(String code) {
        Coupon coupon = couponRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new BusinessException(ErrorCode.COUPON_NOT_FOUND));

        if (!coupon.isActive()) {
            throw new BusinessException(ErrorCode.COUPON_INVALID);
        }
        LocalDateTime now = LocalDateTime.now();
        if (coupon.getExpiresAt() != null && coupon.getExpiresAt().isBefore(now)) {
            throw new BusinessException(ErrorCode.COUPON_EXPIRED);
        }
        if (coupon.getStartsAt() != null && coupon.getStartsAt().isAfter(now)) {
            throw new BusinessException(ErrorCode.COUPON_INVALID);
        }
        if (coupon.getTotalUsageLimit() != null
                && couponUsageRepository.countByCouponId(coupon.getId()) >= coupon.getTotalUsageLimit()) {
            throw new BusinessException(ErrorCode.COUPON_USAGE_LIMIT_REACHED);
        }
        Discount d = coupon.getDiscount();
        return new CouponValidationResponse(true, coupon.getId(), d.getId(),
                d.getValue(), d.getType().name(), "Coupon is valid");
    }

    private Discount getDiscountOrThrow(String id) {
        return discountRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.DISCOUNT_NOT_FOUND));
    }
}
