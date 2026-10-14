package com.jamlsolution.backend.tax;

/**
 * Kenya Revenue Authority VAT classification.
 *
 * STANDARD → 16% VAT (default for most goods and services)
 * ZERO_RATED → 0% VAT, but supplier can claim input VAT credits
 *              (exports, international transport, certain food)
 * EXEMPT → No VAT charged, no input VAT recovery
 *              (medicines, medical equipment, educational materials,
 *               unprocessed agricultural produce)
 */
public enum TaxClass {
    STANDARD,
    ZERO_RATED,
    EXEMPT
}
