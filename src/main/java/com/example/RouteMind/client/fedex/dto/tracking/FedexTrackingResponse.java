package com.example.RouteMind.client.fedex.dto.tracking;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FedexTrackingResponse {

    @JsonProperty("output")
    private Output output;

    @JsonProperty("errors")
    private List<Error> errors;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Output {
        @JsonProperty("completeTrackResults")
        private List<CompleteTrackResult> completeTrackResults;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompleteTrackResult {
        @JsonProperty("trackingNumber")
        private String trackingNumber;

        @JsonProperty("trackResults")
        private List<TrackResult> trackResults;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrackResult {
        @JsonProperty("latestStatusDetail")
        private StatusDetail latestStatusDetail;

        @JsonProperty("scanEvents")
        private List<ScanEvent> scanEvents;

        @JsonProperty("dateAndTimes")
        private List<DateAndTime> dateAndTimes;

        @JsonProperty("estimatedDeliveryTimeWindow")
        private TimeWindow estimatedDeliveryTimeWindow;

        @JsonProperty("serviceCommitMessage")
        private ServiceCommitMessage serviceCommitMessage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusDetail {
        @JsonProperty("code")
        private String code;

        @JsonProperty("description")
        private String description;

        @JsonProperty("scanLocation")
        private ScanLocation scanLocation;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScanLocation {
        @JsonProperty("city")
        private String city;

        @JsonProperty("stateOrProvinceCode")
        private String stateOrProvinceCode;

        @JsonProperty("countryCode")
        private String countryCode;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScanEvent {
        @JsonProperty("date")
        private String date;

        @JsonProperty("eventType")
        private String eventType;

        @JsonProperty("eventDescription")
        private String eventDescription;

        @JsonProperty("scanLocation")
        private ScanLocation scanLocation;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DateAndTime {
        @JsonProperty("type")
        private String type;

        @JsonProperty("dateTime")
        private String dateTime;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeWindow {
        @JsonProperty("window")
        private Window window;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Window {
        @JsonProperty("begins")
        private String begins;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceCommitMessage {
        @JsonProperty("type")
        private String type;
        @JsonProperty("message")
        private String message;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Error {
        @JsonProperty("code")
        private String code;

        @JsonProperty("message")
        private String message;
    }
}
