package com.jamlsolution.backend.notification;

import com.jamlsolution.backend.email.EmailService;
import com.jamlsolution.backend.order.events.OrderPlacedEvent;
import com.jamlsolution.backend.order.events.OrderStatusChangedEvent;
import com.jamlsolution.backend.order.events.PaymentReceivedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final EmailService emailService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void onOrderPlaced(OrderPlacedEvent event) {
        emailService.sendOrderConfirmation(event.order());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void onOrderStatusChanged(OrderStatusChangedEvent event) {
        emailService.sendOrderStatusUpdate(event.order());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void onPaymentReceived(PaymentReceivedEvent event) {
        emailService.sendPaymentConfirmation(event.payment());
    }
}
