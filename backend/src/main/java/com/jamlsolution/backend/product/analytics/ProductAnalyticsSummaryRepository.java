package com.jamlsolution.backend.product.analytics;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface ProductAnalyticsSummaryRepository extends JpaRepository<ProductAnalyticsSummary, String> {
    Optional<ProductAnalyticsSummary> findByProductIdAndSummaryDate(String productId, LocalDate summaryDate);
    Page<ProductAnalyticsSummary> findByProductId(String productId, Pageable pageable);
}
