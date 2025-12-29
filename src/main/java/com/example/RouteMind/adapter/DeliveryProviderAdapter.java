package com.example.RouteMind.adapter;

import com.example.RouteMind.dto.Request.CreateOrderRequest;
import com.example.RouteMind.dto.Response.OrderResponse;
import com.example.RouteMind.dto.Response.TrackingResponse;
import com.example.RouteMind.enums.ProviderCode;
import java.math.BigDecimal;
/**
 * Common interface for all delivery providers.
 * Each provider implements this with their specific API logic.
 */

public interface DeliveryProviderAdapter {

    // Which provider this adapter handles
    ProviderCode getProviderCode();

    // Check if delivery possible to pincode
    boolean checkServiceability(String pickupPincode, String deliveryPincode);

    // Get shipping cost
    BigDecimal calculateRate(String pickupPincode, String deliveryPincode, Double weightKg);

    // Create shipment with provider
    OrderResponse createShipment(CreateOrderRequest request);

    // Track shipment by AWB
    TrackingResponse trackShipment(String trackingId);

    // Cancel shipment
    boolean cancelShipment(String trackingId);
}
