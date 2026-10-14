package com.jamlsolution.backend.product.review;

import com.jamlsolution.backend.common.ApiResponse;
import com.jamlsolution.backend.product.review.request.CreateReviewRequest;
import com.jamlsolution.backend.product.review.response.ReviewResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@Tag(name = "Reviews", description = "Product reviews and helpfulness voting")
@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "Submit a review", description = "Authenticated user submits a review. Status starts as PENDING.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ReviewResponse> create(
            @Valid @RequestBody CreateReviewRequest request,
            HttpServletRequest httpRequest) {
        return ok(reviewService.create(request), httpRequest);
    }

    @Operation(summary = "List reviews for a product")
    @GetMapping("/product/{productId}")
    public ApiResponse<Page<ReviewResponse>> findByProduct(
            @PathVariable String productId,
            @Parameter(description = "PENDING | APPROVED | REJECTED")
            @RequestParam(required = false) ReviewStatus status,
            @PageableDefault(size = 20, sort = "createdDate") Pageable pageable,
            HttpServletRequest httpRequest) {
        return ok(reviewService.findByProduct(productId, status, pageable), httpRequest);
    }

    @Operation(summary = "Get review by ID")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ApiResponse<ReviewResponse> findById(
            @PathVariable String id, HttpServletRequest httpRequest) {
        return ok(reviewService.findById(id), httpRequest);
    }

    @Operation(summary = "Approve a review (admin)")
    @PatchMapping("/{id}/approve")
    public ApiResponse<ReviewResponse> approve(
            @PathVariable String id, HttpServletRequest httpRequest) {
        return ok(reviewService.approve(id), httpRequest);
    }

    @Operation(summary = "Reject a review (admin)")
    @PatchMapping("/{id}/reject")
    public ApiResponse<ReviewResponse> reject(
            @PathVariable String id, HttpServletRequest httpRequest) {
        return ok(reviewService.reject(id), httpRequest);
    }

    @Operation(summary = "Vote whether a review was helpful")
    @PostMapping("/{id}/helpful")
    public ApiResponse<ReviewResponse> markHelpful(
            @PathVariable String id,
            @Parameter(description = "true = helpful, false = not helpful")
            @RequestParam boolean helpful,
            HttpServletRequest httpRequest) {
        return ok(reviewService.markHelpful(id, helpful), httpRequest);
    }

    @Operation(summary = "Delete review")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        reviewService.delete(id);
    }

    private <T> ApiResponse<T> ok(T data, HttpServletRequest req) {
        return new ApiResponse<>(true, "Success", data, null, Instant.now(), req.getRequestURI());
    }
}
