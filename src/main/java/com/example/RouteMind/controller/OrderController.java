package com.example.RouteMind.controller;

import com.example.RouteMind.dto.Request.CreateOrderRequest;
import com.example.RouteMind.dto.Response.GenericResponse;
import com.example.RouteMind.dto.Response.OrderResponse;
import com.example.RouteMind.constants.ApiConstants;
import com.example.RouteMind.entity.Order;
import com.example.RouteMind.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
/**
 * REST API for order operations.
 * Base path: /api/v1/orders
 */
@RestController
@RequestMapping(ApiConstants.API_V1 + ApiConstants.ORDERS)
@RequiredArgsConstructor
@Slf4j

public class OrderController {

    private final OrderService orderService;

    /**
     * POST /api/v1/orders
     * Create a new order.
     */
    @PostMapping
    public ResponseEntity<GenericResponse<OrderResponse>> createOrder(
            @RequestBody CreateOrderRequest request) {

        log.info("Creating order: {}", request.getExternalOrderId());

        OrderResponse response = orderService.createOrder(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(GenericResponse.success(response));
    }

    /**
     * GET /api/v1/orders/{id}
     * Get order by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<GenericResponse<Order>> getOrderById(@PathVariable UUID id) {

        return orderService.getOrderById(id)
                .map(order -> ResponseEntity.ok(GenericResponse.success(order)))
                .orElse(ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(GenericResponse.failure("NOT_FOUND", "Order not found")));
    }

    /**
     * GET /api/v1/orders/external/{externalOrderId}
     * Get order by external ID (web app's order ID).
     */
    @GetMapping("/external/{externalOrderId}")
    public ResponseEntity<GenericResponse<Order>> getOrderByExternalId(
            @PathVariable String externalOrderId) {

        return orderService.getOrderByExternalId(externalOrderId)
                .map(order -> ResponseEntity.ok(GenericResponse.success(order)))
                .orElse(ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(GenericResponse.failure("NOT_FOUND", "Order not found")));
    }
}
