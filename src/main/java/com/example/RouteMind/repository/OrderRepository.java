package com.example.RouteMind.repository;

import com.example.RouteMind.entity.Order;
import com.example.RouteMind.enums.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
/**
 * Database operations for Order entity.
 * Spring auto-generates SQL from method names.
 */
public interface OrderRepository extends JpaRepository<Order,UUID> {

    // Find order by web app's order ID
    Optional<Order> findByExternalOrderId(String externalOrderId);

    // Find all orders for a customer
    List<Order> findByCustomerId(String customerId);

    // Find orders by status
    List<Order> findByStatus(DeliveryStatus status);

    // Check if order exists
    boolean existsByExternalOrderId(String externalOrderId);
}
