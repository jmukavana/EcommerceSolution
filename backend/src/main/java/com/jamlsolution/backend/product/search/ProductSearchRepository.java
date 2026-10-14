package com.jamlsolution.backend.product.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, String> {
    Page<ProductDocument> findByNameContainingOrShortDescriptionContainingOrTagsContaining(
            String name, String shortDescription, String tag, Pageable pageable);
}
