package com.jamlsolution.backend.order.payment;

import com.jamlsolution.backend.order.payment.request.RecordPaymentRequest;
import com.jamlsolution.backend.order.payment.request.UpdatePaymentStatusRequest;
import com.jamlsolution.backend.order.payment.response.PaymentResponse;

import java.util.List;

public interface PaymentService {
    PaymentResponse recordPayment(RecordPaymentRequest request);
    List<PaymentResponse> findByOrder(String orderId);
    PaymentResponse findById(String id);
    PaymentResponse updateStatus(String id, UpdatePaymentStatusRequest request);
}
