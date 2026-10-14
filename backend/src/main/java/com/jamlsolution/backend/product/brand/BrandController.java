package com.jamlsolution.backend.product.brand;

import com.jamlsolution.backend.common.ApiResponse;
import com.jamlsolution.backend.product.brand.request.CreateBrandRequest;
import com.jamlsolution.backend.product.brand.response.BrandResponse;
import io.swagger.v3.oas.annotations.Operation;
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

@Tag(name = "Brands", description = "Brand management")
@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @Operation(summary = "Create brand")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<BrandResponse> create(
            @Valid @RequestBody CreateBrandRequest request,
            HttpServletRequest httpRequest) {
        return ok(brandService.create(request), httpRequest);
    }

    @Operation(summary = "List brands")
    @GetMapping
    public ApiResponse<Page<BrandResponse>> findAll(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "name") Pageable pageable,
            HttpServletRequest httpRequest) {
        return ok(brandService.findAll(search, pageable), httpRequest);
    }

    @Operation(summary = "Get brand by ID")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ApiResponse<BrandResponse> findById(
            @PathVariable String id, HttpServletRequest httpRequest) {
        return ok(brandService.findById(id), httpRequest);
    }

    @Operation(summary = "Update brand")
    @PutMapping("/{id}")
    public ApiResponse<BrandResponse> update(
            @PathVariable String id,
            @Valid @RequestBody CreateBrandRequest request,
            HttpServletRequest httpRequest) {
        return ok(brandService.update(id, request), httpRequest);
    }

    @Operation(summary = "Delete brand")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        brandService.delete(id);
    }

    private <T> ApiResponse<T> ok(T data, HttpServletRequest req) {
        return new ApiResponse<>(true, "Success", data, null, Instant.now(), req.getRequestURI());
    }
}
