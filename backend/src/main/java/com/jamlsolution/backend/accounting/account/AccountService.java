package com.jamlsolution.backend.accounting.account;

import com.jamlsolution.backend.accounting.account.request.CreateAccountRequest;
import com.jamlsolution.backend.accounting.account.response.AccountResponse;

import java.util.List;

public interface AccountService {
    AccountResponse create(CreateAccountRequest request);
    List<AccountResponse> findAll();
    List<AccountResponse> findByType(AccountType type);
    AccountResponse findById(String id);
    AccountResponse update(String id, CreateAccountRequest request);
    void deactivate(String id);
}
