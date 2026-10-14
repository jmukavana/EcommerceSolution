package com.jamlsolution.backend.accounting.event;

import com.jamlsolution.backend.accounting.account.Account;
import com.jamlsolution.backend.accounting.account.AccountRepository;
import com.jamlsolution.backend.accounting.journal.*;
import com.jamlsolution.backend.accounting.seed.SystemAccounts;
import com.jamlsolution.backend.order.events.OrderPlacedEvent;
import com.jamlsolution.backend.order.events.OrderStatusChangedEvent;
import com.jamlsolution.backend.order.events.PaymentReceivedEvent;
import com.jamlsolution.backend.order.order.Order;
import com.jamlsolution.backend.order.order.OrderStatus;
import com.jamlsolution.backend.order.payment.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountingEventListener {

    private final JournalEntryRepository journalEntryRepository;
    private final JournalLineRepository journalLineRepository;
    private final AccountRepository accountRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onOrderPlaced(OrderPlacedEvent event) {
        Order order = event.order();
        try {
            BigDecimal netRevenue = order.getSubtotal().subtract(order.getDiscountAmount());
            List<JournalLine> lines = new ArrayList<>();

            // DR Accounts Receivable = order.total
            lines.add(line(account(SystemAccounts.ACCOUNTS_REC),
                    "AR for order " + order.getOrderNumber(),
                    order.getTotal(), BigDecimal.ZERO));

            // CR Sales Revenue = net revenue
            if (netRevenue.compareTo(BigDecimal.ZERO) > 0) {
                lines.add(line(account(SystemAccounts.SALES_REVENUE),
                        "Sales revenue " + order.getOrderNumber(),
                        BigDecimal.ZERO, netRevenue));
            }

            // CR Shipping Revenue
            if (order.getShippingAmount().compareTo(BigDecimal.ZERO) > 0) {
                lines.add(line(account(SystemAccounts.SHIPPING_REV),
                        "Shipping " + order.getOrderNumber(),
                        BigDecimal.ZERO, order.getShippingAmount()));
            }

            // CR Tax Payable
            if (order.getTaxAmount().compareTo(BigDecimal.ZERO) > 0) {
                lines.add(line(account(SystemAccounts.TAX_PAYABLE),
                        "Tax " + order.getOrderNumber(),
                        BigDecimal.ZERO, order.getTaxAmount()));
            }

            postEntry("Sale – " + order.getOrderNumber(),
                    order.getOrderNumber(), ReferenceType.ORDER, lines);

        } catch (Exception e) {
            log.error("Failed to create accounting entry for order {}: {}", order.getOrderNumber(), e.getMessage());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onPaymentReceived(PaymentReceivedEvent event) {
        Payment payment = event.payment();
        try {
            postEntry("Payment – " + payment.getOrder().getOrderNumber(),
                    payment.getId(), ReferenceType.PAYMENT,
                    List.of(
                            line(account(SystemAccounts.CASH),
                                    "Cash received", payment.getAmount(), BigDecimal.ZERO),
                            line(account(SystemAccounts.ACCOUNTS_REC),
                                    "AR cleared", BigDecimal.ZERO, payment.getAmount())
                    ));
        } catch (Exception e) {
            log.error("Failed to create accounting entry for payment {}: {}", payment.getId(), e.getMessage());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onOrderStatusChanged(OrderStatusChangedEvent event) {
        if (event.order().getStatus() != OrderStatus.CANCELLED) return;
        Order order = event.order();
        try {
            BigDecimal netRevenue = order.getSubtotal().subtract(order.getDiscountAmount());
            List<JournalLine> lines = new ArrayList<>();

            // Reverse the original sale entry
            lines.add(line(account(SystemAccounts.SALES_REVENUE),
                    "Reversal – cancelled order " + order.getOrderNumber(),
                    netRevenue, BigDecimal.ZERO));

            if (order.getShippingAmount().compareTo(BigDecimal.ZERO) > 0) {
                lines.add(line(account(SystemAccounts.SHIPPING_REV),
                        "Reversal – shipping " + order.getOrderNumber(),
                        order.getShippingAmount(), BigDecimal.ZERO));
            }

            if (order.getTaxAmount().compareTo(BigDecimal.ZERO) > 0) {
                lines.add(line(account(SystemAccounts.TAX_PAYABLE),
                        "Reversal – tax " + order.getOrderNumber(),
                        order.getTaxAmount(), BigDecimal.ZERO));
            }

            lines.add(line(account(SystemAccounts.ACCOUNTS_REC),
                    "Reversal – AR " + order.getOrderNumber(),
                    BigDecimal.ZERO, order.getTotal()));

            postEntry("Cancellation – " + order.getOrderNumber(),
                    order.getOrderNumber(), ReferenceType.ORDER, lines);

        } catch (Exception e) {
            log.error("Failed to create reversal entry for cancelled order {}: {}", order.getOrderNumber(), e.getMessage());
        }
    }

    private void postEntry(String description, String reference,
                           ReferenceType refType, List<JournalLine> lines) {
        JournalEntry entry = JournalEntry.builder()
                .entryNumber(generateEntryNumber())
                .entryDate(LocalDate.now())
                .description(description)
                .reference(reference)
                .referenceType(refType)
                .status(JournalEntryStatus.DRAFT)
                .lines(new ArrayList<>())
                .build();

        JournalEntry saved = journalEntryRepository.save(entry);
        for (JournalLine l : lines) {
            l.setJournalEntry(saved);
            journalLineRepository.save(l);
            saved.getLines().add(l);

            // Update account balance
            Account acct = l.getAccount();
            BigDecimal movement = acct.getType().isDebitNormal()
                    ? l.getDebitAmount().subtract(l.getCreditAmount())
                    : l.getCreditAmount().subtract(l.getDebitAmount());
            acct.setCurrentBalance(acct.getCurrentBalance().add(movement));
            accountRepository.save(acct);
        }

        saved.setStatus(JournalEntryStatus.POSTED);
        saved.setPostedAt(LocalDateTime.now());
        journalEntryRepository.save(saved);
    }

    private JournalLine line(Account account, String description,
                             BigDecimal debit, BigDecimal credit) {
        return JournalLine.builder()
                .account(account)
                .description(description)
                .debitAmount(debit)
                .creditAmount(credit)
                .build();
    }

    private Account account(String code) {
        return accountRepository.findByCode(code)
                .orElseThrow(() -> new IllegalStateException("System account not found: " + code));
    }

    private String generateEntryNumber() {
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        return "JE-AUTO-" + ts;
    }
}
