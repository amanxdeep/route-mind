package com.example.RouteMind.service;

import com.example.RouteMind.dto.Request.CreateOrderRequest;
import com.example.RouteMind.dto.Response.OrderResponse;
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
import com.example.RouteMind.exception.ProviderException;
import org.springframework.util.ObjectUtils;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final ShipmentRepository shipmentRepository;
    private final ProviderFactory providerFactory;

    public OrderResponse createOrder(CreateOrderRequest request) {
        log.info("Creating order: {}", request.getExternalOrderId());

        // 1. Check if order already exists
        if (orderRepository.existsByExternalOrderId(request.getExternalOrderId())) {
            throw new RuntimeException("Order already exists: " + request.getExternalOrderId());
        }
        // 2. Save order to database (quick, isolated transaction)
        Order order = mapToOrder(request);
        order = saveOrderTransactional(order);

        // 3. Select provider (use preferred or default to BLUEDART)
        ProviderCode providerCode = getProviderCode(request);

        // 4. Call external provider (do NOT hold DB transaction during this call)
        try {
            DeliveryProviderAdapter adapter = providerFactory.getAdapter(providerCode);
            OrderResponse response = adapter.createShipment(request);

            // 5. Persist shipment and update order status in a short transaction
            Shipment shipment = Shipment.builder()
                    .trackingId(response.getTrackingId())
                    .providerCode(providerCode)
                    .serviceType(request.getServiceType())
                    .currentStatus(DeliveryStatus.ORDER_CONFIRMED)
                    .build();

            saveShipmentAndMarkOrder(order.getId(), shipment);

            // 6. Return response
            response.setOrderId(order.getId())
                    .setExternalOrderId(order.getExternalOrderId());
            return response;
        } catch (Exception ex) {
            log.error("Failed to create shipment for order {}: {}", request.getExternalOrderId(), ex.getMessage());
            markOrderFailed(order.getId(), ex.getMessage());
            throw new ProviderException(providerCode, ex.getMessage());
        }
    }

    private ProviderCode getProviderCode(CreateOrderRequest request) {
        if (ObjectUtils.isEmpty(request.getPreferredProvider()))
            return ProviderCode.BLUEDART;
        return request.getPreferredProvider();
    }

    @Transactional
    private Order saveOrderTransactional(Order order) {
        return orderRepository.save(order);
    }

    @Transactional
    private void saveShipmentAndMarkOrder(UUID orderId, Shipment shipment) {
        Order managed = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        shipment.setOrder(managed);
        shipmentRepository.save(shipment);
        managed.setStatus(DeliveryStatus.ORDER_CONFIRMED);
        orderRepository.save(managed);
    }

    @Transactional
    private void markOrderFailed(UUID orderId, String reason) {
        orderRepository.findById(orderId).ifPresent(o -> {
            o.setStatus(DeliveryStatus.DELIVERY_FAILED);
            orderRepository.save(o);
        });
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