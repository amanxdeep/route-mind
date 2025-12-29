package com.example.RouteMind.service;

import com.example.RouteMind.Dto.Request.CreateOrderRequest;
import com.example.RouteMind.Dto.Response.OrderResponse;
import com.example.RouteMind.adapter.DeliveryProviderAdapter;
import com.example.RouteMind.entity.Order;
import com.example.RouteMind.entity.Shipment;
import com.example.RouteMind.enums.DeliveryStatus;
import com.example.RouteMind.enums.ProviderCode;
import com.example.RouteMind.factory.ProviderFactory;
import com.example.RouteMind.repository.OrderRepository;
import com.example.RouteMind.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.UUID;

@Service                    // Marks this as a Spring service (business logic layer)
@RequiredArgsConstructor    // Lombok: generates constructor for final fields
@Slf4j                      // Lombok: creates logger as 'log'
public class OrderService {
    private final OrderRepository orderRepository;       // Database access for orders
    private final ShipmentRepository shipmentRepository; // Database access for shipments
    private final ProviderFactory providerFactory;       // Get adapter by provider code

    @Transactional  // If anything fails, rollback ALL database changes
    public OrderResponse createOrder(CreateOrderRequest request) {
        log.info("Creating order: {}", request.getExternalOrderId());

        // 1. Check if order already exists
        if (orderRepository.existsByExternalOrderId(request.getExternalOrderId())) {
            throw new RuntimeException("Order already exists: " + request.getExternalOrderId());
        }
        // 2. Save order to database
        Order order = mapToOrder(request);   // Convert DTO → Entity
        order = orderRepository.save(order); // Save & get ID

        // 3. Select provider (use preferred or default to BLUEDART)
        ProviderCode providerCode = request.getPreferredProvider() != null
                ? request.getPreferredProvider()  // Customer chose specific provider
                : ProviderCode.BLUEDART;          // Default if not specified

        // 4. Create shipment with provider
        DeliveryProviderAdapter adapter = providerFactory.getAdapter(providerCode);
        OrderResponse response = adapter.createShipment(request);

        // 5. Save shipment to database
        Shipment shipment = Shipment.builder()
                .order(order)                              // Link to parent order
                .trackingId(response.getTrackingId())      // From provider
                .providerCode(providerCode)                // Which provider
                .serviceType(request.getServiceType())     // EXPRESS/STANDARD/etc
                .currentStatus(DeliveryStatus.ORDER_CONFIRMED)
                .build();
        shipmentRepository.save(shipment);

        // 6. Return response
        response.setOrderId(order.getId());
        response.setExternalOrderId(order.getExternalOrderId());
        return response;
    }

    /**
     * Get order by ID.
     */
    public Optional<Order> getOrderById(UUID id) {
        return orderRepository.findById(id);
    }

    /**
     * Get order by external ID.
     */
    public Optional<Order> getOrderByExternalId(String externalOrderId) {
        return orderRepository.findByExternalOrderId(externalOrderId);
    }

    /**
     * Map request DTO to Order entity.
     */
    private Order mapToOrder(CreateOrderRequest request) {
        return Order.builder()
                .externalOrderId(request.getExternalOrderId())
                .customerId(request.getCustomerId())
                .pickUpName(request.getPickupAddress().getName())
                .pickUpPhone(request.getPickupAddress().getPhone())
                .pickUpAddress(request.getPickupAddress().getAddress())
                .pickUpCity(request.getPickupAddress().getCity())
                .pickUpState(request.getPickupAddress().getState())
                .pickUpPinCode(request.getPickupAddress().getPincode())
                .deliverName(request.getDeliveryAddress().getName())
                .deliveryPhone(request.getDeliveryAddress().getPhone())
                .deliveryAddress(request.getDeliveryAddress().getAddress())
                .deliveryCity(request.getDeliveryAddress().getCity())
                .deliveryState(request.getDeliveryAddress().getState())
                .deliveryPinCode(request.getDeliveryAddress().getPincode())
                .paymentMode(request.getPaymentMode())
                .codValue(request.getCodAmount())
                .build();
    }

}