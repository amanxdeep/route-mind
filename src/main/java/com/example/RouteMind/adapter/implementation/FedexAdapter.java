package com.example.RouteMind.adapter.implementation;

import com.example.RouteMind.dto.Request.CreateOrderRequest;
import com.example.RouteMind.dto.Response.OrderResponse;
import com.example.RouteMind.dto.Response.TrackingResponse;
import com.example.RouteMind.adapter.DeliveryProviderAdapter;
import com.example.RouteMind.enums.DeliveryStatus;
import com.example.RouteMind.enums.ProviderCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;


/**
 * FEDEX API integration.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class FedexAdapter implements DeliveryProviderAdapter {

    @Override
    public ProviderCode getProviderCode() {
        return ProviderCode.FEDEX;
    }
    @Override
    public boolean checkServiceability(String pickupPincode, String deliveryPincode) {
        log.info( "FedEx: Checking serviceability {} -> {}", pickupPincode, deliveryPincode);
        // TODO: Call FEDEX API
        return true;
    }
    @Override
    public BigDecimal calculateRate(String pickupPincode, String deliveryPincode, Double weightKg) {
        log.info("FedEx: Calculating rate for {} kg", weightKg);
        // TODO: Call FEDEX API
        return new BigDecimal("80.00");
    }
    @Override
    public OrderResponse createShipment(CreateOrderRequest request) {
        log.info("FedEx: Creating shipment for order {}", request.getExternalOrderId());
        // TODO: Call FEDEX API
        return OrderResponse.builder()
                .trackingId("FX" + System.currentTimeMillis())
                .provider(ProviderCode.FEDEX)
                .status(DeliveryStatus.ORDER_CONFIRMED)
                .message("Shipment created with FedEx")
                .build();
    }
    @Override
    public TrackingResponse trackShipment(String trackingId) {
        log.info("FedEx: Tracking shipment {}", trackingId);
        // TODO: Call FEDEX API
        return TrackingResponse.builder()
                .trackingId(trackingId)
                .provider(ProviderCode.FEDEX)
                .currentStatus(DeliveryStatus.IN_TRANSIT)
                .build();
    }
    @Override
    public boolean cancelShipment(String trackingId) {
        log.info("FedEx: Cancelling shipment {}", trackingId);
        // TODO: Call FEDEX API
        return true;
    }

}
