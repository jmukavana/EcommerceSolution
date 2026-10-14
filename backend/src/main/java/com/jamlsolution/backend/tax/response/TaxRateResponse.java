package com.jamlsolution.backend.tax.response;

import com.jamlsolution.backend.tax.TaxClass;
import com.jamlsolution.backend.tax.TaxRate;

import java.math.BigDecimal;

public record TaxRateResponse(
        String id,
        String name,
        TaxClass taxClass,
        String countryCode,
        BigDecimal rate,
        boolean active
) {
    public static TaxRateResponse from(TaxRate r) {
        return new TaxRateResponse(r.getId(), r.getName(), r.getTaxClass(),
                r.getCountryCode(), r.getRate(), r.isActive());
    }
}
