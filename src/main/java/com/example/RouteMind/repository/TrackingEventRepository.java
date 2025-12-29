package com.example.RouteMind.repository;

import com.example.RouteMind.entity.TrackingEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
/**
 * Database operations for TrackingEvent entity.
 * Used to store and retrieve tracking history.
 */

public interface TrackingEventRepository extends JpaRepository<TrackingEvent,UUID> {

    List<TrackingEvent> findByShipmentIdOrderByEventTimeDesc(UUID shipmentId);

    // Get latest event for a shipment
    TrackingEvent findFirstByShipmentIdOrderByEventTimeDesc(UUID shipmentId);

    // Check if event already exists (avoid duplicates)
    boolean existsByShipmentIdAndPartnerStatusCode(UUID shipmentId, String partnerStatusCode);

}
