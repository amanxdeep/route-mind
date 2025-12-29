package com.example.RouteMind.adapter.implementation;

import com.example.RouteMind.Dto.Request.CreateOrderRequest;
import com.example.RouteMind.Dto.Response.OrderResponse;
import com.example.RouteMind.Dto.Response.TrackingResponse;
import com.example.RouteMind.adapter.DeliveryProviderAdapter;
import com.example.RouteMind.enums.DeliveryStatus;
import com.example.RouteMind.enums.ProviderCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
/**
 * BlueDart API integration.
 * Handles all communication with BlueDart's systems.
 */
@Component
@RequiredArgsConstructor
@Slf4j

public class BlueDartAdapter implements DeliveryProviderAdapter {
    @Override
    public ProviderCode getProviderCode() {
        return ProviderCode.BLUEDART;
    }

    @Override
    public boolean checkServiceability(String pickupPincode, String deliveryPincode) {
        log.info("BlueDart: Checking serviceability {} -> {}", pickupPincode, deliveryPincode);
        // TODO: Call BlueDart API
        return true;
    }

    @Override
    public BigDecimal calculateRate(String pickupPincode, String deliveryPincode, Double weightKg) {
        log.info("BlueDart: Calculating rate for {} kg", weightKg);
        // TODO: Call BlueDart API
        return new BigDecimal("100.00");
    }

    @Override
    public OrderResponse createShipment(CreateOrderRequest request) {
        log.info("BlueDart: Creating shipment for order {}", request.getExternalOrderId());
        // TODO: Call BlueDart API
        return OrderResponse.builder()
                .trackingId("BD" + System.currentTimeMillis())
                .provider(ProviderCode.BLUEDART)
                .status(DeliveryStatus.ORDER_CONFIRMED)
                .message("Shipment created with BlueDart")
                .build();
    }

    @Override
    public TrackingResponse trackShipment(String trackingId) {
        log.info("BlueDart: Tracking shipment {}", trackingId);
        // TODO: Call BlueDart API
        return TrackingResponse.builder()
                .trackingId(trackingId)
                .provider(ProviderCode.BLUEDART)
                .currentStatus(DeliveryStatus.IN_TRANSIT)
                .build();
    }

    @Override
    public boolean cancelShipment(String trackingId) {
        log.info("BlueDart: Cancelling shipment {}", trackingId);
        // TODO: Call BlueDart API
        return true;
    }

}
