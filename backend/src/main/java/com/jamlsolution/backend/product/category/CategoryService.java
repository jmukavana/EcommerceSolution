package com.jamlsolution.backend.product.category;

import com.jamlsolution.backend.product.category.request.CreateCategoryRequest;
import com.jamlsolution.backend.product.category.response.CategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    CategoryResponse create(CreateCategoryRequest request);
    Page<CategoryResponse> findAll(String search, Pageable pageable);
    CategoryResponse findById(String id);
    List<CategoryResponse> findRoots();
    List<CategoryResponse> findChildren(String parentId);
    CategoryResponse update(String id, CreateCategoryRequest request);
    void delete(String id);
}
