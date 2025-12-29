package com.example.RouteMind.Dto.Response;

import com.example.RouteMind.enums.ProviderCode;
import com.example.RouteMind.enums.ProviderTag;
import lombok.*;
import java.math.BigDecimal;
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
