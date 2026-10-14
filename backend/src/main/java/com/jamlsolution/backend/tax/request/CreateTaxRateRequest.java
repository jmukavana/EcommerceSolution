package com.jamlsolution.backend.tax.request;

import com.jamlsolution.backend.tax.TaxClass;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateTaxRateRequest(
        @NotBlank String name,
        @NotNull TaxClass taxClass,
        @NotBlank @Size(min = 2, max = 2) String countryCode,
        @NotNull @DecimalMin("0") @DecimalMax("100") BigDecimal rate
) {}
