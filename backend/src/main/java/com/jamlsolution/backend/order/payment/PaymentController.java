package com.jamlsolution.backend.order.payment;

import com.jamlsolution.backend.common.ApiResponse;
import com.jamlsolution.backend.order.payment.request.RecordPaymentRequest;
import com.jamlsolution.backend.order.payment.request.UpdatePaymentStatusRequest;
import com.jamlsolution.backend.order.payment.response.PaymentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@Tag(name = "Payments", description = "Payment management")
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "Record payment for an order")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PaymentResponse> record(
            @Valid @RequestBody RecordPaymentRequest request,
            HttpServletRequest httpRequest) {
        return ok(paymentService.recordPayment(request), httpRequest);
    }

    @Operation(summary = "Get payments for an order")
    @GetMapping("/order/{orderId}")
    public ApiResponse<List<PaymentResponse>> findByOrder(
            @PathVariable String orderId, HttpServletRequest httpRequest) {
        return ok(paymentService.findByOrder(orderId), httpRequest);
    }

    @Operation(summary = "Get payment by ID")
    @GetMapping("/{id}")
    public ApiResponse<PaymentResponse> findById(
            @PathVariable String id, HttpServletRequest httpRequest) {
        return ok(paymentService.findById(id), httpRequest);
    }

    @Operation(summary = "Update payment status (admin)")
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PaymentResponse> updateStatus(
            @PathVariable String id,
            @Valid @RequestBody UpdatePaymentStatusRequest request,
            HttpServletRequest httpRequest) {
        return ok(paymentService.updateStatus(id, request), httpRequest);
    }

    private <T> ApiResponse<T> ok(T data, HttpServletRequest req) {
        return new ApiResponse<>(true, "Success", data, null, Instant.now(), req.getRequestURI());
    }
}
