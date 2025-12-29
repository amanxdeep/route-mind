package com.example.RouteMind.dto.Response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * Error details for failed API responses.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor

public class GenericError {

    // Error code for programmatic handling (e.g., "NOT_FOUND", "VALIDATION_ERROR")
    private String code;

    // Human-readable error message
    private String message;
}
