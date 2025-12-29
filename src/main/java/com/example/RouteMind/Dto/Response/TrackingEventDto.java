package com.example.RouteMind.Dto.Response;

import com.example.RouteMind.enums.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Single tracking event in delivery journey.
 * Shown in chronological order to customer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackingEventDto {

    // Status code
    private DeliveryStatus status;

    // Human-readable description
    private String description;

    // Location where event occurred
    private String location;

    // When it happened
    private LocalDateTime timestamp;
}
