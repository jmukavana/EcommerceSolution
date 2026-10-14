package com.jamlsolution.backend.product.discount;

import com.jamlsolution.backend.common.ApiResponse;
import com.jamlsolution.backend.product.discount.request.CreateCouponRequest;
import com.jamlsolution.backend.product.discount.request.CreateDiscountRequest;
import com.jamlsolution.backend.product.discount.response.CouponResponse;
import com.jamlsolution.backend.product.discount.response.CouponValidationResponse;
import com.jamlsolution.backend.product.discount.response.DiscountResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@Tag(name = "Discounts & Coupons", description = "Discount rules and coupon code management")
@RestController
@RequestMapping("/api/v1/discounts")
@RequiredArgsConstructor
public class DiscountController {

    private final DiscountService discountService;

    // ── Discounts ──────────────────────────────────────────────────────────

    @Operation(summary = "Create discount rule")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<DiscountResponse> createDiscount(
            @Valid @RequestBody CreateDiscountRequest request,
            HttpServletRequest httpRequest) {
        return ok(discountService.createDiscount(request), httpRequest);
    }

    @Operation(summary = "List discount rules")
    @GetMapping
    public ApiResponse<Page<DiscountResponse>> findAllDiscounts(
            @PageableDefault(size = 20, sort = "createdDate") Pageable pageable,
            HttpServletRequest httpRequest) {
        return ok(discountService.findAllDiscounts(pageable), httpRequest);
    }

    @Operation(summary = "Get discount rule by ID")
    @GetMapping("/{id}")
    public ApiResponse<DiscountResponse> findDiscountById(
            @PathVariable String id, HttpServletRequest httpRequest) {
        return ok(discountService.findDiscountById(id), httpRequest);
    }

    @Operation(summary = "Update discount rule")
    @PutMapping("/{id}")
    public ApiResponse<DiscountResponse> updateDiscount(
            @PathVariable String id,
            @Valid @RequestBody CreateDiscountRequest request,
            HttpServletRequest httpRequest) {
        return ok(discountService.updateDiscount(id, request), httpRequest);
    }

    @Operation(summary = "Delete discount rule")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDiscount(@PathVariable String id) {
        discountService.deleteDiscount(id);
    }

    // ── Coupons ────────────────────────────────────────────────────────────

    @Operation(summary = "Create coupon code", description = "Code is stored in UPPERCASE.")
    @PostMapping("/coupons")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CouponResponse> createCoupon(
            @Valid @RequestBody CreateCouponRequest request,
            HttpServletRequest httpRequest) {
        return ok(discountService.createCoupon(request), httpRequest);
    }

    @Operation(summary = "List coupons")
    @GetMapping("/coupons")
    public ApiResponse<Page<CouponResponse>> findAllCoupons(
            @PageableDefault(size = 20, sort = "createdDate") Pageable pageable,
            HttpServletRequest httpRequest) {
        return ok(discountService.findAllCoupons(pageable), httpRequest);
    }

    @Operation(summary = "Get coupon by ID")
    @GetMapping("/coupons/{id}")
    public ApiResponse<CouponResponse> findCouponById(
            @PathVariable String id, HttpServletRequest httpRequest) {
        return ok(discountService.findCouponById(id), httpRequest);
    }

    @Operation(summary = "Delete coupon")
    @DeleteMapping("/coupons/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCoupon(@PathVariable String id) {
        discountService.deleteCoupon(id);
    }

    @Operation(summary = "Validate coupon code",
            description = "Checks active status, expiry, and usage limits. Returns discount details if valid.")
    @GetMapping("/coupons/validate")
    public ApiResponse<CouponValidationResponse> validateCoupon(
            @RequestParam String code,
            HttpServletRequest httpRequest) {
        return ok(discountService.validateCoupon(code), httpRequest);
    }

    private <T> ApiResponse<T> ok(T data, HttpServletRequest req) {
        return new ApiResponse<>(true, "Success", data, null, Instant.now(), req.getRequestURI());
    }
}
