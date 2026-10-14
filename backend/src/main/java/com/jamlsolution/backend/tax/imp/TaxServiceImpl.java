package com.jamlsolution.backend.tax.imp;

import com.jamlsolution.backend.exception.BusinessException;
import com.jamlsolution.backend.exception.ErrorCode;
import com.jamlsolution.backend.order.cart.CartItem;
import com.jamlsolution.backend.tax.*;
import com.jamlsolution.backend.tax.request.CreateTaxRateRequest;
import com.jamlsolution.backend.tax.request.UpdateTaxRateRequest;
import com.jamlsolution.backend.tax.response.TaxRateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaxServiceImpl implements TaxService {

    private final TaxRateRepository taxRateRepository;

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculate(List<CartItem> items, String countryCode) {
        BigDecimal totalTax = BigDecimal.ZERO;
        for (CartItem item : items) {
            var product = item.getVariant().getProduct();
            if (!product.isTaxable()) {
                continue;
            }

            TaxClass taxClass = product.getTaxClass() != null
                    ? product.getTaxClass()
                    : TaxClass.STANDARD;

            // EXEMPT and ZERO_RATED always produce zero tax
            if (taxClass == TaxClass.EXEMPT || taxClass == TaxClass.ZERO_RATED) {
                continue;
            }

            Optional<TaxRate> rateOpt = taxRateRepository
                    .findByTaxClassAndCountryCodeAndActiveTrue(taxClass, countryCode);

            if (rateOpt.isEmpty()) {
                continue; // no configured rate → skip silently
            }

            BigDecimal lineTotal = item.getUnitPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));

            BigDecimal tax = lineTotal
                    .multiply(rateOpt.get().getRate())
                    .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);

            totalTax = totalTax.add(tax);
        }
        return totalTax.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    @Transactional
    public TaxRateResponse create(CreateTaxRateRequest request) {
        if (taxRateRepository.existsByTaxClassAndCountryCode(request.taxClass(), request.countryCode())) {
            throw new BusinessException(ErrorCode.TAX_RATE_ALREADY_EXISTS);
        }
        TaxRate rate = TaxRate.builder()
                .name(request.name())
                .taxClass(request.taxClass())
                .countryCode(request.countryCode().toUpperCase())
                .rate(request.rate())
                .active(true)
                .build();
        return TaxRateResponse.from(taxRateRepository.save(rate));
    }

    @Override
    @Transactional
    public TaxRateResponse update(String id, UpdateTaxRateRequest request) {
        TaxRate rate = findRateOrThrow(id);
        if (request.name() != null) rate.setName(request.name());
        if (request.rate() != null) rate.setRate(request.rate());
        if (request.active() != null) rate.setActive(request.active());
        return TaxRateResponse.from(taxRateRepository.save(rate));
    }

    @Override
    @Transactional(readOnly = true)
    public TaxRateResponse findById(String id) {
        return TaxRateResponse.from(findRateOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaxRateResponse> findAll() {
        return taxRateRepository.findAll().stream().map(TaxRateResponse::from).toList();
    }

    @Override
    @Transactional
    public void delete(String id) {
        taxRateRepository.delete(findRateOrThrow(id));
    }

    private TaxRate findRateOrThrow(String id) {
        return taxRateRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.TAX_RATE_NOT_FOUND));
    }
}
