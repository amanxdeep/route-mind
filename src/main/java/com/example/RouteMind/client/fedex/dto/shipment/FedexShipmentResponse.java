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
public class FedexShipmentResponse {

    @JsonProperty("transactionId")
    private String transactionId;

    @JsonProperty("customerTransactionId")
    private String customerTransactionId;

    @JsonProperty("output")
    private Output output;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Output {

        @JsonProperty("transactionShipments")
        private List<TransactionShipment> transactionShipments;

        @JsonProperty("alerts")
        private List<Alert> alerts;

        @JsonProperty("jobId")
        private String jobId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TransactionShipment {

        @JsonProperty("serviceType")
        private String serviceType;

        @JsonProperty("shipDatestamp")
        private String shipDatestamp;

        @JsonProperty("serviceCategory")
        private String serviceCategory;

        @JsonProperty("shipmentDocuments")
        private List<ShipmentDocument> shipmentDocuments;

        @JsonProperty("pieceResponses")
        private List<PieceResponse> pieceResponses;

        @JsonProperty("serviceName")
        private String serviceName;

        @JsonProperty("masterTrackingNumber")
        private String masterTrackingNumber;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ShipmentDocument {

        @JsonProperty("trackingNumber")
        private String trackingNumber;

        @JsonProperty("encodedLabel")
        private String encodedLabel;

        @JsonProperty("url")
        private String url;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PieceResponse {

        @JsonProperty("netChargeAmount")
        private Double netChargeAmount;

        @JsonProperty("trackingNumber")
        private String trackingNumber;

        @JsonProperty("masterTrackingNumber")
        private String masterTrackingNumber;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Alert {

        @JsonProperty("code")
        private String code;

        @JsonProperty("message")
        private String message;
    }
}