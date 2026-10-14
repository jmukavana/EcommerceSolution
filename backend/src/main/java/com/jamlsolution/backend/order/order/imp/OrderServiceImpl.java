package com.jamlsolution.backend.order.order.imp;

import com.jamlsolution.backend.exception.BusinessException;
import com.jamlsolution.backend.exception.ErrorCode;
import com.jamlsolution.backend.identity.user.User;
import com.jamlsolution.backend.identity.user.UserRepository;
import com.jamlsolution.backend.order.address.Address;
import com.jamlsolution.backend.order.address.AddressRepository;
import com.jamlsolution.backend.order.cart.Cart;
import com.jamlsolution.backend.order.cart.CartItem;
import com.jamlsolution.backend.order.cart.CartRepository;
import com.jamlsolution.backend.order.order.*;
import com.jamlsolution.backend.order.order.request.PlaceOrderRequest;
import com.jamlsolution.backend.order.order.request.UpdateOrderStatusRequest;
import com.jamlsolution.backend.order.order.response.OrderResponse;
import com.jamlsolution.backend.order.order.response.OrderSummaryResponse;
import com.jamlsolution.backend.product.discount.Coupon;
import com.jamlsolution.backend.product.discount.CouponRepository;
import com.jamlsolution.backend.product.discount.CouponUsage;
import com.jamlsolution.backend.product.discount.CouponUsageRepository;
import com.jamlsolution.backend.product.discount.Discount;
import com.jamlsolution.backend.product.discount.DiscountType;
import com.jamlsolution.backend.product.inventory.InventoryItem;
import com.jamlsolution.backend.product.inventory.InventoryItemRepository;
import com.jamlsolution.backend.product.inventory.MovementType;
import com.jamlsolution.backend.product.inventory.StockMovement;
import com.jamlsolution.backend.product.inventory.StockMovementRepository;
import com.jamlsolution.backend.order.events.OrderPlacedEvent;
import com.jamlsolution.backend.order.events.OrderStatusChangedEvent;
import com.jamlsolution.backend.product.inventory.StockReservationRepository;
import com.jamlsolution.backend.tax.TaxService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final CouponRepository couponRepository;
    private final CouponUsageRepository couponUsageRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final StockMovementRepository stockMovementRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final TaxService taxService;
    private final StockReservationRepository reservationRepository;

    @Override
    public OrderResponse placeOrder(PlaceOrderRequest request) {
        User user = currentUser();

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CART_EMPTY));
        if (cart.getItems().isEmpty()) {
            throw new BusinessException(ErrorCode.CART_EMPTY);
        }

        Address shipping = addressRepository.findById(request.shippingAddressId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ADDRESS_NOT_FOUND));
        Address billing = request.billingAddressId() != null
                ? addressRepository.findById(request.billingAddressId())
                        .orElseThrow(() -> new BusinessException(ErrorCode.ADDRESS_NOT_FOUND))
                : shipping;

        // Validate stock and compute subtotal
        validateStock(cart.getItems());
        BigDecimal subtotal = cart.getItems().stream()
                .map(i -> i.getVariant().getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Apply coupon
        Coupon coupon = null;
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (request.couponCode() != null && !request.couponCode().isBlank()) {
            coupon = couponRepository.findByCode(request.couponCode().toUpperCase())
                    .orElseThrow(() -> new BusinessException(ErrorCode.COUPON_NOT_FOUND));
            validateCoupon(coupon, user);
            discountAmount = calculateDiscount(coupon.getDiscount(), subtotal);
        }

        BigDecimal taxAmount = taxService.calculate(cart.getItems(), shipping.getCountry());
        BigDecimal total = subtotal.subtract(discountAmount).add(taxAmount).max(BigDecimal.ZERO);

        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .user(user)
                .status(OrderStatus.PENDING)
                .items(new ArrayList<>())
                .payments(new ArrayList<>())
                .shipments(new ArrayList<>())
                // shipping snapshot
                .shippingFirstName(shipping.getFirstName())
                .shippingLastName(shipping.getLastName())
                .shippingPhone(shipping.getPhone())
                .shippingLine1(shipping.getLine1())
                .shippingLine2(shipping.getLine2())
                .shippingCity(shipping.getCity())
                .shippingState(shipping.getState())
                .shippingPostalCode(shipping.getPostalCode())
                .shippingCountry(shipping.getCountry())
                // billing snapshot
                .billingFirstName(billing.getFirstName())
                .billingLastName(billing.getLastName())
                .billingPhone(billing.getPhone())
                .billingLine1(billing.getLine1())
                .billingLine2(billing.getLine2())
                .billingCity(billing.getCity())
                .billingState(billing.getState())
                .billingPostalCode(billing.getPostalCode())
                .billingCountry(billing.getCountry())
                .coupon(coupon)
                .couponCode(coupon != null ? coupon.getCode() : null)
                .subtotal(subtotal)
                .discountAmount(discountAmount)
                .shippingAmount(BigDecimal.ZERO)
                .taxAmount(taxAmount)
                .total(total)
                .currency("USD")
                .notes(request.notes())
                .paymentMethod(request.paymentMethod())
                .build();

        Order saved = orderRepository.save(order);

        // Create order items and deduct inventory
        String performer = SecurityContextHolder.getContext().getAuthentication().getName();
        for (CartItem cartItem : cart.getItems()) {
            BigDecimal lineTotal = cartItem.getVariant().getPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            OrderItem orderItem = OrderItem.builder()
                    .order(saved)
                    .productId(cartItem.getVariant().getProduct().getId())
                    .variantId(cartItem.getVariant().getId())
                    .productName(cartItem.getVariant().getProduct().getName())
                    .variantName(cartItem.getVariant().getName())
                    .sku(cartItem.getVariant().getSku())
                    .imageUrl(cartItem.getVariant().getImageUrl())
                    .unitPrice(cartItem.getVariant().getPrice())
                    .quantity(cartItem.getQuantity())
                    .discountAmount(BigDecimal.ZERO)
                    .total(lineTotal)
                    .build();
            orderItemRepository.save(orderItem);
            saved.getItems().add(orderItem);

            deductInventory(cartItem.getVariant().getId(), cartItem.getQuantity(),
                    saved.getId(), performer);
        }

        // Record coupon usage
        if (coupon != null) {
            coupon.setTotalUsageCount(coupon.getTotalUsageCount() + 1);
            couponRepository.save(coupon);
            couponUsageRepository.save(CouponUsage.builder()
                    .coupon(coupon)
                    .user(user)
                    .orderId(saved.getId())
                    .discountAmount(discountAmount)
                    .build());
        }

        // Release reservations and clear cart
        reservationRepository.deleteByCartId(cart.getId());
        cart.getItems().clear();
        cartRepository.save(cart);

        eventPublisher.publishEvent(new OrderPlacedEvent(saved));
        return OrderResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderSummaryResponse> findMyOrders(Pageable pageable) {
        return orderRepository.findByUserId(currentUser().getId(), pageable)
                .map(OrderSummaryResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderSummaryResponse> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable).map(OrderSummaryResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse findById(String id) {
        return OrderResponse.from(getOrThrow(id));
    }

    @Override
    public OrderResponse cancelOrder(String id, String reason) {
        Order order = getOrThrow(id);
        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.CONFIRMED) {
            throw new BusinessException(ErrorCode.ORDER_CANNOT_BE_CANCELLED);
        }
        OrderStatus previous = order.getStatus();
        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelReason(reason);
        order.setCancelledAt(LocalDateTime.now());
        Order saved = orderRepository.save(order);
        eventPublisher.publishEvent(new OrderStatusChangedEvent(saved, previous));
        return OrderResponse.from(saved);
    }

    @Override
    public OrderResponse updateStatus(String id, UpdateOrderStatusRequest request) {
        Order order = getOrThrow(id);
        OrderStatus previous = order.getStatus();
        order.setStatus(request.status());
        if (request.status() == OrderStatus.CONFIRMED) {
            order.setConfirmedAt(LocalDateTime.now());
        }
        if (request.status() == OrderStatus.CANCELLED) {
            order.setCancelReason(request.notes());
            order.setCancelledAt(LocalDateTime.now());
        }
        Order saved = orderRepository.save(order);
        eventPublisher.publishEvent(new OrderStatusChangedEvent(saved, previous));
        return OrderResponse.from(saved);
    }

    private void validateStock(List<CartItem> items) {
        for (CartItem item : items) {
            List<InventoryItem> inventoryItems = inventoryItemRepository
                    .findByVariantId(item.getVariant().getId());
            int totalAvailable = inventoryItems.stream()
                    .mapToInt(InventoryItem::getQuantityAvailable)
                    .sum();
            boolean backorderAllowed = inventoryItems.stream()
                    .anyMatch(InventoryItem::isAllowBackorder);
            if (!backorderAllowed && totalAvailable < item.getQuantity()) {
                throw new BusinessException(ErrorCode.INSUFFICIENT_STOCK);
            }
        }
    }

    private void deductInventory(String variantId, int qty, String orderId, String performer) {
        List<InventoryItem> inventoryItems = inventoryItemRepository.findByVariantId(variantId);
        inventoryItems.sort((a, b) -> Integer.compare(b.getQuantityAvailable(), a.getQuantityAvailable()));

        int remaining = qty;
        for (InventoryItem inv : inventoryItems) {
            if (remaining <= 0) break;
            int deduct = Math.min(remaining, inv.getQuantityAvailable());
            if (deduct <= 0) continue;

            int before = inv.getQuantityOnHand();
            inv.setQuantityOnHand(before - deduct);
            inv.setQuantityAvailable(inv.getQuantityOnHand() - inv.getQuantityReserved());
            inventoryItemRepository.save(inv);

            stockMovementRepository.save(StockMovement.builder()
                    .inventoryItem(inv)
                    .warehouse(inv.getWarehouse())
                    .type(MovementType.SALE)
                    .quantity(-deduct)
                    .quantityBefore(before)
                    .quantityAfter(inv.getQuantityOnHand())
                    .referenceType("ORDER")
                    .referenceId(orderId)
                    .performedBy(performer)
                    .build());

            remaining -= deduct;
        }
    }

    private void validateCoupon(Coupon coupon, User user) {
        if (!coupon.isActive()) throw new BusinessException(ErrorCode.COUPON_INVALID);
        LocalDateTime now = LocalDateTime.now();
        if (coupon.getExpiresAt() != null && coupon.getExpiresAt().isBefore(now))
            throw new BusinessException(ErrorCode.COUPON_EXPIRED);
        if (coupon.getStartsAt() != null && coupon.getStartsAt().isAfter(now))
            throw new BusinessException(ErrorCode.COUPON_INVALID);
        if (coupon.getTotalUsageLimit() != null
                && couponUsageRepository.countByCouponId(coupon.getId()) >= coupon.getTotalUsageLimit())
            throw new BusinessException(ErrorCode.COUPON_USAGE_LIMIT_REACHED);
        if (coupon.getPerUserLimit() != null
                && couponUsageRepository.countByCouponIdAndUserId(coupon.getId(), user.getId()) >= coupon.getPerUserLimit())
            throw new BusinessException(ErrorCode.COUPON_USAGE_LIMIT_REACHED);
    }

    private BigDecimal calculateDiscount(Discount discount, BigDecimal subtotal) {
        return switch (discount.getType()) {
            case PERCENTAGE -> {
                BigDecimal pct = discount.getValue().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
                BigDecimal amount = subtotal.multiply(pct);
                if (discount.getMaxDiscountAmount() != null)
                    amount = amount.min(discount.getMaxDiscountAmount());
                yield amount;
            }
            case FIXED_AMOUNT -> discount.getValue().min(subtotal);
            default -> BigDecimal.ZERO;
        };
    }

    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        return "ORD-" + timestamp;
    }

    private Order getOrThrow(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
    }

    private User currentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
