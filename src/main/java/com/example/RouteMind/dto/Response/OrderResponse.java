package com.example.RouteMind.dto.Response;

import com.example.RouteMind.enums.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
/**
 * Response after creating an order.
 * Returned to web app with order & shipment details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class OrderResponse {

    private UUID orderId;

    // Web app's order ID (echo back)
    private String externalOrderId;

    // Tracking number from provider
    private String trackingId;

    // Which provider was selected
    private ProviderCode provider;

    // Service used (EXPRESS, STANDARD, etc.)
    private ServiceType serviceType;

    // Current status
    private DeliveryStatus status;

    // Cost charged
    private BigDecimal shippingCost;

    // Expected delivery
    private LocalDate estimatedDeliveryDate;

    // Message for web app
    private String message;

}
