package com.jamlsolution.backend.tax;

import com.jamlsolution.backend.order.cart.CartItem;
import com.jamlsolution.backend.tax.request.CreateTaxRateRequest;
import com.jamlsolution.backend.tax.request.UpdateTaxRateRequest;
import com.jamlsolution.backend.tax.response.TaxRateResponse;

import java.math.BigDecimal;
import java.util.List;
public interface TaxService {

    BigDecimal calculate(List<CartItem> items, String countryCode);

    TaxRateResponse create(CreateTaxRateRequest request);

    TaxRateResponse update(String id, UpdateTaxRateRequest request);

    TaxRateResponse findById(String id);

    List<TaxRateResponse> findAll();

    void delete(String id);
}
