package com.example.RouteMind.service;

import com.example.RouteMind.entity.Order;
import com.example.RouteMind.entity.Shipment;
import com.example.RouteMind.enums.DeliveryStatus;
import com.example.RouteMind.repository.OrderRepository;
import com.example.RouteMind.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderDataAccessService {
    private final OrderRepository orderRepository;
    private final ShipmentRepository shipmentRepository;

    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    public Optional<Order> getOrderById(UUID id) {
        return orderRepository.findById(id);
    }

    public Optional<Order> getOrderByExternalId(String externalOrderId) {
        return orderRepository.findByExternalOrderId(externalOrderId);
    }

    public void markOrderFailed(UUID orderId, String reason) {
        orderRepository.findById(orderId).ifPresent(o -> {
            o.setStatus(DeliveryStatus.DELIVERY_FAILED);
            orderRepository.save(o);
        });
    }

    public void saveShipmentAndMarkOrder(UUID orderId, Shipment shipment) {
        Order managed = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        shipment.setOrder(managed);
        shipmentRepository.save(shipment);
        managed.setStatus(DeliveryStatus.ORDER_CONFIRMED);
        orderRepository.save(managed);
    }
}
