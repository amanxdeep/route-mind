package com.example.RouteMind.exceptionHandling;

import com.example.RouteMind.Dto.Response.GenericError;
import com.example.RouteMind.Dto.Response.GenericResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
/**
 * Catches all exceptions and returns proper error responses.
 */
@RestControllerAdvice
@Slf4j

public class GlobalExceptionHandling {

    /**
     * Handle OrderNotFoundException.
     */
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<GenericResponse<Void>> handleOrderNotFound(OrderNotFoundException ex) {
        log.error("Order not found: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(GenericResponse.failure(ex.getErrorCode(), ex.getMessage()));
    }
    /**
     * Handle ProviderException.
     */
    @ExceptionHandler(ProviderException.class)
    public ResponseEntity<GenericResponse<Void>> handleProviderError(ProviderException ex) {
        log.error("Provider error: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(GenericResponse.failure(ex.getErrorCode(), ex.getMessage()));
    }
    /**
     * Handle RouteMindException (base).
     */
    @ExceptionHandler(RouteMindException.class)
    public ResponseEntity<GenericResponse<Void>> handleRouteMindException(RouteMindException ex) {
        log.error("Application error: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(GenericResponse.failure(ex.getErrorCode(), ex.getMessage()));
    }
    /**
     * Handle all other exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<GenericResponse<Void>> handleGenericException(Exception ex) {
        log.error("Unexpected error: ", ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(GenericResponse.failure("INTERNAL_ERROR", "An unexpected error occurred"));
    }

}