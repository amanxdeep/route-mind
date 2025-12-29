package com.example.RouteMind.adapter.implementation;

import com.example.RouteMind.adapter.DeliveryProviderAdapter;
import com.example.RouteMind.dto.Request.CreateOrderRequest;
import com.example.RouteMind.dto.Response.OrderResponse;
import com.example.RouteMind.dto.Response.TrackingResponse;
import com.example.RouteMind.enums.DeliveryStatus;
import com.example.RouteMind.enums.ProviderCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
/**
 * DTDC API integration.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DtdcAdapter implements DeliveryProviderAdapter {


    @Override
    public ProviderCode getProviderCode() {
        return ProviderCode.DTDC;
    }
    @Override
    public boolean checkServiceability(String pickupPincode, String deliveryPincode) {
        log.info("DTDC: Checking serviceability {} -> {}", pickupPincode, deliveryPincode);
        return true;
    }
    @Override
    public BigDecimal calculateRate(String pickupPincode, String deliveryPincode, Double weightKg) {
        log.info("DTDC: Calculating rate for {} kg", weightKg);
        return new BigDecimal("60.00");
    }
    @Override
    public OrderResponse createShipment(CreateOrderRequest request) {
        log.info("DTDC: Creating shipment for order {}", request.getExternalOrderId());
        return OrderResponse.builder()
                .trackingId("DT" + System.currentTimeMillis())
                .provider(ProviderCode.DTDC)
                .status(DeliveryStatus.ORDER_CONFIRMED)
                .message("Shipment created with DTDC")
                .build();
    }
    @Override
    public TrackingResponse trackShipment(String trackingId) {
        log.info("DTDC: Tracking shipment {}", trackingId);
        return TrackingResponse.builder()
                .trackingId(trackingId)
                .provider(ProviderCode.DTDC)
                .currentStatus(DeliveryStatus.IN_TRANSIT)
                .build();
    }
    @Override
    public boolean cancelShipment(String trackingId) {
        log.info("DTDC: Cancelling shipment {}", trackingId);
        return true;
    }
}
