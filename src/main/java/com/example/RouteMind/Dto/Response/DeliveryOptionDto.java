package com.example.RouteMind.Dto.Response;

import com.example.RouteMind.enums.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;
/**
 * Represents one delivery option.
 * Multiple options returned for customer to choose.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class DeliveryOptionDto {

    // Provider (BLUEDART, FEDEX, DTDC)
    private ProviderCode provider;

    // Provider display name
    private String providerName;

    // Service type (EXPRESS, STANDARD, ECONOMY)
    private ServiceType serviceType;

    // Transport mode (AIR, ROAD)
    private TransportMode transportMode;

    // Shipping cost
    private BigDecimal cost;

    // Estimated delivery days
    private Integer etaDaysMin;
    private Integer etaDaysMax;

    // Is COD available?
    private Boolean codAvailable;

    // Tags for UI display (FASTEST, CHEAPEST, RECOMMENDED)
    private List<ProviderTag> tags;
}
