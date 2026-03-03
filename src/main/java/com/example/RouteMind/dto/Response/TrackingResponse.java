package com.example.RouteMind.dto.Response;

import com.example.RouteMind.enums.DeliveryStatus;
import com.example.RouteMind.enums.ProviderCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.List;

/**
 * Response for tracking request.
 * Shows current status + full journey history.
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class TrackingResponse {

    // Tracking number
    private String trackingId;

    // Web app's order ID
    private String externalOrderId;

    // Provider handling delivery
    private ProviderCode provider;

    // Current status
    private DeliveryStatus currentStatus;

    // Current location
    private String currentLocation;

    // Expected delivery date
    private LocalDate estimatedDeliveryDate;

    // Full tracking history
    private List<TrackingEventDto> events;

}
