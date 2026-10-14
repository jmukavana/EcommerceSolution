package com.jamlsolution.backend.product.wishlist;

import com.jamlsolution.backend.common.ApiResponse;
import com.jamlsolution.backend.product.wishlist.request.AddWishlistItemRequest;
import com.jamlsolution.backend.product.wishlist.request.CreateWishlistRequest;
import com.jamlsolution.backend.product.wishlist.response.WishlistResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@Tag(name = "Wishlists", description = "User wishlist management")
@RestController
@RequestMapping("/api/v1/wishlists")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @Operation(summary = "Create a wishlist")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<WishlistResponse> create(
            @Valid @RequestBody CreateWishlistRequest request,
            HttpServletRequest httpRequest) {
        return ok(wishlistService.create(request), httpRequest);
    }

    @Operation(summary = "Get all wishlists for the current user")
    @GetMapping("/my")
    public ApiResponse<List<WishlistResponse>> findMyWishlists(HttpServletRequest httpRequest) {
        return ok(wishlistService.findMyWishlists(), httpRequest);
    }

    @Operation(summary = "Get wishlist by ID")
    @GetMapping("/{id}")
    public ApiResponse<WishlistResponse> findById(
            @PathVariable String id, HttpServletRequest httpRequest) {
        return ok(wishlistService.findById(id), httpRequest);
    }

    @Operation(summary = "Get public wishlist by share token")
    @GetMapping("/shared/{token}")
    public ApiResponse<WishlistResponse> findByShareToken(
            @PathVariable String token, HttpServletRequest httpRequest) {
        return ok(wishlistService.findByShareToken(token), httpRequest);
    }

    @Operation(summary = "Add item to wishlist")
    @PostMapping("/{id}/items")
    public ApiResponse<WishlistResponse> addItem(
            @PathVariable String id,
            @Valid @RequestBody AddWishlistItemRequest request,
            HttpServletRequest httpRequest) {
        return ok(wishlistService.addItem(id, request), httpRequest);
    }

    @Operation(summary = "Remove item from wishlist")
    @DeleteMapping("/{id}/items/{itemId}")
    public ApiResponse<WishlistResponse> removeItem(
            @PathVariable String id,
            @PathVariable String itemId,
            HttpServletRequest httpRequest) {
        return ok(wishlistService.removeItem(id, itemId), httpRequest);
    }

    @Operation(summary = "Delete wishlist")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        wishlistService.delete(id);
    }

    private <T> ApiResponse<T> ok(T data, HttpServletRequest req) {
        return new ApiResponse<>(true, "Success", data, null, Instant.now(), req.getRequestURI());
    }
}
