package com.jamlsolution.backend.tax;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
@Order(3)
@RequiredArgsConstructor
public class TaxSeeder implements CommandLineRunner {

    private final TaxRateRepository taxRateRepository;

    @Override
    public void run(String... args) {
        seedKenyaRates();
    }

    private void seedKenyaRates() {
        List<TaxRate> rates = List.of(
                rate("Kenya Standard VAT", TaxClass.STANDARD, "KE", new BigDecimal("16.00")),
                rate("Kenya Zero-Rated VAT", TaxClass.ZERO_RATED, "KE", BigDecimal.ZERO),
                rate("Kenya VAT Exempt", TaxClass.EXEMPT, "KE", BigDecimal.ZERO)
        );

        for (TaxRate r : rates) {
            if (!taxRateRepository.existsByTaxClassAndCountryCode(r.getTaxClass(), r.getCountryCode())) {
                taxRateRepository.save(r);
                log.info("Seeded tax rate: {} ({}%)", r.getName(), r.getRate());
            }
        }
    }

    private TaxRate rate(String name, TaxClass taxClass, String countryCode, BigDecimal rate) {
        return TaxRate.builder()
                .name(name)
                .taxClass(taxClass)
                .countryCode(countryCode)
                .rate(rate)
                .active(true)
                .build();
    }
}
