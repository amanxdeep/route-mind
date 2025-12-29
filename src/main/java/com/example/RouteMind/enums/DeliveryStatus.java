package com.example.RouteMind.enums;
/*
 * Standard delivery status codes.
 * Each provider has different codes - we map them to these standard ones.
 */
import lombok.Getter;

public enum DeliveryStatus {

    ORDER_PLACED("Order has been Placed"),
    ORDER_CONFIRMED("Order confirmed by provider"),

    PICKUP_SCHEDULED("Pickup scheduled by provider"),
    PICKED_UP("Package picked up by provider"),

    IN_TRANSIT("Package is in transit"),
    OUT_FOR_DELIVERY("Package is out for delivery"),

    DELIVERY_SUCCESS("Package has been delivered"),
    DELIVERY_FAILED("Delivery attemptfailed"),

    RTO_INITIATED("Return to origin initiated"),
    RTO_COMPLETED("Package returned to origin"),

    CANCELLED("Order cancelled by customer");


    private final String description;

    DeliveryStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
