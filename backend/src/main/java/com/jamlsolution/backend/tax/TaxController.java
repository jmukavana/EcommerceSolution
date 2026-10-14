package com.jamlsolution.backend.tax;

import com.jamlsolution.backend.common.ApiResponse;
import com.jamlsolution.backend.tax.request.CreateTaxRateRequest;
import com.jamlsolution.backend.tax.request.UpdateTaxRateRequest;
import com.jamlsolution.backend.tax.response.TaxRateResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tax-rates")
@RequiredArgsConstructor
public class TaxController {

    private final TaxService taxService;

    @GetMapping
    public ApiResponse<List<TaxRateResponse>> findAll(HttpServletRequest req) {
        return ok(taxService.findAll(), req);
    }

    @GetMapping("/{id}")
    public ApiResponse<TaxRateResponse> findById(@PathVariable String id, HttpServletRequest req) {
        return ok(taxService.findById(id), req);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<TaxRateResponse> create(@Valid @RequestBody CreateTaxRateRequest request,
                                               HttpServletRequest req) {
        return ok(taxService.create(request), req);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<TaxRateResponse> update(@PathVariable String id,
                                               @Valid @RequestBody UpdateTaxRateRequest request,
                                               HttpServletRequest req) {
        return ok(taxService.update(id, request), req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable String id) {
        taxService.delete(id);
    }

    private <T> ApiResponse<T> ok(T data, HttpServletRequest req) {
        return new ApiResponse<>(true, "Success", data, null, Instant.now(), req.getRequestURI());
    }
}
