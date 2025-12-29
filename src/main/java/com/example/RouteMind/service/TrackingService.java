package com.example.RouteMind.service;

import com.example.RouteMind.dto.Response.TrackingEventDto;
import com.example.RouteMind.dto.Response.TrackingResponse;
import com.example.RouteMind.adapter.DeliveryProviderAdapter;
import com.example.RouteMind.entity.Shipment;
import com.example.RouteMind.entity.TrackingEvent;
import com.example.RouteMind.factory.ProviderFactory;
import com.example.RouteMind.repository.ShipmentRepository;
import com.example.RouteMind.repository.TrackingEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrackingService {

    private final ShipmentRepository shipmentRepository;
    private final TrackingEventRepository trackingEventRepository;
    private final ProviderFactory providerFactory;

    public TrackingResponse trackShipment(String trackingId) {
        log.info("Tracking: {}", trackingId);
        // Find shipment
        Shipment shipment = shipmentRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new RuntimeException("Shipment not found: " + trackingId));
        // Get live status from provider
        DeliveryProviderAdapter adapter = providerFactory.getAdapter(shipment.getProviderCode());
        TrackingResponse response = adapter.trackShipment(trackingId);
        // Add history from database
        List<TrackingEvent> events = trackingEventRepository
                .findByShipmentIdOrderByEventTimeDesc(shipment.getId());
        response.setEvents(events.stream().map(this::mapToDto).collect(Collectors.toList()));
        response.setExternalOrderId(shipment.getOrder().getExternalOrderId());

        return response;
    }

    private TrackingEventDto mapToDto(TrackingEvent event) {
        return TrackingEventDto.builder()
                .status(event.getStatusCode())
                .description(event.getDescription())
                .location(event.getLocation())
                .timestamp(event.getEventTime())
                .build();
    }
}
