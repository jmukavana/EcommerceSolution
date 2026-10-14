package com.jamlsolution.backend.product.brand.imp;

import com.jamlsolution.backend.common.PageableUtils;
import com.jamlsolution.backend.exception.BusinessException;
import com.jamlsolution.backend.exception.ErrorCode;
import com.jamlsolution.backend.product.brand.Brand;
import com.jamlsolution.backend.product.brand.BrandRepository;
import com.jamlsolution.backend.product.brand.BrandService;
import com.jamlsolution.backend.product.brand.BrandSpecification;
import com.jamlsolution.backend.product.brand.request.CreateBrandRequest;
import com.jamlsolution.backend.product.brand.response.BrandResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;

    private static final Set<String> SORT_FIELDS = Set.of("name", "slug", "active", "createdDate");

    @Override
    public BrandResponse create(CreateBrandRequest request) {
        if (brandRepository.existsByName(request.name())) {
            throw new BusinessException(ErrorCode.BRAND_NAME_EXISTS);
        }
        String slug = resolveSlug(request.slug(), request.name());
        if (brandRepository.existsBySlug(slug)) {
            throw new BusinessException(ErrorCode.BRAND_SLUG_EXISTS);
        }
        Brand brand = Brand.builder()
                .name(request.name())
                .slug(slug)
                .description(request.description())
                .logoUrl(request.logoUrl())
                .websiteUrl(request.websiteUrl())
                .active(true)
                .build();
        return BrandResponse.from(brandRepository.save(brand));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BrandResponse> findAll(String search, Pageable pageable) {
        Pageable safe = PageableUtils.sanitize(pageable, SORT_FIELDS, "name");
        return brandRepository.findAll(BrandSpecification.build(search), safe).map(BrandResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public BrandResponse findById(String id) {
        return BrandResponse.from(getOrThrow(id));
    }

    @Override
    public BrandResponse update(String id, CreateBrandRequest request) {
        Brand brand = getOrThrow(id);
        if (!brand.getName().equals(request.name()) && brandRepository.existsByName(request.name())) {
            throw new BusinessException(ErrorCode.BRAND_NAME_EXISTS);
        }
        String slug = resolveSlug(request.slug(), request.name());
        if (!slug.equals(brand.getSlug()) && brandRepository.existsBySlug(slug)) {
            throw new BusinessException(ErrorCode.BRAND_SLUG_EXISTS);
        }
        brand.setName(request.name());
        brand.setSlug(slug);
        brand.setDescription(request.description());
        brand.setLogoUrl(request.logoUrl());
        brand.setWebsiteUrl(request.websiteUrl());
        return BrandResponse.from(brandRepository.save(brand));
    }

    @Override
    public void delete(String id) {
        brandRepository.delete(getOrThrow(id));
    }

    private Brand getOrThrow(String id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BRAND_NOT_FOUND));
    }

    private String resolveSlug(String provided, String name) {
        if (provided != null && !provided.isBlank()) return provided.toLowerCase().trim();
        return name.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
    }
}
