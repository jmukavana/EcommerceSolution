package com.jamlsolution.backend.product.wishlist;

import com.jamlsolution.backend.product.wishlist.request.AddWishlistItemRequest;
import com.jamlsolution.backend.product.wishlist.request.CreateWishlistRequest;
import com.jamlsolution.backend.product.wishlist.response.WishlistResponse;

import java.util.List;

public interface WishlistService {
    WishlistResponse create(CreateWishlistRequest request);
    List<WishlistResponse> findMyWishlists();
    WishlistResponse findById(String id);
    WishlistResponse findByShareToken(String shareToken);
    WishlistResponse addItem(String wishlistId, AddWishlistItemRequest request);
    WishlistResponse removeItem(String wishlistId, String itemId);
    void delete(String id);
}
