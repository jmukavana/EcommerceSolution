package com.jamlsolution.backend.tax;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaxRateRepository extends JpaRepository<TaxRate, String> {

    Optional<TaxRate> findByTaxClassAndCountryCodeAndActiveTrue(TaxClass taxClass, String countryCode);

    boolean existsByTaxClassAndCountryCode(TaxClass taxClass, String countryCode);

    List<TaxRate> findAllByCountryCode(String countryCode);
}
