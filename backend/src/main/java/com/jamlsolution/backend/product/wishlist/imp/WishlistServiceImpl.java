package com.jamlsolution.backend.product.wishlist.imp;

import com.jamlsolution.backend.exception.BusinessException;
import com.jamlsolution.backend.exception.ErrorCode;
import com.jamlsolution.backend.identity.user.User;
import com.jamlsolution.backend.identity.user.UserRepository;
import com.jamlsolution.backend.product.product.Product;
import com.jamlsolution.backend.product.product.ProductRepository;
import com.jamlsolution.backend.product.product.ProductVariant;
import com.jamlsolution.backend.product.product.ProductVariantRepository;
import com.jamlsolution.backend.product.wishlist.*;
import com.jamlsolution.backend.product.wishlist.request.AddWishlistItemRequest;
import com.jamlsolution.backend.product.wishlist.request.CreateWishlistRequest;
import com.jamlsolution.backend.product.wishlist.response.WishlistResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final UserRepository userRepository;

    @Override
    public WishlistResponse create(CreateWishlistRequest request) {
        User user = currentUser();
        boolean hasDefault = wishlistRepository.findByUserIdAndIsDefaultTrue(user.getId()).isPresent();
        Wishlist wishlist = Wishlist.builder()
                .user(user)
                .name(request.name())
                .isDefault(!hasDefault)
                .isPublic(request.isPublic())
                .shareToken(UUID.randomUUID().toString())
                .items(new ArrayList<>())
                .build();
        return WishlistResponse.from(wishlistRepository.save(wishlist));
    }

    @Override
    @Transactional(readOnly = true)
    public List<WishlistResponse> findMyWishlists() {
        return wishlistRepository.findByUserId(currentUser().getId())
                .stream().map(WishlistResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public WishlistResponse findById(String id) {
        return WishlistResponse.from(getOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public WishlistResponse findByShareToken(String shareToken) {
        Wishlist wishlist = wishlistRepository.findByShareToken(shareToken)
                .orElseThrow(() -> new BusinessException(ErrorCode.WISHLIST_NOT_FOUND));
        return WishlistResponse.from(wishlist);
    }

    @Override
    public WishlistResponse addItem(String wishlistId, AddWishlistItemRequest request) {
        Wishlist wishlist = getOrThrow(wishlistId);
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        ProductVariant variant = null;
        if (request.variantId() != null) {
            variant = variantRepository.findById(request.variantId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.VARIANT_NOT_FOUND));
        }

        WishlistItem item = WishlistItem.builder()
                .wishlist(wishlist)
                .product(product)
                .variant(variant)
                .notes(request.notes())
                .build();
        wishlistItemRepository.save(item);
        wishlist.getItems().add(item);
        return WishlistResponse.from(wishlist);
    }

    @Override
    public WishlistResponse removeItem(String wishlistId, String itemId) {
        Wishlist wishlist = getOrThrow(wishlistId);
        WishlistItem item = wishlistItemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException(ErrorCode.WISHLIST_ITEM_NOT_FOUND));
        wishlistItemRepository.delete(item);
        wishlist.getItems().remove(item);
        return WishlistResponse.from(wishlist);
    }

    @Override
    public void delete(String id) {
        wishlistRepository.delete(getOrThrow(id));
    }

    private Wishlist getOrThrow(String id) {
        return wishlistRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.WISHLIST_NOT_FOUND));
    }

    private User currentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
