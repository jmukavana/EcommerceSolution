package com.jamlsolution.backend.order.shipment.request;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record CreateShipmentRequest(
        @NotBlank String orderId,
        String carrier,
        String trackingNumber,
        String trackingUrl,
        LocalDate estimatedDelivery,
        String notes
) {}
