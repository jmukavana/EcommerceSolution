package com.jamlsolution.backend.mpesa.imp;

import com.fasterxml.jackson.databind.JsonNode;
import com.jamlsolution.backend.mpesa.MpesaProperties;
import com.jamlsolution.backend.mpesa.MpesaService;
import com.jamlsolution.backend.mpesa.dto.MpesaCallback;
import com.jamlsolution.backend.mpesa.dto.StkPushRequest;
import com.jamlsolution.backend.mpesa.dto.StkPushResponse;
import com.jamlsolution.backend.order.events.PaymentReceivedEvent;
import com.jamlsolution.backend.order.order.Order;
import com.jamlsolution.backend.order.order.OrderRepository;
import com.jamlsolution.backend.order.payment.Payment;
import com.jamlsolution.backend.order.payment.PaymentMethod;
import com.jamlsolution.backend.order.payment.PaymentRepository;
import com.jamlsolution.backend.order.payment.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpesaServiceImpl implements MpesaService {

    private final MpesaProperties props;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final ApplicationEventPublisher eventPublisher;

    private static final DateTimeFormatter TIMESTAMP_FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Override
    @Transactional
    public StkPushResponse initiateStkPush(StkPushRequest request) {
        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new RuntimeException("Order not found: " + request.orderId()));

        String token = getAccessToken();
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FMT);
        String password = Base64.getEncoder().encodeToString(
                (props.getShortcode() + props.getPasskey() + timestamp).getBytes(StandardCharsets.UTF_8));

        Map<String, Object> body = new HashMap<>();
        body.put("BusinessShortCode", props.getShortcode());
        body.put("Password", password);
        body.put("Timestamp", timestamp);
        body.put("TransactionType", props.getTransactionType());
        body.put("Amount", request.amount().intValue());
        body.put("PartyA", request.phoneNumber());
        body.put("PartyB", props.getShortcode());
        body.put("PhoneNumber", request.phoneNumber());
        body.put("CallBackURL", props.getCallbackUrl());
        body.put("AccountReference", order.getOrderNumber());
        body.put("TransactionDesc", request.description() != null ? request.description() : "Order " + order.getOrderNumber());

        JsonNode response = RestClient.create(props.getBaseUrl())
                .post()
                .uri("/mpesa/stkpush/v1/processrequest")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(JsonNode.class);

        String checkoutRequestId = response.path("CheckoutRequestID").asText();

        // Create a PENDING payment record, reference = CheckoutRequestID
        Payment payment = Payment.builder()
                .order(order)
                .method(PaymentMethod.MOBILE_MONEY)
                .status(PaymentStatus.PENDING)
                .amount(request.amount())
                .currency("KES")
                .gatewayReference(checkoutRequestId)
                .build();
        paymentRepository.save(payment);

        return new StkPushResponse(
                response.path("MerchantRequestID").asText(),
                checkoutRequestId,
                response.path("ResponseCode").asText(),
                response.path("ResponseDescription").asText(),
                response.path("CustomerMessage").asText()
        );
    }

    @Override
    @Transactional
    public void handleCallback(MpesaCallback callback) {
        var stkCallback = callback.body().stkCallback();
        String checkoutRequestId = stkCallback.checkoutRequestId();

        paymentRepository.findByGatewayReference(checkoutRequestId).ifPresent(payment -> {
            if (stkCallback.resultCode() == 0) {
                // Successful payment
                String mpesaReceipt = extractMetadata(stkCallback, "MpesaReceiptNumber");
                payment.setStatus(PaymentStatus.PAID);
                payment.setPaidAt(LocalDateTime.now());
                payment.setGatewayResponse(mpesaReceipt);
                paymentRepository.save(payment);
                eventPublisher.publishEvent(new PaymentReceivedEvent(payment));
                log.info("M-Pesa payment confirmed for order {}, receipt: {}", payment.getOrder().getOrderNumber(), mpesaReceipt);
            } else {
                payment.setStatus(PaymentStatus.FAILED);
                payment.setGatewayResponse(stkCallback.resultDesc());
                paymentRepository.save(payment);
                log.warn("M-Pesa payment failed for checkout {}: {}", checkoutRequestId, stkCallback.resultDesc());
            }
        });
    }

    private String getAccessToken() {
        String credentials = Base64.getEncoder().encodeToString(
                (props.getConsumerKey() + ":" + props.getConsumerSecret()).getBytes(StandardCharsets.UTF_8));

        JsonNode response = RestClient.create(props.getBaseUrl())
                .get()
                .uri("/oauth/v1/generate?grant_type=client_credentials")
                .header(HttpHeaders.AUTHORIZATION, "Basic " + credentials)
                .retrieve()
                .body(JsonNode.class);

        return response.path("access_token").asText();
    }

    private String extractMetadata(MpesaCallback.StkCallback callback, String key) {
        if (callback.callbackMetadata() == null) return null;
        return callback.callbackMetadata().items().stream()
                .filter(i -> key.equals(i.name()))
                .map(i -> i.value() != null ? i.value().toString() : null)
                .findFirst()
                .orElse(null);
    }
}
