package com.jamlsolution.backend.order.events;

import com.jamlsolution.backend.order.order.Order;

public record OrderPlacedEvent(Order order) {}
