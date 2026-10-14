package com.jamlsolution.backend.product.category;

import com.jamlsolution.backend.common.ApiResponse;
import com.jamlsolution.backend.product.category.request.CreateCategoryRequest;
import com.jamlsolution.backend.product.category.response.CategoryResponse;
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
import java.util.List;

@Tag(name = "Categories", description = "Product category management (tree structure)")
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Create category")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CategoryResponse> create(
            @Valid @RequestBody CreateCategoryRequest request,
            HttpServletRequest httpRequest) {
        return ok(categoryService.create(request), httpRequest);
    }

    @Operation(summary = "List categories (paginated)")
    @GetMapping
    public ApiResponse<Page<CategoryResponse>> findAll(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "sortOrder") Pageable pageable,
            HttpServletRequest httpRequest) {
        return ok(categoryService.findAll(search, pageable), httpRequest);
    }

    @Operation(summary = "Get root categories (no parent)")
    @GetMapping("/roots")
    public ApiResponse<List<CategoryResponse>> findRoots(HttpServletRequest httpRequest) {
        return ok(categoryService.findRoots(), httpRequest);
    }

    @Operation(summary = "Get children of a category")
    @GetMapping("/{id}/children")
    public ApiResponse<List<CategoryResponse>> findChildren(
            @PathVariable String id, HttpServletRequest httpRequest) {
        return ok(categoryService.findChildren(id), httpRequest);
    }

    @Operation(summary = "Get category by ID")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ApiResponse<CategoryResponse> findById(
            @PathVariable String id, HttpServletRequest httpRequest) {
        return ok(categoryService.findById(id), httpRequest);
    }

    @Operation(summary = "Update category")
    @PutMapping("/{id}")
    public ApiResponse<CategoryResponse> update(
            @PathVariable String id,
            @Valid @RequestBody CreateCategoryRequest request,
            HttpServletRequest httpRequest) {
        return ok(categoryService.update(id, request), httpRequest);
    }

    @Operation(summary = "Delete category")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        categoryService.delete(id);
    }

    private <T> ApiResponse<T> ok(T data, HttpServletRequest req) {
        return new ApiResponse<>(true, "Success", data, null, Instant.now(), req.getRequestURI());
    }
}
