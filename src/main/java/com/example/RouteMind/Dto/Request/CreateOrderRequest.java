package com.example.RouteMind.Dto.Request;

import com.example.RouteMind.enums.PaymentMode;
import com.example.RouteMind.enums.ProviderCode;
import com.example.RouteMind.enums.ServiceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
/**
 * Request to create a new delivery order.
 * Sent by web application after customer places order.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CreateOrderRequest {

    // Web app's order ID (for reference)
    private String externalOrderId;

    // Customer identifier
    private String customerId;

    // Where to pickup from (seller/warehouse)
    private AddressDto pickupAddress;

    // Where to deliver (customer)
    private AddressDto deliveryAddress;

    // Package details for pricing
    private PackageDetailDto packageDetails;

    // Payment mode: PREPAID, COD, POSTPAID
    private PaymentMode paymentMode;

    // Amount to collect if COD
    private BigDecimal codAmount;

    // Optional: specific provider (null = auto-select)
    private ProviderCode preferredProvider;

    // Optional: service type (null = STANDARD)
    private ServiceType serviceType;

}
