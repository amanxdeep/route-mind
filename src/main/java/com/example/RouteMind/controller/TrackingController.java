package com.example.RouteMind.controller;

import com.example.RouteMind.dto.Response.GenericResponse;
import com.example.RouteMind.dto.Response.TrackingResponse;
import com.example.RouteMind.constants.ApiConstants;
import com.example.RouteMind.service.TrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
/**
 * REST API for tracking shipments.
 * Base path: /api/v1/tracking
 */
@RestController
@RequestMapping(ApiConstants.API_V1 + ApiConstants.TRACKING)
@RequiredArgsConstructor
@Slf4j

public class TrackingController {


    private final TrackingService trackingService;
    /**
     * GET /api/v1/tracking/{trackingId}
     * Track shipment by tracking ID.
     */
    @GetMapping("/{trackingId}")
    public ResponseEntity<GenericResponse<TrackingResponse>> trackShipment(
            @PathVariable String trackingId) {

        log.info("Tracking request: {}", trackingId);

        TrackingResponse response = trackingService.trackShipment(trackingId);

        return ResponseEntity.ok(GenericResponse.success(response));
    }
}
