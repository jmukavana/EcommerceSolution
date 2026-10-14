package com.jamlsolution.backend.accounting.journal;

import com.jamlsolution.backend.accounting.journal.request.CreateJournalEntryRequest;
import com.jamlsolution.backend.accounting.journal.response.JournalEntryResponse;
import com.jamlsolution.backend.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@Tag(name = "Accounting – Journal", description = "Double-entry journal entry management")
@RestController
@RequestMapping("/api/v1/accounting/journal")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class JournalEntryController {

    private final JournalEntryService journalEntryService;

    @Operation(summary = "Create journal entry")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<JournalEntryResponse> create(
            @Valid @RequestBody CreateJournalEntryRequest request,
            HttpServletRequest httpRequest) {
        return ok(journalEntryService.create(request), httpRequest);
    }

    @Operation(summary = "Post (finalise) a draft journal entry")
    @PatchMapping("/{id}/post")
    public ApiResponse<JournalEntryResponse> post(
            @PathVariable String id, HttpServletRequest httpRequest) {
        return ok(journalEntryService.post(id), httpRequest);
    }

    @Operation(summary = "Get journal entry by ID")
    @GetMapping("/{id}")
    public ApiResponse<JournalEntryResponse> findById(
            @PathVariable String id, HttpServletRequest httpRequest) {
        return ok(journalEntryService.findById(id), httpRequest);
    }

    @Operation(summary = "List all journal entries")
    @GetMapping
    public ApiResponse<Page<JournalEntryResponse>> findAll(
            @PageableDefault(size = 20, sort = "entryDate") Pageable pageable,
            HttpServletRequest httpRequest) {
        return ok(journalEntryService.findAll(pageable), httpRequest);
    }

    @Operation(summary = "List journal entries by status")
    @GetMapping("/by-status/{status}")
    public ApiResponse<Page<JournalEntryResponse>> findByStatus(
            @PathVariable JournalEntryStatus status,
            @PageableDefault(size = 20) Pageable pageable,
            HttpServletRequest httpRequest) {
        return ok(journalEntryService.findByStatus(status, pageable), httpRequest);
    }

    @Operation(summary = "Get journal entries for a reference (order/payment ID)")
    @GetMapping("/by-reference/{reference}")
    public ApiResponse<List<JournalEntryResponse>> findByReference(
            @PathVariable String reference, HttpServletRequest httpRequest) {
        return ok(journalEntryService.findByReference(reference), httpRequest);
    }

    private <T> ApiResponse<T> ok(T data, HttpServletRequest req) {
        return new ApiResponse<>(true, "Success", data, null, Instant.now(), req.getRequestURI());
    }
}
