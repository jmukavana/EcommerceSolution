package com.jamlsolution.backend.accounting.journal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface JournalEntryRepository extends JpaRepository<JournalEntry, String> {
    Page<JournalEntry> findByStatus(JournalEntryStatus status, Pageable pageable);
    List<JournalEntry> findByStatusAndEntryDateBetween(JournalEntryStatus status, LocalDate from, LocalDate to);
    List<JournalEntry> findByReference(String reference);
}
