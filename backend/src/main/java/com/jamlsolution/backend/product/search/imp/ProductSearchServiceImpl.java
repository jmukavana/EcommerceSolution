package com.jamlsolution.backend.product.search.imp;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.jamlsolution.backend.product.search.ProductDocument;
import com.jamlsolution.backend.product.search.ProductSearchRepository;
import com.jamlsolution.backend.product.search.ProductSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductSearchServiceImpl implements ProductSearchService {

    private final ProductSearchRepository searchRepository;
    private final ElasticsearchClient elasticsearchClient;

    @Override
    public Page<ProductDocument> search(String query, String status, String brandName, Pageable pageable) {
        try {
            int from = (int) pageable.getOffset();
            int size = pageable.getPageSize();

            SearchResponse<ProductDocument> response = elasticsearchClient.search(s -> s
                    .index("products")
                    .from(from)
                    .size(size)
                    .query(q -> q
                            .bool(b -> {
                                if (query != null && !query.isBlank()) {
                                    b.must(m -> m.multiMatch(mm -> mm
                                            .fields("name^3", "shortDescription^2", "description", "tags")
                                            .query(query)
                                            .fuzziness("AUTO")));
                                }
                                if (status != null && !status.isBlank()) {
                                    b.filter(f -> f.term(t -> t.field("status").value(status)));
                                }
                                if (brandName != null && !brandName.isBlank()) {
                                    b.filter(f -> f.term(t -> t.field("brandName").value(brandName)));
                                }
                                return b;
                            })
                    ), ProductDocument.class);

            List<ProductDocument> hits = response.hits().hits().stream()
                    .map(Hit::source)
                    .toList();
            long total = response.hits().total() != null ? response.hits().total().value() : 0;
            return new PageImpl<>(hits, pageable, total);

        } catch (IOException e) {
            log.error("Elasticsearch search failed: {}", e.getMessage());
            // Fallback to repository-based search
            if (query != null && !query.isBlank()) {
                return searchRepository.findByNameContainingOrShortDescriptionContainingOrTagsContaining(
                        query, query, query, pageable);
            }
            return searchRepository.findAll(pageable);
        }
    }

    @Override
    public void index(ProductDocument document) {
        searchRepository.save(document);
    }

    @Override
    public void delete(String productId) {
        searchRepository.deleteById(productId);
    }
}
