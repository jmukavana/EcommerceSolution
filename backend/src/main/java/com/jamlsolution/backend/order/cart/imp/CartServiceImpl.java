package com.jamlsolution.backend.order.cart.imp;

import com.jamlsolution.backend.exception.BusinessException;
import com.jamlsolution.backend.exception.ErrorCode;
import com.jamlsolution.backend.identity.user.User;
import com.jamlsolution.backend.identity.user.UserRepository;
import com.jamlsolution.backend.order.cart.*;
import com.jamlsolution.backend.order.cart.request.AddCartItemRequest;
import com.jamlsolution.backend.order.cart.request.UpdateCartItemRequest;
import com.jamlsolution.backend.order.cart.response.CartResponse;
import com.jamlsolution.backend.product.inventory.InventoryItemRepository;
import com.jamlsolution.backend.product.inventory.StockReservation;
import com.jamlsolution.backend.product.inventory.StockReservationRepository;
import com.jamlsolution.backend.product.product.ProductVariant;
import com.jamlsolution.backend.product.product.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository variantRepository;
    private final UserRepository userRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final StockReservationRepository reservationRepository;

    // Reservations last 30 minutes; a scheduler cleans expired ones
    private static final int RESERVATION_MINUTES = 30;

    @Override
    @Transactional(readOnly = true)
    public CartResponse getMyCart() {
        User user = currentUser();
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseGet(() -> Cart.builder().user(user).items(new ArrayList<>()).build());
        return CartResponse.from(cart);
    }

    @Override
    public CartResponse addItem(AddCartItemRequest request) {
        User user = currentUser();
        ProductVariant variant = variantRepository.findById(request.variantId())
                .orElseThrow(() -> new BusinessException(ErrorCode.VARIANT_NOT_FOUND));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseGet(() -> cartRepository.save(
                        Cart.builder().user(user).items(new ArrayList<>()).build()));

        // Validate and reserve stock
        int totalAvailable = inventoryItemRepository.findByVariantId(variant.getId())
                .stream().mapToInt(i -> i.getQuantityOnHand() - i.getQuantityReserved()).sum();
        int alreadyReserved = reservationRepository
                .findByVariantIdAndCartId(variant.getId(), cart.getId())
                .map(StockReservation::getQuantity).orElse(0);
        int newTotal = request.quantity() + alreadyReserved;

        if (newTotal > totalAvailable + alreadyReserved) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_STOCK);
        }

        cartItemRepository.findByCartIdAndVariantId(cart.getId(), variant.getId())
                .ifPresentOrElse(
                        existing -> existing.setQuantity(existing.getQuantity() + request.quantity()),
                        () -> {
                            CartItem newItem = CartItem.builder()
                                    .cart(cart)
                                    .variant(variant)
                                    .quantity(request.quantity())
                                    .unitPrice(variant.getPrice())
                                    .build();
                            cart.getItems().add(cartItemRepository.save(newItem));
                        });

        upsertReservation(variant.getId(), cart.getId(), newTotal);

        return CartResponse.from(cartRepository.save(cart));
    }

    @Override
    public CartResponse updateItem(String itemId, UpdateCartItemRequest request) {
        CartItem item = getItemOrThrow(itemId);
        String cartId = item.getCart().getId();
        String variantId = item.getVariant().getId();

        int totalAvailable = inventoryItemRepository.findByVariantId(variantId)
                .stream().mapToInt(i -> i.getQuantityOnHand() - i.getQuantityReserved()).sum();
        int currentReserved = reservationRepository
                .findByVariantIdAndCartId(variantId, cartId)
                .map(StockReservation::getQuantity).orElse(0);
        int freeStock = totalAvailable + currentReserved;

        if (request.quantity() > freeStock) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_STOCK);
        }

        item.setQuantity(request.quantity());
        cartItemRepository.save(item);
        upsertReservation(variantId, cartId, request.quantity());

        return CartResponse.from(cartRepository.findById(cartId).orElseThrow());
    }

    @Override
    public CartResponse removeItem(String itemId) {
        CartItem item = getItemOrThrow(itemId);
        Cart cart = item.getCart();
        reservationRepository.findByVariantIdAndCartId(item.getVariant().getId(), cart.getId())
                .ifPresent(reservationRepository::delete);
        cart.getItems().remove(item);
        cartItemRepository.delete(item);
        return CartResponse.from(cartRepository.save(cart));
    }

    @Override
    public void clearCart() {
        cartRepository.findByUserId(currentUser().getId()).ifPresent(cart -> {
            reservationRepository.deleteByCartId(cart.getId());
            cart.getItems().clear();
            cartRepository.save(cart);
        });
    }

    private void upsertReservation(String variantId, String cartId, int quantity) {
        StockReservation reservation = reservationRepository
                .findByVariantIdAndCartId(variantId, cartId)
                .orElseGet(() -> StockReservation.builder()
                        .variantId(variantId)
                        .cartId(cartId)
                        .build());
        reservation.setQuantity(quantity);
        reservation.setExpiresAt(LocalDateTime.now().plusMinutes(RESERVATION_MINUTES));
        reservationRepository.save(reservation);
    }

    private CartItem getItemOrThrow(String itemId) {
        return cartItemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND));
    }

    private User currentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
