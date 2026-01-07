package com.example.RouteMind.client.fedex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * FedEx OAuth token response.
 */
@Data
public class FedexOAuthResponse {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_in")
    private Long expiresIn;

    @JsonProperty("scope")
    private String scope;
}