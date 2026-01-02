package com.example.RouteMind.client.fedex.dto.tracking;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Minimal FedEx Track API request.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FedexTrackingRequest {

    @JsonProperty("trackingInfo")
    private List<TrackingInfo> trackingInfo;

    @JsonProperty("includeDetailedScans")
    private Boolean includeDetailedScans;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TrackingInfo {
        @JsonProperty("trackingNumberInfo")
        private TrackingNumberInfo trackingNumberInfo;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TrackingNumberInfo {
        @JsonProperty("trackingNumber")
        private String trackingNumber;
    }
}
