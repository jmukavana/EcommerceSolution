package com.jamlsolution.backend.order.order;

import com.jamlsolution.backend.order.order.request.PlaceOrderRequest;
import com.jamlsolution.backend.order.order.request.UpdateOrderStatusRequest;
import com.jamlsolution.backend.order.order.response.OrderResponse;
import com.jamlsolution.backend.order.order.response.OrderSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponse placeOrder(PlaceOrderRequest request);
    Page<OrderSummaryResponse> findMyOrders(Pageable pageable);
    Page<OrderSummaryResponse> findAll(Pageable pageable);
    OrderResponse findById(String id);
    OrderResponse cancelOrder(String id, String reason);
    OrderResponse updateStatus(String id, UpdateOrderStatusRequest request);
}
