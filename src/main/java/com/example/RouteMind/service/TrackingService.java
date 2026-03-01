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
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;


import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrackingService {

    private final ShipmentRepository shipmentRepository;
    private final TrackingEventRepository trackingEventRepository;
    private final ProviderFactory providerFactory;

    /**
     * Track shipment by tracking ID.
     * Fetches live status from provider API, saves new events to database,
     * and returns merged response with both API and DB events.
     */
    @Transactional
    public TrackingResponse trackShipment(String trackingId) {
        log.info("Tracking request for: {}", trackingId);

        // Step 1: Find shipment in database
        Shipment shipment = shipmentRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new RuntimeException("Shipment not found: " + trackingId));

        log.debug("Found shipment: {} with provider: {}", trackingId, shipment.getProviderCode());

        // Step 2: Get live status from provider (includes event history from API)
        DeliveryProviderAdapter adapter = providerFactory.getAdapter(shipment.getProviderCode());
        TrackingResponse response = adapter.trackShipment(trackingId);

        // Ensure events list is not null
        if (ObjectUtils.isEmpty(response.getEvents())) {
            response.setEvents(new ArrayList<>());
        }

        // Step 3: Save new events from API to database
        int savedCount = saveNewEventsFromApi(shipment, response.getEvents());
        if (savedCount > 0) {
            log.info("Saved {} new tracking events for shipment {}", savedCount, trackingId);
        }

        // Step 4: Get all events from database (now includes newly saved ones)
        List<TrackingEvent> dbEvents = trackingEventRepository
                .findByShipmentIdOrderByEventTimeDesc(shipment.getId());

        log.debug("Found {} events in database for shipment {}", dbEvents.size(), trackingId);

        // Step 5: Merge API events with DB events (DB is source of truth for history)
        List<TrackingEventDto> allEvents = mergeEvents(response.getEvents(), dbEvents);

        // Step 6: Set merged events and external order ID
        response.setEvents(allEvents)
            .setExternalOrderId(shipment.getOrder().getExternalOrderId());

        // Step 7: Update shipment status if changed
        if (response.getCurrentStatus() != null &&
                !response.getCurrentStatus().equals(shipment.getCurrentStatus())) {

            shipment.setCurrentStatus(response.getCurrentStatus())
                .setCurrentLocation(response.getCurrentLocation());

            if (ObjectUtils.isNotEmpty(response.getEstimatedDeliveryDate())) {
                shipment.setEstimatedDeliveryDate(response.getEstimatedDeliveryDate());
            }
            shipmentRepository.save(shipment);
            log.info("Updated shipment status: {} -> {}", trackingId, response.getCurrentStatus());
        }

        log.info("Tracking response prepared with {} total events", allEvents.size());
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

    /**
     * Save new tracking events from API response to database.
     * Only saves events that don't already exist (based on timestamp and status).
     *
     * @param shipment The shipment entity
     * @param apiEvents List of events from API response
     * @return Number of new events saved
     */
    private int saveNewEventsFromApi(Shipment shipment, List<TrackingEventDto> apiEvents) {
        if (CollectionUtils.isEmpty(apiEvents)) {
            return 0;
        }

        // Get existing events from DB to check for duplicates
        List<TrackingEvent> existingEvents = trackingEventRepository
                .findByShipmentIdOrderByEventTimeDesc(shipment.getId());

        // Create a set of existing event keys (timestamp + status) for quick lookup
        Set<String> existingEventKeys = existingEvents.stream()
                .map(e -> {
                    LocalDateTime eventTime = e.getEventTime() != null ? e.getEventTime() : LocalDateTime.now();
                    return eventTime.toString() + "|" + e.getStatusCode();
                })
                .collect(Collectors.toSet());

        int savedCount = 0;
        for (TrackingEventDto apiEvent : apiEvents) {
            // Create unique key for this event (timestamp + status)
            LocalDateTime eventTime = apiEvent.getTimestamp() != null
                    ? apiEvent.getTimestamp()
                    : LocalDateTime.now();
            String eventKey = eventTime.toString() + "|" + apiEvent.getStatus();

            // Skip if already exists
            if (existingEventKeys.contains(eventKey)) {
                log.debug("Skipping duplicate event: {}", eventKey);
                continue;
            }

            // Save new event
            TrackingEvent event = TrackingEvent.builder()
                    .shipment(shipment)
                    .statusCode(apiEvent.getStatus())
                    .description(apiEvent.getDescription() != null
                            ? apiEvent.getDescription()
                            : "Status update")
                    .location(apiEvent.getLocation() != null
                            ? apiEvent.getLocation()
                            : "")
                    .eventTime(eventTime)
                    .partnerStatusCode(apiEvent.getStatus().name())
                    .build();

            trackingEventRepository.save(event);
            existingEventKeys.add(eventKey); // Add to set to avoid duplicates in same batch
            savedCount++;

            log.debug("Saved new tracking event: {} at {}", apiEvent.getStatus(), eventTime);
        }

        return savedCount;
    }

    /**
     * Merge API events with DB events, prioritizing DB events (source of truth).
     * Returns combined list sorted by timestamp (oldest first).
     *
     * @param apiEvents Events from API response
     * @param dbEvents Events from database
     * @return Merged and sorted list of events
     */
    private List<TrackingEventDto> mergeEvents(List<TrackingEventDto> apiEvents,
                                               List<TrackingEvent> dbEvents) {
        // Convert DB events to DTOs (DB is source of truth)
        List<TrackingEventDto> merged = dbEvents.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        // Create set of DB event keys to avoid duplicates
        Set<String> dbEventKeys = dbEvents.stream()
                .map(e -> {
                    LocalDateTime eventTime = e.getEventTime() != null ? e.getEventTime() : LocalDateTime.now();
                    return eventTime.toString() + "|" + e.getStatusCode();
                })
                .collect(Collectors.toSet());

        // Add API events that don't exist in DB (by timestamp and status)
        if (CollectionUtils.isNotEmpty(apiEvents)) {
            for (TrackingEventDto apiEvent : apiEvents) {
                LocalDateTime eventTime = apiEvent.getTimestamp() != null
                        ? apiEvent.getTimestamp()
                        : LocalDateTime.now();
                String eventKey = eventTime.toString() + "|" + apiEvent.getStatus();

                // Only add if not already in DB
                if (!dbEventKeys.contains(eventKey)) {
                    merged.add(apiEvent);
                    log.debug("Added API event to merged list: {}", eventKey);
                }
            }
        }

        // Sort by timestamp (oldest first, most recent last)
        merged.sort(Comparator.comparing(
                TrackingEventDto::getTimestamp,
                Comparator.nullsLast(Comparator.naturalOrder())
        ));

        return merged;
    }
}
