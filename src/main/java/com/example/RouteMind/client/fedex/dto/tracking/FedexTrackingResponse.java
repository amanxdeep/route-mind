package com.example.RouteMind.client.fedex.dto.tracking;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Enhanced FedEx Track API response with detailed scan events.
 */
@Data
public class FedexTrackingResponse {

    @JsonProperty("output")
    private Output output;

    @Data
    public static class Output {
        @JsonProperty("completeTrackResults")
        private List<CompleteTrackResult> completeTrackResults;
    }

    @Data
    public static class CompleteTrackResult {
        @JsonProperty("trackingNumber")
        private String trackingNumber;

        @JsonProperty("trackResults")
        private List<TrackResult> trackResults;
    }

    @Data
    public static class TrackResult {
        @JsonProperty("latestStatusDetail")
        private StatusDetail latestStatusDetail;

        @JsonProperty("scanEvents")
        private List<ScanEvent> scanEvents;  // NEW: Detailed scan history

        @JsonProperty("dateDetail")
        private List<DateDetail> dateDetail;  // NEW: Estimated delivery dates
    }

    @Data
    public static class StatusDetail {
        @JsonProperty("code")
        private String code;

        @JsonProperty("description")
        private String description;

        @JsonProperty("scanLocation")
        private ScanLocation scanLocation;

        @JsonProperty("eventDescription")  // NEW: Additional description
        private String eventDescription;

        @JsonProperty("eventTime")  // NEW: Timestamp for latest status
        private String eventTime;
    }

    @Data
    public static class ScanLocation {
        @JsonProperty("city")
        private String city;

        @JsonProperty("stateOrProvinceCode")
        private String stateOrProvinceCode;

        @JsonProperty("countryCode")  // NEW: Country code
        private String countryCode;
    }

    // NEW: Detailed scan event structure
    @Data
    public static class ScanEvent {
        @JsonProperty("date")
        private String date;  // Format: "2024-01-15T10:30:00"

        @JsonProperty("eventType")
        private String eventType;

        @JsonProperty("eventDescription")
        private String eventDescription;

        @JsonProperty("scanLocation")
        private ScanLocation scanLocation;

        @JsonProperty("eventTypeCode")
        private String eventTypeCode;  // FedEx status code
    }

    // NEW: Date details for estimated delivery
    @Data
    public static class DateDetail {
        @JsonProperty("type")
        private String type;  // e.g., "ESTIMATED_DELIVERY", "ACTUAL_DELIVERY"

        @JsonProperty("date")
        private String date;
    }
}
