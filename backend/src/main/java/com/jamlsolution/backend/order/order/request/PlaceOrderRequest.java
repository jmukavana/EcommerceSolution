package com.jamlsolution.backend.order.order.request;

import com.jamlsolution.backend.order.payment.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PlaceOrderRequest(
        @NotBlank String shippingAddressId,
        String billingAddressId,
        String couponCode,
        @NotNull PaymentMethod paymentMethod,
        String notes
) {}
