package com.example.RouteMind.exception;


/**
 * Thrown when order is not found.
 */
public class OrderNotFoundException extends RouteMindException {

    public OrderNotFoundException(String orderId) {
        super("ORDER_NOT_FOUND", "Order not found: " + orderId);
    }
}
