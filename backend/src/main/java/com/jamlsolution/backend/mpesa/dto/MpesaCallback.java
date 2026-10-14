package com.jamlsolution.backend.mpesa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record MpesaCallback(
        @JsonProperty("Body") Body body
) {
    public record Body(
            @JsonProperty("stkCallback") StkCallback stkCallback
    ) {}

    public record StkCallback(
            @JsonProperty("MerchantRequestID") String merchantRequestId,
            @JsonProperty("CheckoutRequestID") String checkoutRequestId,
            @JsonProperty("ResultCode") int resultCode,
            @JsonProperty("ResultDesc") String resultDesc,
            @JsonProperty("CallbackMetadata") CallbackMetadata callbackMetadata
    ) {}

    public record CallbackMetadata(
            @JsonProperty("Item") List<MetadataItem> items
    ) {}

    public record MetadataItem(
            @JsonProperty("Name") String name,
            @JsonProperty("Value") Object value
    ) {}
}
