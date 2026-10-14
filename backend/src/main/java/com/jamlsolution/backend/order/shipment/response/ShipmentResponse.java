package com.jamlsolution.backend.order.shipment.response;

import com.jamlsolution.backend.order.shipment.Shipment;
import com.jamlsolution.backend.order.shipment.ShipmentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ShipmentResponse(
        String id,
        String orderId,
        String orderNumber,
        ShipmentStatus status,
        String carrier,
        String trackingNumber,
        String trackingUrl,
        LocalDate estimatedDelivery,
        LocalDateTime shippedAt,
        LocalDateTime deliveredAt,
        String notes,
        LocalDateTime createdDate
) {
    public static ShipmentResponse from(Shipment s) {
        return new ShipmentResponse(
                s.getId(), s.getOrder().getId(), s.getOrder().getOrderNumber(),
                s.getStatus(), s.getCarrier(), s.getTrackingNumber(), s.getTrackingUrl(),
                s.getEstimatedDelivery(), s.getShippedAt(), s.getDeliveredAt(),
                s.getNotes(), s.getCreatedDate());
    }
}
