package com.example.RouteMind.dto.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * Represents an address (used for pickup/delivery).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class AddressDto {

    // Contact person name
    private String name;

    // Phone number for delivery coordination
    private String phone;

    // Full street address
    private String address;

    // City name
    private String city;

    // State name
    private String state;

    // 6-digit pincode
    private String pincode;

}
