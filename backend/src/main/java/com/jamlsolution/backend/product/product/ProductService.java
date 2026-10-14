package com.jamlsolution.backend.product.product;

import com.jamlsolution.backend.product.product.request.AddImageRequest;
import org.springframework.web.multipart.MultipartFile;
import com.jamlsolution.backend.product.product.request.CreateProductRequest;
import com.jamlsolution.backend.product.product.request.CreateVariantRequest;
import com.jamlsolution.backend.product.product.response.ProductImageResponse;
import com.jamlsolution.backend.product.product.response.ProductResponse;
import com.jamlsolution.backend.product.product.response.ProductSummaryResponse;
import com.jamlsolution.backend.product.product.response.VariantResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    ProductResponse create(CreateProductRequest request);
    Page<ProductSummaryResponse> findAll(String search, ProductStatus status, ProductType type,
                                         String brandId, String categoryId, Pageable pageable);
    ProductResponse findById(String id);
    ProductResponse update(String id, CreateProductRequest request);
    void delete(String id);

    VariantResponse addVariant(String productId, CreateVariantRequest request);
    VariantResponse updateVariant(String productId, String variantId, CreateVariantRequest request);
    void removeVariant(String productId, String variantId);

    ProductImageResponse addImage(String productId, AddImageRequest request);
    ProductImageResponse uploadImage(String productId, MultipartFile file, boolean isPrimary, int position);
    void removeImage(String productId, String imageId);
}
