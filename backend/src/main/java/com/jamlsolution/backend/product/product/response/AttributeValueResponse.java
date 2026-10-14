package com.jamlsolution.backend.product.product.response;

import com.jamlsolution.backend.product.product.ProductAttributeValue;

public record AttributeValueResponse(
        String id,
        String attributeId,
        String attributeName,
        String value,
        String displayValue
) {
    public static AttributeValueResponse from(ProductAttributeValue av) {
        return new AttributeValueResponse(av.getId(),
                av.getAttribute().getId(), av.getAttribute().getName(),
                av.getValue(), av.getDisplayValue());
    }
}
