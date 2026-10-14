package com.jamlsolution.backend.order.shipment.request;

import com.jamlsolution.backend.order.shipment.ShipmentStatus;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UpdateShipmentRequest(
        @NotNull ShipmentStatus status,
        String carrier,
        String trackingNumber,
        String trackingUrl,
        LocalDate estimatedDelivery,
        String notes
) {}
