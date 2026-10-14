package com.jamlsolution.backend.order.order.request;

import com.jamlsolution.backend.order.order.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(
        @NotNull OrderStatus status,
        String notes
) {}
