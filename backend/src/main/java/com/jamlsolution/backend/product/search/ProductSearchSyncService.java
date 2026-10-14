package com.jamlsolution.backend.product.search;

import com.jamlsolution.backend.product.product.Product;
import com.jamlsolution.backend.product.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductSearchSyncService {

    private final ProductSearchService searchService;
    private final ProductRepository productRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    @Async
    public void syncProduct(Product product) {
        try {
            searchService.index(ProductDocument.from(product));
        } catch (Exception e) {
            log.warn("Failed to sync product {} to Elasticsearch: {}", product.getId(), e.getMessage());
        }
    }

    @Async
    public void removeProduct(String productId) {
        try {
            searchService.delete(productId);
        } catch (Exception e) {
            log.warn("Failed to remove product {} from Elasticsearch: {}", productId, e.getMessage());
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional(readOnly = true)
    public void reindexAll() {
        try {
            ensureIndex();
            List<Product> products = productRepository.findAll();
            products.forEach(p -> {
                try { searchService.index(ProductDocument.from(p)); }
                catch (Exception e) { log.warn("Skip product {}: {}", p.getId(), e.getMessage()); }
            });
            log.info("Indexed {} products to Elasticsearch", products.size());
        } catch (Exception e) {
            log.warn("Elasticsearch reindex skipped (ES unavailable?): {}", e.getMessage());
        }
    }

    private void ensureIndex() {
        IndexOperations ops = elasticsearchOperations.indexOps(ProductDocument.class);
        if (!ops.exists()) {
            ops.createWithMapping();
            log.info("Created Elasticsearch index 'products'");
        }
    }
}
