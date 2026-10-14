package com.jamlsolution.backend.product.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StockReservationRepository extends JpaRepository<StockReservation, String> {

    Optional<StockReservation> findByVariantIdAndCartId(String variantId, String cartId);

    List<StockReservation> findByCartId(String cartId);

    @Query("SELECT COALESCE(SUM(r.quantity), 0) FROM StockReservation r WHERE r.variantId = :variantId AND r.expiresAt > :now")
    int sumReservedQuantity(String variantId, LocalDateTime now);

    @Modifying
    @Query("DELETE FROM StockReservation r WHERE r.expiresAt < :now")
    void deleteExpired(LocalDateTime now);

    @Modifying
    void deleteByCartId(String cartId);
}
