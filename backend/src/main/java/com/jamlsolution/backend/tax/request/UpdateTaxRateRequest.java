package com.jamlsolution.backend.tax.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public record UpdateTaxRateRequest(
        String name,
        @DecimalMin("0") @DecimalMax("100") BigDecimal rate,
        Boolean active
) {}
