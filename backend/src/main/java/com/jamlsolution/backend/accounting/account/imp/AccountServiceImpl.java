package com.jamlsolution.backend.accounting.account.imp;

import com.jamlsolution.backend.accounting.account.Account;
import com.jamlsolution.backend.accounting.account.AccountRepository;
import com.jamlsolution.backend.accounting.account.AccountService;
import com.jamlsolution.backend.accounting.account.AccountType;
import com.jamlsolution.backend.accounting.account.request.CreateAccountRequest;
import com.jamlsolution.backend.accounting.account.response.AccountResponse;
import com.jamlsolution.backend.exception.BusinessException;
import com.jamlsolution.backend.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    public AccountResponse create(CreateAccountRequest request) {
        if (accountRepository.existsByCode(request.code())) {
            throw new BusinessException(ErrorCode.ACCOUNT_CODE_EXISTS);
        }
        Account parent = null;
        if (request.parentId() != null) {
            parent = accountRepository.findById(request.parentId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));
        }
        Account account = Account.builder()
                .code(request.code())
                .name(request.name())
                .type(request.type())
                .description(request.description())
                .parent(parent)
                .system(false)
                .active(true)
                .currentBalance(BigDecimal.ZERO)
                .build();
        return AccountResponse.from(accountRepository.save(account));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountResponse> findAll() {
        return accountRepository.findByActiveTrue().stream()
                .map(AccountResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountResponse> findByType(AccountType type) {
        return accountRepository.findByTypeAndActiveTrue(type).stream()
                .map(AccountResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponse findById(String id) {
        return AccountResponse.from(getOrThrow(id));
    }

    @Override
    public AccountResponse update(String id, CreateAccountRequest request) {
        Account account = getOrThrow(id);
        if (!account.getCode().equals(request.code()) && accountRepository.existsByCode(request.code())) {
            throw new BusinessException(ErrorCode.ACCOUNT_CODE_EXISTS);
        }
        account.setCode(request.code());
        account.setName(request.name());
        account.setType(request.type());
        account.setDescription(request.description());
        if (request.parentId() != null) {
            account.setParent(accountRepository.findById(request.parentId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND)));
        }
        return AccountResponse.from(accountRepository.save(account));
    }

    @Override
    public void deactivate(String id) {
        Account account = getOrThrow(id);
        account.setActive(false);
        accountRepository.save(account);
    }

    private Account getOrThrow(String id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));
    }
}
