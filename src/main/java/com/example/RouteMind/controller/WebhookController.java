package com.example.RouteMind.controller;

import com.example.RouteMind.dto.Response.GenericResponse;
import com.example.RouteMind.constants.ApiConstants;
import com.example.RouteMind.enums.DeliveryStatus;
import com.example.RouteMind.entity.Shipment;
import com.example.RouteMind.entity.TrackingEvent;
import com.example.RouteMind.repository.ShipmentRepository;
import com.example.RouteMind.repository.TrackingEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Map;
/**
 * Receives status updates from delivery providers.
 * Each provider posts updates here when status changes.
 */
@RestController
@RequestMapping(ApiConstants.API_V1 + ApiConstants.WEBHOOKS)
@RequiredArgsConstructor
@Slf4j
public class WebhookController {
    private final ShipmentRepository shipmentRepository;
    private final TrackingEventRepository trackingEventRepository;
    /**
     * POST /api/v1/webhooks/bluedart
     * Receive status update from BlueDart.
     */
    @PostMapping(ApiConstants.WEBHOOK_BLUEDART)
    public GenericResponse<String> bluedartWebhook(
            @RequestBody Map<String, Object> payload) {

        log.info("BlueDart webhook received: {}", payload);

        // Extract data from payload
        String trackingId = (String) payload.get("trackingId");
        String statusCode = (String) payload.get("status");
        String location = (String) payload.get("location");
        String description = (String) payload.get("description");

        // Process update
        processStatusUpdate(trackingId, statusCode, location, description);

        return GenericResponse.success("OK");
    }
    /**
     * POST /api/v1/webhooks/delhivery
     * Receive status update from Delhivery.
     */
    @PostMapping(ApiConstants.WEBHOOK_FEDEX)
    public GenericResponse<String> fedexWebhook(
            @RequestBody Map<String, Object> payload) {
        log.info("FedEx webhook received: {}", payload);

        String trackingId = (String) payload.get("waybill");
        String statusCode = (String) payload.get("status_code");
        String location = (String) payload.get("city");
        String description = (String) payload.get("status_description");

        processStatusUpdate(trackingId, statusCode, location, description);

        return GenericResponse.success("OK");
    }
    /**
     * POST /api/v1/webhooks/dtdc
     * Receive status update from DTDC.
     */
    @PostMapping(ApiConstants.WEBHOOK_DTDC)
    public GenericResponse<String> dtdcWebhook(
            @RequestBody Map<String, Object> payload) {

        log.info("DTDC webhook received: {}", payload);

        String trackingId = (String) payload.get("consignment_no");
        String statusCode = (String) payload.get("status");
        String location = (String) payload.get("dest_city");
        String description = (String) payload.get("remarks");

        processStatusUpdate(trackingId, statusCode, location, description);

        return GenericResponse.success("OK");
    }
    /**
     * Process status update from any provider.
     */
    private void processStatusUpdate(String trackingId, String partnerStatusCode,
                                     String location, String description) {

        // 1. Find shipment
        Shipment shipment = shipmentRepository.findByTrackingId(trackingId)
                .orElse(null);

        if (shipment == null) {
            log.warn("Shipment not found: {}", trackingId);
            return;
        }
        // 2. Map partner status to our status
        DeliveryStatus status = mapStatus(partnerStatusCode);
        // 3. Check for duplicate event
        if (trackingEventRepository.existsByShipmentIdAndPartnerStatusCode(
                shipment.getId(), partnerStatusCode)) {
            log.info("Duplicate event ignored: {}", partnerStatusCode);
            return;
        }
        // 4. Save tracking event
        TrackingEvent event = TrackingEvent.builder()
                .shipment(shipment)
                .statusCode(status)
                .partnerStatusCode(partnerStatusCode)
                .location(location)
                .description(description)
                .eventTime(LocalDateTime.now())
                .build();
        trackingEventRepository.save(event);
        // 5. Update shipment status
        shipment.setCurrentStatus(status);
        shipment.setCurrentLocation(location);
        shipmentRepository.save(shipment);
        log.info("Status updated: {} -> {}", trackingId, status);
    }
    /**
     * Map provider status codes to our standard status.
     */
    private DeliveryStatus mapStatus(String partnerCode) {
        // Common mappings
        if (partnerCode == null) {
            log.warn("Received null partner code, defaulting to IN_TRANSIT");
            return DeliveryStatus.IN_TRANSIT;
        }
        return switch (partnerCode.toUpperCase()) {
            case "PKD", "PICKED", "PICKUP_DONE" -> DeliveryStatus.PICKED_UP;
            case "ITR", "INTRANSIT", "IN_TRANSIT" -> DeliveryStatus.IN_TRANSIT;
            case "OFD", "OUT_FOR_DELIVERY" -> DeliveryStatus.OUT_FOR_DELIVERY;
            case "DLV", "DELIVERED", "DELIVERY_DONE" -> DeliveryStatus.DELIVERY_SUCCESS;
            case "RTO", "RETURNED" -> DeliveryStatus.RTO_INITIATED;
            default -> DeliveryStatus.IN_TRANSIT;
        };
    }
}