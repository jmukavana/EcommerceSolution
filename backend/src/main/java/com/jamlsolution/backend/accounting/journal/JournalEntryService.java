package com.jamlsolution.backend.accounting.journal;

import com.jamlsolution.backend.accounting.journal.request.CreateJournalEntryRequest;
import com.jamlsolution.backend.accounting.journal.response.JournalEntryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface JournalEntryService {
    JournalEntryResponse create(CreateJournalEntryRequest request);
    JournalEntryResponse post(String id);
    JournalEntryResponse findById(String id);
    Page<JournalEntryResponse> findAll(Pageable pageable);
    Page<JournalEntryResponse> findByStatus(JournalEntryStatus status, Pageable pageable);
    List<JournalEntryResponse> findByReference(String reference);
}
