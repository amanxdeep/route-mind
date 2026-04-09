package com.example.RouteMind.client.fedex.dto.serviceability;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class FedexTransitTimesResponse {

    @JsonProperty("transactionId")
    private String transactionId;

    @JsonProperty("output")
    private Output output;

    @Data
    public static class Output {
        @JsonProperty("transitTimes")
        private List<TransitTimes> transitTimes;
    }

    @Data
    public static class TransitTimes {
        @JsonProperty("transitTimeDetails")
        private List<TransitTimeDetail> transitTimeDetails;
    }

    @Data
    public static class TransitTimeDetail {
        @JsonProperty("serviceType")
        private String serviceType;

        @JsonProperty("commit")
        private Commit commit;
    }

    @Data
    public static class Commit {
        @JsonProperty("transitDays")
        private TransitDays transitDays;
    }

    @Data
    public static class TransitDays {
        @JsonProperty("description")
        private String description;

        @JsonProperty("minimumTransitTime")
        private String minimumTransitTime;

        @JsonProperty("maximumTransitTime")
        private String maximumTransitTime;
    }
}
