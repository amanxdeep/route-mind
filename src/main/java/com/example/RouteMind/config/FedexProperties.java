package com.example.RouteMind.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for FedEx API integration.
 * Loaded from application.yml with prefix "fedex"
 */
@Data
@Component
@ConfigurationProperties(prefix = "fedex")
public class FedexProperties {

    private String baseUrl;
    private String apiKey;
    private String accountNumber;
    private String password;
    private String meterNumber;
    private String clientId;
    private String clientSecret;
    private boolean enableLogging = true;
}


