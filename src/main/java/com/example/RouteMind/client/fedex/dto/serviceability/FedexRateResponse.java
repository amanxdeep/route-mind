package com.example.RouteMind.client.fedex.dto.serviceability;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * Minimal FedEx Rate API response to extract a total charge.
 */
@Data
public class FedexRateResponse {

    @JsonProperty("transactionId")
    private String transactionId;

    @JsonProperty("output")
    private Output output;

    @Data
    public static class Output {
        @JsonProperty("rateReplyDetails")
        private List<RateReplyDetail> rateReplyDetails;
    }

    @Data
    public static class RateReplyDetail {
        @JsonProperty("serviceType")
        private String serviceType;

        @JsonProperty("serviceName")
        private String serviceName;

        @JsonProperty("ratedShipmentDetails")
        private List<RatedShipmentDetail> ratedShipmentDetails;

        @JsonProperty("operationalDetail")
        private OperationalDetail operationalDetail;
    }

    @Data
    public static class RatedShipmentDetail {
        @JsonProperty("rateType")
        private String rateType;

        @JsonProperty("totalNetCharge")
        private BigDecimal totalNetCharge;

        @JsonProperty("currency")
        private String currency;
    }

    @Data
    public static class OperationalDetail {
        @JsonProperty("transitTime")
        private String transitTime;

        @JsonProperty("deliveryDay")
        private String deliveryDay;

        @JsonProperty("commitDate")
        private String commitDate;
    }
}
