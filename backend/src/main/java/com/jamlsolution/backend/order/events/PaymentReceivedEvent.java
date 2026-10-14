package com.jamlsolution.backend.order.events;

import com.jamlsolution.backend.order.payment.Payment;

public record PaymentReceivedEvent(Payment payment) {}
