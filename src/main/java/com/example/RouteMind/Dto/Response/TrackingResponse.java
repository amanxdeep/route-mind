package com.example.RouteMind.Dto.Response;

import com.example.RouteMind.enums.DeliveryStatus;
import com.example.RouteMind.enums.ProviderCode;
import lombok.*;
import java.time.LocalDate;
import java.util.List;
/**
 * Response for tracking request.
 * Shows current status + full journey history.
 */
@Data
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
