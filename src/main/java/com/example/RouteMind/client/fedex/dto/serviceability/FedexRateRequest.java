package com.example.RouteMind.client.fedex.dto.serviceability;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Minimal FedEx Rate API request for quotes.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FedexRateRequest {

    @JsonProperty("accountNumber")
    private AccountNumber accountNumber;

    @JsonProperty("requestedShipment")
    private RequestedShipment requestedShipment;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AccountNumber {
        @JsonProperty("value")
        private String value; // your FedEx account number
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RequestedShipment {

        @JsonProperty("shipper")
        private Party shipper;

        @JsonProperty("recipients")
        private List<Party> recipients;

        @JsonProperty("pickupType")
        private String pickupType; // e.g., "DROPOFF_AT_FEDEX_LOCATION"

        @JsonProperty("serviceType")
        private String serviceType; // optional; null to let FedEx return multiple

        @JsonProperty("packagingType")
        private String packagingType; // e.g., "YOUR_PACKAGING"

        @JsonProperty("requestedPackageLineItems")
        private List<PackageLineItem> requestedPackageLineItems;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Party {
        @JsonProperty("address")
        private Address address;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Address {
        @JsonProperty("postalCode")
        private String postalCode;

        @JsonProperty("countryCode")
        private String countryCode; // "IN"
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PackageLineItem {
        @JsonProperty("weight")
        private Weight weight;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Weight {
        @JsonProperty("units")
        private String units; // "KG"

        @JsonProperty("value")
        private Double value;
    }
}
