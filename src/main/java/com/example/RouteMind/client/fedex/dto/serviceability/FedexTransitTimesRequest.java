package com.example.RouteMind.client.fedex.dto.serviceability;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FedexTransitTimesRequest {

    @JsonProperty("requestedShipment")
    private RequestedShipment requestedShipment;

    @JsonProperty("carrierCodes")
    private List<String> carrierCodes;

    @JsonProperty("accountNumber")
    private AccountNumber accountNumber;

    @JsonProperty("systemOfMeasureType")
    private String systemOfMeasureType;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RequestedShipment {
        @JsonProperty("shipper")
        private Party shipper;

        @JsonProperty("recipients")
        private List<Party> recipients;

        @JsonProperty("shipDateStamp")
        private String shipDateStamp;

        @JsonProperty("requestedPackageLineItems")
        private List<RequestedPackageLineItem> requestedPackageLineItems;

        @JsonProperty("pickupType")
        private String pickupType;
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
        private String countryCode;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RequestedPackageLineItem {
        @JsonProperty("weight")
        private Weight weight;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Weight {
        @JsonProperty("units")
        private String units;

        @JsonProperty("value")
        private Double value;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AccountNumber {
        @JsonProperty("value")
        private String value;
    }
}
