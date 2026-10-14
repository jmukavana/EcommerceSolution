package com.jamlsolution.backend.product.brand;

import com.jamlsolution.backend.product.brand.request.CreateBrandRequest;
import com.jamlsolution.backend.product.brand.response.BrandResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BrandService {
    BrandResponse create(CreateBrandRequest request);
    Page<BrandResponse> findAll(String search, Pageable pageable);
    BrandResponse findById(String id);
    BrandResponse update(String id, CreateBrandRequest request);
    void delete(String id);
}
