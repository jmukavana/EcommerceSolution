package com.jamlsolution.backend.accounting.account;

import com.jamlsolution.backend.accounting.account.request.CreateAccountRequest;
import com.jamlsolution.backend.accounting.account.response.AccountResponse;
import com.jamlsolution.backend.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@Tag(name = "Accounting – Accounts", description = "Chart of accounts management")
@RestController
@RequestMapping("/api/v1/accounting/accounts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AccountController {

    private final AccountService accountService;

    @Operation(summary = "Create account")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AccountResponse> create(
            @Valid @RequestBody CreateAccountRequest request,
            HttpServletRequest httpRequest) {
        return ok(accountService.create(request), httpRequest);
    }

    @Operation(summary = "List all active accounts")
    @GetMapping
    public ApiResponse<List<AccountResponse>> findAll(HttpServletRequest httpRequest) {
        return ok(accountService.findAll(), httpRequest);
    }

    @Operation(summary = "List accounts by type")
    @GetMapping("/by-type/{type}")
    public ApiResponse<List<AccountResponse>> findByType(
            @PathVariable AccountType type, HttpServletRequest httpRequest) {
        return ok(accountService.findByType(type), httpRequest);
    }

    @Operation(summary = "Get account by ID")
    @GetMapping("/{id}")
    public ApiResponse<AccountResponse> findById(
            @PathVariable String id, HttpServletRequest httpRequest) {
        return ok(accountService.findById(id), httpRequest);
    }

    @Operation(summary = "Update account")
    @PutMapping("/{id}")
    public ApiResponse<AccountResponse> update(
            @PathVariable String id,
            @Valid @RequestBody CreateAccountRequest request,
            HttpServletRequest httpRequest) {
        return ok(accountService.update(id, request), httpRequest);
    }

    @Operation(summary = "Deactivate account")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable String id) {
        accountService.deactivate(id);
    }

    private <T> ApiResponse<T> ok(T data, HttpServletRequest req) {
        return new ApiResponse<>(true, "Success", data, null, Instant.now(), req.getRequestURI());
    }
}
