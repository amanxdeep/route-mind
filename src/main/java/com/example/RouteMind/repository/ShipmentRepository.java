package com.example.RouteMind.repository;

import com.example.RouteMind.entity.Shipment;
import com.example.RouteMind.enums.DeliveryStatus;
import com.example.RouteMind.enums.ProviderCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShipmentRepository extends JpaRepository<Shipment, UUID> {

    // Find shipment by tracking number
    Optional<Shipment> findByTrackingId(String awbNumber);

    // Find all shipments for an order
    List<Shipment> findByOrderId(UUID orderId);

    // Find active shipments by status (for polling)
    List<Shipment> findByCurrentStatusAndIsActiveTrue(DeliveryStatus status);

    // Find shipments by provider
    List<Shipment> findByProviderCode(ProviderCode providerCode);
}
