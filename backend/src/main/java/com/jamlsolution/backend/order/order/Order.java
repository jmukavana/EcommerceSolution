package com.jamlsolution.backend.order.order;

import com.jamlsolution.backend.common.BaseEntity;
import com.jamlsolution.backend.identity.user.User;
import com.jamlsolution.backend.order.payment.Payment;
import com.jamlsolution.backend.order.payment.PaymentMethod;
import com.jamlsolution.backend.order.shipment.Shipment;
import com.jamlsolution.backend.product.discount.Coupon;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Order extends BaseEntity {

    @Column(name = "order_number", unique = true, nullable = false)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    // Shipping address snapshot
    @Column(name = "shipping_first_name") private String shippingFirstName;
    @Column(name = "shipping_last_name")  private String shippingLastName;
    @Column(name = "shipping_phone")      private String shippingPhone;
    @Column(name = "shipping_line1")      private String shippingLine1;
    @Column(name = "shipping_line2")      private String shippingLine2;
    @Column(name = "shipping_city")       private String shippingCity;
    @Column(name = "shipping_state")      private String shippingState;
    @Column(name = "shipping_postal_code") private String shippingPostalCode;
    @Column(name = "shipping_country")    private String shippingCountry;

    // Billing address snapshot
    @Column(name = "billing_first_name")  private String billingFirstName;
    @Column(name = "billing_last_name")   private String billingLastName;
    @Column(name = "billing_phone")       private String billingPhone;
    @Column(name = "billing_line1")       private String billingLine1;
    @Column(name = "billing_line2")       private String billingLine2;
    @Column(name = "billing_city")        private String billingCity;
    @Column(name = "billing_state")       private String billingState;
    @Column(name = "billing_postal_code") private String billingPostalCode;
    @Column(name = "billing_country")     private String billingCountry;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @Column(name = "coupon_code")
    private String couponCode;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal subtotal;

    @Column(name = "discount_amount", precision = 19, scale = 4)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "shipping_amount", precision = 19, scale = 4)
    private BigDecimal shippingAmount = BigDecimal.ZERO;

    @Column(name = "tax_amount", precision = 19, scale = 4)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal total;

    @Column(nullable = false)
    private String currency = "USD";

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(name = "cancel_reason")
    private String cancelReason;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<Payment> payments = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<Shipment> shipments = new ArrayList<>();
}
