package com.jamlsolution.backend.product.product;

import com.jamlsolution.backend.common.ApiResponse;
import com.jamlsolution.backend.product.product.request.AddImageRequest;
import com.jamlsolution.backend.product.product.request.CreateProductRequest;
import com.jamlsolution.backend.product.product.request.CreateVariantRequest;
import com.jamlsolution.backend.product.product.response.ProductImageResponse;
import com.jamlsolution.backend.product.product.response.ProductResponse;
import com.jamlsolution.backend.product.product.response.ProductSummaryResponse;
import com.jamlsolution.backend.product.product.response.VariantResponse;
import com.jamlsolution.backend.product.search.ProductDocument;
import com.jamlsolution.backend.product.search.ProductSearchService;
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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;

@Tag(name = "Products", description = "Product catalog management including variants and images")
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductSearchService productSearchService;

    @Operation(summary = "Full-text search products via Elasticsearch",
            description = "Fuzzy multi-field search across name, description, and tags. Falls back to DB if ES is unavailable.")
    @GetMapping("/search")
    public ApiResponse<Page<ProductDocument>> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String brand,
            @PageableDefault(size = 20) Pageable pageable,
            HttpServletRequest httpRequest) {
        return ok(productSearchService.search(q, status, brand, pageable), httpRequest);
    }

    @Operation(summary = "Create product")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ProductResponse> create(
            @Valid @RequestBody CreateProductRequest request,
            HttpServletRequest httpRequest) {
        return ok(productService.create(request), httpRequest);
    }

    @Operation(summary = "List products",
            description = "Filterable by status, type, brand, category, and search term.")
    @GetMapping
    public ApiResponse<Page<ProductSummaryResponse>> findAll(
            @RequestParam(required = false) String search,
            @Parameter(description = "DRAFT | ACTIVE | INACTIVE | ARCHIVED")
            @RequestParam(required = false) ProductStatus status,
            @Parameter(description = "SIMPLE | VARIABLE | DIGITAL | BUNDLE")
            @RequestParam(required = false) ProductType type,
            @RequestParam(required = false) String brandId,
            @RequestParam(required = false) String categoryId,
            @PageableDefault(size = 20, sort = "createdDate") Pageable pageable,
            HttpServletRequest httpRequest) {
        return ok(productService.findAll(search, status, type, brandId, categoryId, pageable), httpRequest);
    }

    @Operation(summary = "Get product by ID")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> findById(
            @PathVariable String id, HttpServletRequest httpRequest) {
        return ok(productService.findById(id), httpRequest);
    }

    @Operation(summary = "Update product")
    @PutMapping("/{id}")
    public ApiResponse<ProductResponse> update(
            @PathVariable String id,
            @Valid @RequestBody CreateProductRequest request,
            HttpServletRequest httpRequest) {
        return ok(productService.update(id, request), httpRequest);
    }

    @Operation(summary = "Delete product")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        productService.delete(id);
    }

    // ── Variants ────────────────────────────────────────────────────────────

    @Operation(summary = "Add variant to product")
    @PostMapping("/{id}/variants")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<VariantResponse> addVariant(
            @PathVariable String id,
            @Valid @RequestBody CreateVariantRequest request,
            HttpServletRequest httpRequest) {
        return ok(productService.addVariant(id, request), httpRequest);
    }

    @Operation(summary = "Update variant")
    @PutMapping("/{id}/variants/{variantId}")
    public ApiResponse<VariantResponse> updateVariant(
            @PathVariable String id,
            @PathVariable String variantId,
            @Valid @RequestBody CreateVariantRequest request,
            HttpServletRequest httpRequest) {
        return ok(productService.updateVariant(id, variantId, request), httpRequest);
    }

    @Operation(summary = "Remove variant from product")
    @DeleteMapping("/{id}/variants/{variantId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeVariant(@PathVariable String id, @PathVariable String variantId) {
        productService.removeVariant(id, variantId);
    }

    // ── Images ───────────────────────────────────────────────────────────────

    @Operation(summary = "Add image to product")
    @PostMapping("/{id}/images")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ProductImageResponse> addImage(
            @PathVariable String id,
            @Valid @RequestBody AddImageRequest request,
            HttpServletRequest httpRequest) {
        return ok(productService.addImage(id, request), httpRequest);
    }

    @Operation(summary = "Upload image file to product (MinIO)")
    @PostMapping(value = "/{id}/images/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ProductImageResponse> uploadImage(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "false") boolean isPrimary,
            @RequestParam(defaultValue = "0") int position,
            HttpServletRequest httpRequest) {
        return ok(productService.uploadImage(id, file, isPrimary, position), httpRequest);
    }

    @Operation(summary = "Remove image from product")
    @DeleteMapping("/{id}/images/{imageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeImage(@PathVariable String id, @PathVariable String imageId) {
        productService.removeImage(id, imageId);
    }

    private <T> ApiResponse<T> ok(T data, HttpServletRequest req) {
        return new ApiResponse<>(true, "Success", data, null, Instant.now(), req.getRequestURI());
    }
}
