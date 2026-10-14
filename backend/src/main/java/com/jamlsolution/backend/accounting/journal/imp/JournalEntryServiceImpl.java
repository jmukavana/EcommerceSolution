package com.jamlsolution.backend.accounting.journal.imp;

import com.jamlsolution.backend.accounting.account.Account;
import com.jamlsolution.backend.accounting.account.AccountRepository;
import com.jamlsolution.backend.accounting.journal.*;
import com.jamlsolution.backend.accounting.journal.request.CreateJournalEntryRequest;
import com.jamlsolution.backend.accounting.journal.request.JournalLineRequest;
import com.jamlsolution.backend.accounting.journal.response.JournalEntryResponse;
import com.jamlsolution.backend.exception.BusinessException;
import com.jamlsolution.backend.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class JournalEntryServiceImpl implements JournalEntryService {

    private final JournalEntryRepository journalEntryRepository;
    private final JournalLineRepository journalLineRepository;
    private final AccountRepository accountRepository;

    @Override
    public JournalEntryResponse create(CreateJournalEntryRequest request) {
        JournalEntry entry = JournalEntry.builder()
                .entryNumber(generateEntryNumber())
                .entryDate(request.entryDate())
                .description(request.description())
                .reference(request.reference())
                .referenceType(request.referenceType())
                .status(JournalEntryStatus.DRAFT)
                .lines(new ArrayList<>())
                .build();

        JournalEntry saved = journalEntryRepository.save(entry);

        for (JournalLineRequest lineReq : request.lines()) {
            Account account = accountRepository.findById(lineReq.accountId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));
            JournalLine line = JournalLine.builder()
                    .journalEntry(saved)
                    .account(account)
                    .description(lineReq.description())
                    .debitAmount(lineReq.debitAmount() != null ? lineReq.debitAmount() : BigDecimal.ZERO)
                    .creditAmount(lineReq.creditAmount() != null ? lineReq.creditAmount() : BigDecimal.ZERO)
                    .build();
            saved.getLines().add(journalLineRepository.save(line));
        }

        if (request.postImmediately()) {
            return post(saved.getId());
        }
        return JournalEntryResponse.from(saved);
    }

    @Override
    public JournalEntryResponse post(String id) {
        JournalEntry entry = getOrThrow(id);
        if (entry.getStatus() == JournalEntryStatus.POSTED) {
            throw new BusinessException(ErrorCode.JOURNAL_ENTRY_ALREADY_POSTED);
        }

        BigDecimal totalDebits = entry.getLines().stream()
                .map(JournalLine::getDebitAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCredits = entry.getLines().stream()
                .map(JournalLine::getCreditAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalDebits.compareTo(totalCredits) != 0) {
            throw new BusinessException(ErrorCode.JOURNAL_ENTRY_NOT_BALANCED);
        }

        for (JournalLine line : entry.getLines()) {
            Account account = line.getAccount();
            BigDecimal movement = account.getType().isDebitNormal()
                    ? line.getDebitAmount().subtract(line.getCreditAmount())
                    : line.getCreditAmount().subtract(line.getDebitAmount());
            account.setCurrentBalance(account.getCurrentBalance().add(movement));
            accountRepository.save(account);
        }

        entry.setStatus(JournalEntryStatus.POSTED);
        entry.setPostedAt(LocalDateTime.now());
        return JournalEntryResponse.from(journalEntryRepository.save(entry));
    }

    @Override
    @Transactional(readOnly = true)
    public JournalEntryResponse findById(String id) {
        return JournalEntryResponse.from(getOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JournalEntryResponse> findAll(Pageable pageable) {
        return journalEntryRepository.findAll(pageable).map(JournalEntryResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JournalEntryResponse> findByStatus(JournalEntryStatus status, Pageable pageable) {
        return journalEntryRepository.findByStatus(status, pageable).map(JournalEntryResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JournalEntryResponse> findByReference(String reference) {
        return journalEntryRepository.findByReference(reference).stream()
                .map(JournalEntryResponse::from).toList();
    }

    private JournalEntry getOrThrow(String id) {
        return journalEntryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.JOURNAL_ENTRY_NOT_FOUND));
    }

    private String generateEntryNumber() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = journalEntryRepository.count() + 1;
        return "JE-%s-%05d".formatted(datePart, count);
    }
}
