package com.jamlsolution.backend.order.events;

import com.jamlsolution.backend.order.order.Order;
import com.jamlsolution.backend.order.order.OrderStatus;

public record OrderStatusChangedEvent(Order order, OrderStatus previousStatus) {}
