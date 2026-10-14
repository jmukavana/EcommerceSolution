package com.jamlsolution.backend.product.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductSearchService {
    Page<ProductDocument> search(String query, String status, String brandName, Pageable pageable);
    void index(ProductDocument document);
    void delete(String productId);
}
