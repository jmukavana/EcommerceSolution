package com.jamlsolution.backend.email.imp;

import com.jamlsolution.backend.email.EmailService;
import com.jamlsolution.backend.order.order.Order;
import com.jamlsolution.backend.order.payment.Payment;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${app.email.from-address}")
    private String fromAddress;

    @Value("${app.email.from-name}")
    private String fromName;

    @Value("${app.mfa.otp-expiry-minutes:5}")
    private int otpExpiryMinutes;

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    @Async
    public void sendOtpEmail(String to, String recipientName, String otp) {
        try {
            Context ctx = new Context();
            ctx.setVariable("recipientName", recipientName);
            ctx.setVariable("otp", otp);
            ctx.setVariable("expiryMinutes", otpExpiryMinutes);
            ctx.setVariable("expiryTime", LocalDateTime.now().plusMinutes(otpExpiryMinutes).format(TIME_FMT));

            String html = templateEngine.process("email/otp-email", ctx);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress, fromName);
            helper.setTo(to);
            helper.setSubject("Your One-Time Password");
            helper.setText(html, true);

            mailSender.send(message);
            log.info("OTP email sent to {}", to);
        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            log.error("Failed to send OTP email to {}: {}", to, e.getMessage());
        }
    }

    @Override
    @Async
    public void sendOrderConfirmation(Order order) {
        try {
            Context ctx = new Context();
            ctx.setVariable("order", order);
            ctx.setVariable("recipientName", order.getUser().getFullName());
            String html = templateEngine.process("email/order-confirmation", ctx);
            send(order.getUser().getEmail(), "Order Confirmed – " + order.getOrderNumber(), html);
        } catch (Exception e) {
            log.error("Failed to send order confirmation for {}: {}", order.getOrderNumber(), e.getMessage());
        }
    }

    @Override
    @Async
    public void sendOrderStatusUpdate(Order order) {
        try {
            Context ctx = new Context();
            ctx.setVariable("order", order);
            ctx.setVariable("recipientName", order.getUser().getFullName());
            ctx.setVariable("status", order.getStatus().name());
            String html = templateEngine.process("email/order-status-update", ctx);
            send(order.getUser().getEmail(), "Order Update – " + order.getOrderNumber(), html);
        } catch (Exception e) {
            log.error("Failed to send status update for order {}: {}", order.getOrderNumber(), e.getMessage());
        }
    }

    @Override
    @Async
    public void sendPaymentConfirmation(Payment payment) {
        try {
            Context ctx = new Context();
            ctx.setVariable("payment", payment);
            ctx.setVariable("order", payment.getOrder());
            ctx.setVariable("recipientName", payment.getOrder().getUser().getFullName());
            String html = templateEngine.process("email/payment-confirmation", ctx);
            send(payment.getOrder().getUser().getEmail(),
                    "Payment Received – " + payment.getOrder().getOrderNumber(), html);
        } catch (Exception e) {
            log.error("Failed to send payment confirmation for payment {}: {}", payment.getId(), e.getMessage());
        }
    }

    private void send(String to, String subject, String html) throws MessagingException, java.io.UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(fromAddress, fromName);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);
        mailSender.send(message);
        log.info("Email sent to {}: {}", to, subject);
    }
}
