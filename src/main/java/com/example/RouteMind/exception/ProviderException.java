package com.example.RouteMind.exception;

import com.example.RouteMind.enums.ProviderCode;
/**
 * Thrown when provider API fails.
 */
public class ProviderException extends RouteMindException {
    private final ProviderCode provider;
    public ProviderException(ProviderCode provider, String message) {
        super("PROVIDER_ERROR", provider.getDisplayName() + ": " + message);
        this.provider = provider;
    }
    public ProviderCode getProvider() {
        return provider;
    }
}
