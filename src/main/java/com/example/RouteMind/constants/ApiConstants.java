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
    public static final String FEDEX = "/fedex";
    public static final String FEDEX_RATES = "/rates";
    public static final String WEBHOOKS = "/webhooks";

    public static final String BY_TRACKING_ID = "/tracking/{trackingId}";

    // order-specific paths
    public static final String ORDER_ID = "/{id}";
    public static final String EXTERNAL_ORDER_ID = "/external/{externalOrderId}";

    // tracking paths
    public static final String TRACKING_ID = "/{trackingId}";

    // webhook subpaths
    public static final String WEBHOOK_BLUEDART = "/bluedart";
    public static final String WEBHOOK_FEDEX = "/fedex";
    public static final String WEBHOOK_DTDC = "/dtdc";
}
