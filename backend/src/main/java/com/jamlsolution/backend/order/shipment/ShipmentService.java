package com.jamlsolution.backend.order.shipment;

import com.jamlsolution.backend.order.shipment.request.CreateShipmentRequest;
import com.jamlsolution.backend.order.shipment.request.UpdateShipmentRequest;
import com.jamlsolution.backend.order.shipment.response.ShipmentResponse;

import java.util.List;

public interface ShipmentService {
    ShipmentResponse create(CreateShipmentRequest request);
    List<ShipmentResponse> findByOrder(String orderId);
    ShipmentResponse findById(String id);
    ShipmentResponse update(String id, UpdateShipmentRequest request);
}
