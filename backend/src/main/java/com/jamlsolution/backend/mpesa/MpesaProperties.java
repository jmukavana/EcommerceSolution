package com.jamlsolution.backend.mpesa;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "mpesa")
public class MpesaProperties {
    private String baseUrl;
    private String consumerKey;
    private String consumerSecret;
    private String shortcode;
    private String passkey;
    private String callbackUrl;
    private String transactionType;
}
