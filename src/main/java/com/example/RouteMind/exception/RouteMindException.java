package com.example.RouteMind.exception;

/**
 * Base exception for all RouteMind errors.
 */
public class RouteMindException extends RuntimeException {

    private final String errorCode;
    public RouteMindException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    public String getErrorCode() {
        return errorCode;
    }
}
