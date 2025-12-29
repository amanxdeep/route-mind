package com.example.RouteMind.constants;

public class ApiConstants {
    private ApiConstants() {}

    public static final String API_V1 = "/api/v1";

    //RESOURCES

    //pre-order
    public static final String SERVICEABILITY = "/serviceability";
    public static final String ESTIMATES = "/estimates";

    //transit-order
    public static final String ORDERS = "/orders";
    public static final String TRACKING = "/tracking";

    public static final String PROVIDERS = "/providers";
    public static final String WEBHOOKS = "/webhooks";

    public static final String BY_TRACKING_ID = "/tracking/{trackingId}";
}
