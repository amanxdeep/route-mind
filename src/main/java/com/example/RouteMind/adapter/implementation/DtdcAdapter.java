package com.example.RouteMind.adapter.implementation;

import com.example.RouteMind.adapter.DeliveryProviderAdapter;
import com.example.RouteMind.dto.Request.CreateOrderRequest;
import com.example.RouteMind.dto.Response.OrderResponse;
import com.example.RouteMind.dto.Response.TrackingResponse;
import com.example.RouteMind.enums.DeliveryStatus;
import com.example.RouteMind.enums.ProviderCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

/**
 * SAMPLE / MOCK ONLY: illustrative DTDC placeholder; no DTDC systems are accessed.
 */
@Component
@Slf4j
public class DtdcAdapter implements DeliveryProviderAdapter {
    @Override
    public ProviderCode getProviderCode() {
        return ProviderCode.DTDC;
    }

    @Override
    public boolean checkServiceability(String pickupPincode, String deliveryPincode) {
        throw new UnsupportedOperationException(
                "SAMPLE / MOCK ONLY: DTDC serviceability checks are not implemented.");
    }

    @Override
    public BigDecimal calculateRate(String pickupPincode, String deliveryPincode, Double weightKg) {
        log.info("SAMPLE / MOCK DATA: Returning illustrative DTDC rate for {} kg", weightKg);
        return new BigDecimal("60.00");
    }

    @Override
    public OrderResponse createShipment(CreateOrderRequest request) {
        log.info("SAMPLE / MOCK DATA: Simulating DTDC shipment response for order {}",
                request.getExternalOrderId());
        return OrderResponse.builder()
                .trackingId("SAMPLE-MOCK-DT-" + System.currentTimeMillis())
                .provider(ProviderCode.DTDC)
                .status(DeliveryStatus.ORDER_CONFIRMED)
                .message("SAMPLE / MOCK DATA: Simulated shipment confirmation only; no shipment was created.")
                .build();
    }

    @Override
    public TrackingResponse trackShipment(String trackingId) {
        log.info("SAMPLE / MOCK DATA: Returning illustrative DTDC tracking status for {}", trackingId);
        return TrackingResponse.builder()
                .trackingId(trackingId)
                .provider(ProviderCode.DTDC)
                .currentStatus(DeliveryStatus.IN_TRANSIT)
                .dataNotice("SAMPLE / MOCK DATA: Simulated tracking status only; no DTDC systems were accessed.")
                .build();
    }

    @Override
    public boolean cancelShipment(String trackingId) {
        throw new UnsupportedOperationException(
                "SAMPLE / MOCK ONLY: DTDC shipment cancellation is not implemented.");
    }
}
