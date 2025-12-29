package com.example.RouteMind.dto.Request;

import com.example.RouteMind.enums.PaymentMode;
import lombok.*;
/**
 * Request to check delivery serviceability.
 * Called BEFORE order to show available options.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ServiceabilityRequest {

    // Pickup pincode (seller/warehouse)
    private String pickupPincode;

    // Delivery pincode (customer)
    private String deliveryPincode;

    // Package details for pricing
    private PackageDetailDto packageDetails;

    // Payment mode to filter providers
    private PaymentMode paymentMode;

}
