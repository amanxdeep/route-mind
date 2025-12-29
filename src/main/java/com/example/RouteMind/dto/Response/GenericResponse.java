package com.example.RouteMind.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * Standard wrapper for all API responses.
 * Either 'data' OR 'error' will be populated, never both.
 *
 * Success: { "data": {...}, "error": null }
 * Failure: { "data": null, "error": {...} }
 */
@Data
@AllArgsConstructor
@NoArgsConstructor

public class GenericResponse<T> {

    // The actual response data (null if error)
    private T data;

    // Error details (null if success)
    private GenericError error;
    /**
     * Create success response with data
     */
    public static <T> GenericResponse<T> success(T data) {
        return new GenericResponse<>(data, null);
    }
    /**
     * Create failure response with error
     */
    public static <T> GenericResponse<T> failure(GenericError error) {
        return new GenericResponse<>(null, error);
    }

    /**
     * Create failure response with code and message
     */
    public static <T> GenericResponse<T> failure(String code, String message) {
        return new GenericResponse<>(null, new GenericError(code, message));
    }


}
