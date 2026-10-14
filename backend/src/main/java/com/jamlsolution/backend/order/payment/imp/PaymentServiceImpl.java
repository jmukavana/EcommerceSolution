package com.jamlsolution.backend.order.payment.imp;

import com.jamlsolution.backend.exception.BusinessException;
import com.jamlsolution.backend.exception.ErrorCode;
import com.jamlsolution.backend.order.order.Order;
import com.jamlsolution.backend.order.order.OrderRepository;
import com.jamlsolution.backend.order.order.OrderStatus;
import com.jamlsolution.backend.order.payment.*;
import com.jamlsolution.backend.order.payment.request.RecordPaymentRequest;
import com.jamlsolution.backend.order.payment.request.UpdatePaymentStatusRequest;
import com.jamlsolution.backend.order.payment.response.PaymentResponse;
import com.jamlsolution.backend.order.events.PaymentReceivedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public PaymentResponse recordPayment(RecordPaymentRequest request) {
        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        Payment payment = Payment.builder()
                .order(order)
                .method(request.method())
                .status(PaymentStatus.PAID)
                .amount(request.amount())
                .currency(request.currency() != null ? request.currency() : "USD")
                .gatewayReference(request.gatewayReference())
                .gatewayResponse(request.gatewayResponse())
                .notes(request.notes())
                .paidAt(LocalDateTime.now())
                .build();

        Payment saved = paymentRepository.save(payment);

        // Auto-confirm order if it's still pending
        if (order.getStatus() == OrderStatus.PENDING) {
            order.setStatus(OrderStatus.CONFIRMED);
            order.setConfirmedAt(LocalDateTime.now());
            orderRepository.save(order);
        }

        eventPublisher.publishEvent(new PaymentReceivedEvent(saved));
        return PaymentResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> findByOrder(String orderId) {
        return paymentRepository.findByOrderId(orderId).stream()
                .map(PaymentResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse findById(String id) {
        return PaymentResponse.from(getOrThrow(id));
    }

    @Override
    public PaymentResponse updateStatus(String id, UpdatePaymentStatusRequest request) {
        Payment payment = getOrThrow(id);
        payment.setStatus(request.status());
        if (request.status() == PaymentStatus.PAID && payment.getPaidAt() == null) {
            payment.setPaidAt(LocalDateTime.now());
        }
        return PaymentResponse.from(paymentRepository.save(payment));
    }

    private Payment getOrThrow(String id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));
    }
}
