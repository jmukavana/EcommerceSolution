package com.jamlsolution.backend.product.inventory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationExpiryJob {

    private final StockReservationRepository reservationRepository;

    @Scheduled(fixedDelay = 300_000) // every 5 minutes
    @Transactional
    public void purgeExpired() {
        reservationRepository.deleteExpired(LocalDateTime.now());
        log.debug("Purged expired stock reservations");
    }
}
