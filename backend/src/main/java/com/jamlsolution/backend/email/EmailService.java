package com.jamlsolution.backend.email;

import com.jamlsolution.backend.order.order.Order;
import com.jamlsolution.backend.order.payment.Payment;

public interface EmailService {
    void sendOtpEmail(String to, String recipientName, String otp);
    void sendOrderConfirmation(Order order);
    void sendOrderStatusUpdate(Order order);
    void sendPaymentConfirmation(Payment payment);
}
