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

        @JsonProperty("ratedShipmentDetails")
        private List<RatedShipmentDetail> ratedShipmentDetails;
    }

    @Data
    public static class RatedShipmentDetail {
        @JsonProperty("totalNetCharge")
        private Money totalNetCharge;
    }

    @Data
    public static class Money {
        @JsonProperty("amount")
        private BigDecimal amount;

        @JsonProperty("currency")
        private String currency;
    }
}
