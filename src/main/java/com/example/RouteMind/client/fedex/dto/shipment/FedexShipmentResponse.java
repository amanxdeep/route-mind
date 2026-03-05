package com.example.RouteMind.client.fedex.dto.shipment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * FedEx Shipment API Response
 */
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

    @JsonProperty("errors")
    private List<ErrorDetail> errors;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Output {
        
        @JsonProperty("transactionId")
        private String transactionId;

        @JsonProperty("customerTransactionId")
        private String customerTransactionId;

        @JsonProperty("shipmentId")
        private String shipmentId;

        @JsonProperty("jobId")
        private String jobId;

        @JsonProperty("warnings")
        private List<String> warnings;

        @JsonProperty("pleaseIgnore")
        private String pleaseIgnore;

        @JsonProperty("asynchronousShippingDataEligible")
        private Boolean asynchronousShippingDataEligible;

        @JsonProperty("completedShipmentDetail")
        private CompletedShipmentDetail completedShipmentDetail;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CompletedShipmentDetail {
        
        @JsonProperty("shipmentType")
        private String shipmentType;

        @JsonProperty("masterTrackingNumber")
        private String masterTrackingNumber;

        @JsonProperty("carrierCode")
        private String carrierCode;

        @JsonProperty("serviceName")
        private String serviceName;

        @JsonProperty("serviceId")
        private String serviceId;

        @JsonProperty("fieldStatuses")
        private List<FieldStatus> fieldStatuses;

        @JsonProperty("pieceResponses")
        private List<PieceResponse> pieceResponses;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PieceResponse {
        
        @JsonProperty("trackingNumber")
        private String trackingNumber;

        @JsonProperty("masterTrackingNumber")
        private String masterTrackingNumber;

        @JsonProperty("formId")
        private String formId;

        @JsonProperty("alerts")
        private List<Alert> alerts;
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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FieldStatus {
        
        @JsonProperty("name")
        private String name;

        @JsonProperty("status")
        private String status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ErrorDetail {
        
        @JsonProperty("code")
        private String code;

        @JsonProperty("parameterRelated")
        private String parameterRelated;

        @JsonProperty("message")
        private String message;
    }
}
