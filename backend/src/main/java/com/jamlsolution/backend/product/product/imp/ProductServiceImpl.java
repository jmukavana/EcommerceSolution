package com.jamlsolution.backend.product.product.imp;

import com.jamlsolution.backend.common.PageableUtils;
import com.jamlsolution.backend.exception.BusinessException;
import com.jamlsolution.backend.exception.ErrorCode;
import com.jamlsolution.backend.product.brand.Brand;
import com.jamlsolution.backend.product.brand.BrandRepository;
import com.jamlsolution.backend.product.category.Category;
import com.jamlsolution.backend.product.category.CategoryRepository;
import com.jamlsolution.backend.product.product.*;
import com.jamlsolution.backend.product.product.request.AddImageRequest;
import com.jamlsolution.backend.product.search.ProductSearchSyncService;
import com.jamlsolution.backend.storage.StorageService;
import org.springframework.web.multipart.MultipartFile;
import com.jamlsolution.backend.product.product.request.AttributeValueRequest;
import com.jamlsolution.backend.product.product.request.CreateProductRequest;
import com.jamlsolution.backend.product.product.request.CreateVariantRequest;
import com.jamlsolution.backend.product.product.response.ProductImageResponse;
import com.jamlsolution.backend.product.product.response.ProductResponse;
import com.jamlsolution.backend.product.product.response.ProductSummaryResponse;
import com.jamlsolution.backend.product.product.response.VariantResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final ProductImageRepository imageRepository;
    private final ProductAttributeRepository attributeRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final StorageService storageService;
    private final ProductSearchSyncService searchSyncService;

    private static final Set<String> SORT_FIELDS =
            Set.of("name", "slug", "status", "type", "featured", "averageRating", "totalReviews", "createdDate");

    @Override
    public ProductResponse create(CreateProductRequest request) {
        String slug = resolveSlug(request.slug(), request.name());
        if (productRepository.existsBySlug(slug)) {
            throw new BusinessException(ErrorCode.PRODUCT_SLUG_EXISTS);
        }
        Product.ProductBuilder<?, ?> builder = Product.builder()
                .name(request.name())
                .slug(slug)
                .shortDescription(request.shortDescription())
                .description(request.description())
                .status(ProductStatus.DRAFT)
                .type(request.type() != null ? request.type() : ProductType.SIMPLE)
                .featured(request.featured())
                .taxable(request.taxable())
                .taxClass(request.taxClass())
                .weight(request.weight())
                .weightUnit(request.weightUnit() != null ? request.weightUnit() : "kg")
                .length(request.length())
                .width(request.width())
                .height(request.height())
                .dimensionUnit(request.dimensionUnit() != null ? request.dimensionUnit() : "cm")
                .requiresShipping(request.requiresShipping())
                .metaTitle(request.metaTitle())
                .metaDescription(request.metaDescription())
                .metaKeywords(request.metaKeywords())
                .tags(request.tags() != null ? request.tags() : new ArrayList<>())
                .variants(new ArrayList<>())
                .images(new ArrayList<>())
                .categories(new ArrayList<>());

        if (request.brandId() != null) {
            Brand brand = brandRepository.findById(request.brandId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.BRAND_NOT_FOUND));
            builder.brand(brand);
        }

        Product product = builder.build();

        if (request.categoryIds() != null) {
            List<Category> categories = categoryRepository.findAllById(request.categoryIds());
            product.setCategories(categories);
        }

        Product saved = productRepository.save(product);
        searchSyncService.syncProduct(saved);
        return ProductResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductSummaryResponse> findAll(String search, ProductStatus status, ProductType type,
                                                 String brandId, String categoryId, Pageable pageable) {
        Pageable safe = PageableUtils.sanitize(pageable, SORT_FIELDS, "createdDate");
        return productRepository
                .findAll(ProductSpecification.build(search, status, type, brandId, categoryId), safe)
                .map(ProductSummaryResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse findById(String id) {
        return ProductResponse.from(getOrThrow(id));
    }

    @Override
    public ProductResponse update(String id, CreateProductRequest request) {
        Product product = getOrThrow(id);
        String slug = resolveSlug(request.slug(), request.name());
        if (!slug.equals(product.getSlug()) && productRepository.existsBySlug(slug)) {
            throw new BusinessException(ErrorCode.PRODUCT_SLUG_EXISTS);
        }
        product.setName(request.name());
        product.setSlug(slug);
        product.setShortDescription(request.shortDescription());
        product.setDescription(request.description());
        if (request.type() != null) product.setType(request.type());
        product.setFeatured(request.featured());
        product.setTaxable(request.taxable());
        product.setTaxClass(request.taxClass());
        product.setWeight(request.weight());
        if (request.weightUnit() != null) product.setWeightUnit(request.weightUnit());
        product.setLength(request.length());
        product.setWidth(request.width());
        product.setHeight(request.height());
        if (request.dimensionUnit() != null) product.setDimensionUnit(request.dimensionUnit());
        product.setRequiresShipping(request.requiresShipping());
        product.setMetaTitle(request.metaTitle());
        product.setMetaDescription(request.metaDescription());
        product.setMetaKeywords(request.metaKeywords());
        if (request.tags() != null) product.setTags(request.tags());

        if (request.brandId() != null) {
            product.setBrand(brandRepository.findById(request.brandId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.BRAND_NOT_FOUND)));
        }
        if (request.categoryIds() != null) {
            product.setCategories(categoryRepository.findAllById(request.categoryIds()));
        }
        Product updated = productRepository.save(product);
        searchSyncService.syncProduct(updated);
        return ProductResponse.from(updated);
    }

    @Override
    public void delete(String id) {
        Product product = getOrThrow(id);
        productRepository.delete(product);
        searchSyncService.removeProduct(id);
    }

    @Override
    public VariantResponse addVariant(String productId, CreateVariantRequest request) {
        Product product = getOrThrow(productId);
        if (variantRepository.existsBySku(request.sku())) {
            throw new BusinessException(ErrorCode.VARIANT_SKU_EXISTS);
        }
        ProductVariant variant = ProductVariant.builder()
                .product(product)
                .sku(request.sku())
                .barcode(request.barcode())
                .name(request.name())
                .price(request.price())
                .compareAtPrice(request.compareAtPrice())
                .costPrice(request.costPrice())
                .trackInventory(request.trackInventory())
                .imageUrl(request.imageUrl())
                .isDefault(request.isDefault())
                .position(request.position())
                .active(true)
                .attributeValues(new ArrayList<>())
                .build();

        if (request.attributeValues() != null) {
            for (AttributeValueRequest av : request.attributeValues()) {
                ProductAttribute attr = attributeRepository.findById(av.attributeId())
                        .orElseThrow(() -> new BusinessException(ErrorCode.VARIANT_NOT_FOUND));
                variant.getAttributeValues().add(ProductAttributeValue.builder()
                        .variant(variant)
                        .attribute(attr)
                        .value(av.value())
                        .displayValue(av.displayValue())
                        .build());
            }
        }
        return VariantResponse.from(variantRepository.save(variant));
    }

    @Override
    public VariantResponse updateVariant(String productId, String variantId, CreateVariantRequest request) {
        getOrThrow(productId);
        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new BusinessException(ErrorCode.VARIANT_NOT_FOUND));
        if (!variant.getSku().equals(request.sku()) && variantRepository.existsBySku(request.sku())) {
            throw new BusinessException(ErrorCode.VARIANT_SKU_EXISTS);
        }
        variant.setSku(request.sku());
        variant.setBarcode(request.barcode());
        variant.setName(request.name());
        variant.setPrice(request.price());
        variant.setCompareAtPrice(request.compareAtPrice());
        variant.setCostPrice(request.costPrice());
        variant.setTrackInventory(request.trackInventory());
        variant.setImageUrl(request.imageUrl());
        variant.setDefault(request.isDefault());
        variant.setPosition(request.position());
        return VariantResponse.from(variantRepository.save(variant));
    }

    @Override
    public void removeVariant(String productId, String variantId) {
        getOrThrow(productId);
        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new BusinessException(ErrorCode.VARIANT_NOT_FOUND));
        variantRepository.delete(variant);
    }

    @Override
    public ProductImageResponse addImage(String productId, AddImageRequest request) {
        Product product = getOrThrow(productId);
        ProductImage image = ProductImage.builder()
                .product(product)
                .url(request.url())
                .altText(request.altText())
                .isPrimary(request.isPrimary())
                .position(request.position())
                .variantId(request.variantId())
                .build();
        return ProductImageResponse.from(imageRepository.save(image));
    }

    @Override
    public ProductImageResponse uploadImage(String productId, MultipartFile file, boolean isPrimary, int position) {
        Product product = getOrThrow(productId);
        String url = storageService.uploadProductImage(file);
        ProductImage image = ProductImage.builder()
                .product(product)
                .url(url)
                .altText(file.getOriginalFilename())
                .isPrimary(isPrimary)
                .position(position)
                .build();
        return ProductImageResponse.from(imageRepository.save(image));
    }

    @Override
    public void removeImage(String productId, String imageId) {
        getOrThrow(productId);
        ProductImage image = imageRepository.findById(imageId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        imageRepository.delete(image);
    }

    private Product getOrThrow(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    private String resolveSlug(String provided, String name) {
        if (provided != null && !provided.isBlank()) return provided.toLowerCase().trim();
        return name.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
    }
}
