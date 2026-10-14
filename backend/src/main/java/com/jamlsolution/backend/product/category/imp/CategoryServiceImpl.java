package com.jamlsolution.backend.product.category.imp;

import com.jamlsolution.backend.common.PageableUtils;
import com.jamlsolution.backend.exception.BusinessException;
import com.jamlsolution.backend.exception.ErrorCode;
import com.jamlsolution.backend.product.category.Category;
import com.jamlsolution.backend.product.category.CategoryRepository;
import com.jamlsolution.backend.product.category.CategoryService;
import com.jamlsolution.backend.product.category.CategorySpecification;
import com.jamlsolution.backend.product.category.request.CreateCategoryRequest;
import com.jamlsolution.backend.product.category.response.CategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private static final Set<String> SORT_FIELDS = Set.of("name", "slug", "sortOrder", "active", "createdDate");

    @Override
    public CategoryResponse create(CreateCategoryRequest request) {
        String slug = resolveSlug(request.slug(), request.name());
        if (categoryRepository.existsBySlug(slug)) {
            throw new BusinessException(ErrorCode.CATEGORY_SLUG_EXISTS);
        }
        Category.CategoryBuilder<?, ?> builder = Category.builder()
                .name(request.name())
                .slug(slug)
                .description(request.description())
                .imageUrl(request.imageUrl())
                .sortOrder(request.sortOrder())
                .active(true);
        if (request.parentId() != null) {
            builder.parent(getOrThrow(request.parentId()));
        }
        return CategoryResponse.from(categoryRepository.save(builder.build()));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryResponse> findAll(String search, Pageable pageable) {
        Pageable safe = PageableUtils.sanitize(pageable, SORT_FIELDS, "name");
        return categoryRepository.findAll(CategorySpecification.build(search), safe)
                .map(CategoryResponse::shallow);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse findById(String id) {
        return CategoryResponse.from(getOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> findRoots() {
        return categoryRepository.findByParentIsNull().stream()
                .map(CategoryResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> findChildren(String parentId) {
        return categoryRepository.findByParentId(parentId).stream()
                .map(CategoryResponse::shallow).toList();
    }

    @Override
    public CategoryResponse update(String id, CreateCategoryRequest request) {
        Category category = getOrThrow(id);
        String slug = resolveSlug(request.slug(), request.name());
        if (!slug.equals(category.getSlug()) && categoryRepository.existsBySlug(slug)) {
            throw new BusinessException(ErrorCode.CATEGORY_SLUG_EXISTS);
        }
        category.setName(request.name());
        category.setSlug(slug);
        category.setDescription(request.description());
        category.setImageUrl(request.imageUrl());
        category.setSortOrder(request.sortOrder());
        if (request.parentId() != null) {
            category.setParent(getOrThrow(request.parentId()));
        } else {
            category.setParent(null);
        }
        return CategoryResponse.from(categoryRepository.save(category));
    }

    @Override
    public void delete(String id) {
        categoryRepository.delete(getOrThrow(id));
    }

    private Category getOrThrow(String id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    private String resolveSlug(String provided, String name) {
        if (provided != null && !provided.isBlank()) return provided.toLowerCase().trim();
        return name.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
    }
}
