package com.example.RouteMind.dto.Response;

import com.example.RouteMind.enums.DeliveryStatus;
import com.example.RouteMind.enums.ProviderCode;
import com.example.RouteMind.enums.ServiceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Response after creating an order.
 * Returned to web app with order & shipment details.
 */
@Data
@Accessors(chain = true)
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
