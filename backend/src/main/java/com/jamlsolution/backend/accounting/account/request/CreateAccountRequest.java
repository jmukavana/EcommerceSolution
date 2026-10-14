package com.jamlsolution.backend.accounting.account.request;

import com.jamlsolution.backend.accounting.account.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAccountRequest(
        @NotBlank String code,
        @NotBlank String name,
        @NotNull AccountType type,
        String description,
        String parentId
) {}
