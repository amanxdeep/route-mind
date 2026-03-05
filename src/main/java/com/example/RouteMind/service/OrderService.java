package com.example.RouteMind.service;

import com.example.RouteMind.dto.Request.CreateOrderRequest;
import com.example.RouteMind.dto.Response.OrderResponse;
import com.example.RouteMind.adapter.DeliveryProviderAdapter;
import com.example.RouteMind.entity.Order;
import com.example.RouteMind.entity.Shipment;
import com.example.RouteMind.enums.DeliveryStatus;
import com.example.RouteMind.enums.ProviderCode;
import com.example.RouteMind.factory.ProviderFactory;
import com.example.RouteMind.mapper.AppModelMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.RouteMind.exception.ProviderException;
import org.springframework.util.ObjectUtils;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final ProviderFactory providerFactory;
    private final AppModelMapper appModelMapper;
    private final OrderDataAccessService orderDataAccessService;

    public OrderResponse createOrder(CreateOrderRequest request) {
        log.info("Creating order request: {}", request);

        validateOrderRequest(request);

        Order order = appModelMapper.createOrderRequestToOrder(request);
        Order orderEntity = orderDataAccessService.saveOrder(order);

        ProviderCode providerCode = getProviderCode(request);

        try {
            DeliveryProviderAdapter adapter = providerFactory.getAdapter(providerCode);
            OrderResponse response = adapter.createShipment(request);

            Shipment shipment = getShipment(request, response, providerCode);

            orderDataAccessService.saveShipmentAndMarkOrder(orderEntity.getId(), shipment);

            response.setOrderId(orderEntity.getId())
                    .setExternalOrderId(orderEntity.getExternalOrderId());

            return response;
        } catch (Exception ex) {
            log.error("Failed to create shipment for order {}", request.getExternalOrderId(), ex);
            orderDataAccessService.markOrderFailed(orderEntity.getId(), ex.getMessage());
            throw new ProviderException(providerCode, ex.getMessage());
        }
    }

    private static Shipment getShipment(CreateOrderRequest request, OrderResponse response, ProviderCode providerCode) {
        return Shipment.builder()
                .trackingId(response.getTrackingId())
                .providerCode(providerCode)
                .serviceType(request.getServiceType())
                .currentStatus(DeliveryStatus.ORDER_CONFIRMED)
                .build();
    }

    private void validateOrderRequest(CreateOrderRequest request) {
        if (orderDataAccessService.getOrderByExternalId(request.getExternalOrderId()).isPresent()) {
            throw new RuntimeException("Order already exists: " + request.getExternalOrderId());
        }
    }

    private ProviderCode getProviderCode(CreateOrderRequest request) {
        if (ObjectUtils.isEmpty(request.getPreferredProvider()))
            return ProviderCode.BLUEDART;

        return request.getPreferredProvider();
    }

}