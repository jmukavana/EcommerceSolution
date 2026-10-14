package com.jamlsolution.backend.order.shipment.imp;

import com.jamlsolution.backend.exception.BusinessException;
import com.jamlsolution.backend.exception.ErrorCode;
import com.jamlsolution.backend.order.order.Order;
import com.jamlsolution.backend.order.order.OrderRepository;
import com.jamlsolution.backend.order.order.OrderStatus;
import com.jamlsolution.backend.order.shipment.*;
import com.jamlsolution.backend.order.shipment.request.CreateShipmentRequest;
import com.jamlsolution.backend.order.shipment.request.UpdateShipmentRequest;
import com.jamlsolution.backend.order.shipment.response.ShipmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ShipmentServiceImpl implements ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final OrderRepository orderRepository;

    @Override
    public ShipmentResponse create(CreateShipmentRequest request) {
        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        Shipment shipment = Shipment.builder()
                .order(order)
                .status(ShipmentStatus.PROCESSING)
                .carrier(request.carrier())
                .trackingNumber(request.trackingNumber())
                .trackingUrl(request.trackingUrl())
                .estimatedDelivery(request.estimatedDelivery())
                .notes(request.notes())
                .build();

        Shipment saved = shipmentRepository.save(shipment);

        // Move order to PROCESS if still CONFIRMED
        if (order.getStatus() == OrderStatus.CONFIRMED) {
            order.setStatus(OrderStatus.PROCESSING);
            orderRepository.save(order);
        }

        return ShipmentResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShipmentResponse> findByOrder(String orderId) {
        return shipmentRepository.findByOrderId(orderId).stream()
                .map(ShipmentResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ShipmentResponse findById(String id) {
        return ShipmentResponse.from(getOrThrow(id));
    }

    @Override
    public ShipmentResponse update(String id, UpdateShipmentRequest request) {
        Shipment shipment = getOrThrow(id);
        Order order = shipment.getOrder();

        shipment.setStatus(request.status());
        if (request.carrier() != null) shipment.setCarrier(request.carrier());
        if (request.trackingNumber() != null) shipment.setTrackingNumber(request.trackingNumber());
        if (request.trackingUrl() != null) shipment.setTrackingUrl(request.trackingUrl());
        if (request.estimatedDelivery() != null) shipment.setEstimatedDelivery(request.estimatedDelivery());
        if (request.notes() != null) shipment.setNotes(request.notes());

        if (request.status() == ShipmentStatus.SHIPPED && shipment.getShippedAt() == null) {
            shipment.setShippedAt(LocalDateTime.now());
            if (order.getStatus() == OrderStatus.PROCESSING) {
                order.setStatus(OrderStatus.SHIPPED);
                orderRepository.save(order);
            }
        }
        if (request.status() == ShipmentStatus.DELIVERED && shipment.getDeliveredAt() == null) {
            shipment.setDeliveredAt(LocalDateTime.now());
            if (order.getStatus() == OrderStatus.SHIPPED) {
                order.setStatus(OrderStatus.DELIVERED);
                orderRepository.save(order);
            }
        }

        return ShipmentResponse.from(shipmentRepository.save(shipment));
    }

    private Shipment getOrThrow(String id) {
        return shipmentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.SHIPMENT_NOT_FOUND));
    }
}
