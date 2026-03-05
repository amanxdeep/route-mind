package com.example.RouteMind.client.fedex.dto.shipment;

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
public class FedexShipmentRequest {

    @JsonProperty("requestedShipment")
    private RequestedShipment requestedShipment;

    @JsonProperty("accountNumber")
    private AccountNumber accountNumber;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RequestedShipment {
        
        @JsonProperty("shipper")
        private Party shipper;

        @JsonProperty("recipients")
        private List<Party> recipients;

        @JsonProperty("serviceType")
        private String serviceType;

        @JsonProperty("packagingType")
        private String packagingType;

        @JsonProperty("pickupType")
        private String pickupType;

        @JsonProperty("shippingChargesPayment")
        private ShippingChargesPayment shippingChargesPayment;

        @JsonProperty("labelSpecification")
        private LabelSpecification labelSpecification;

        @JsonProperty("requestedPackageLineItems")
        private List<PackageLineItem> requestedPackageLineItems;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Party {
        
        @JsonProperty("contact")
        private Contact contact;

        @JsonProperty("address")
        private Address address;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Contact {
        
        @JsonProperty("personName")
        private String personName;

        @JsonProperty("phoneNumber")
        private String phoneNumber;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Address {
        
        @JsonProperty("streetLines")
        private List<String> streetLines;

        @JsonProperty("city")
        private String city;

        @JsonProperty("stateOrProvinceCode")
        private String stateOrProvinceCode;

        @JsonProperty("postalCode")
        private String postalCode;

        @JsonProperty("countryCode")
        private String countryCode;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ShippingChargesPayment {
        
        @JsonProperty("paymentType")
        private String paymentType;

        @JsonProperty("payor")
        private Payor payor;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Payor {
        
        @JsonProperty("responsibleParty")
        private ResponsibleParty responsibleParty;

        @JsonProperty("address")
        private Address address;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ResponsibleParty {
        
        @JsonProperty("accountNumber")
        private ValueKey accountNumber;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ValueKey {
        
        @JsonProperty("value")
        private String value;

        @JsonProperty("key")
        private String key;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LabelSpecification {
        // Can be extended with format, size, etc.
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
        private String units; // LB or KG

        @JsonProperty("value")
        private String value;
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
