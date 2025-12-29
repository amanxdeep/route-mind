package com.example.RouteMind.dto.Response;

import lombok.*;

import java.util.List;
/**
 * Response for serviceability check.
 * Lists all available delivery options.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ServiceabilityResponse {

    // Is delivery possible?
    private Boolean serviceable;

    // Pickup pincode (echo back)
    private String pickupPincode;

    // Delivery pincode (echo back)
    private String deliveryPincode;

    // Available options
    private List<DeliveryOptionDto> deliveryOptions;

    // Message if not serviceable
    private String message;
}
