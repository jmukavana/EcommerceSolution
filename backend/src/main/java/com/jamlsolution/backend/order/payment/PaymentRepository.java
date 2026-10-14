package com.jamlsolution.backend.order.payment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, String> {
    List<Payment> findByOrderId(String orderId);
    java.util.Optional<Payment> findByGatewayReference(String gatewayReference);
}
