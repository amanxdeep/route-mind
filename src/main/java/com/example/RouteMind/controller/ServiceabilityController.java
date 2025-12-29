package com.example.RouteMind.controller;

import com.example.RouteMind.Dto.Request.ServiceabilityRequest;
import com.example.RouteMind.Dto.Response.GenericResponse;
import com.example.RouteMind.Dto.Response.ServiceabilityResponse;
import com.example.RouteMind.constants.ApiConstants;
import com.example.RouteMind.service.ServiceabilityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
/**
 * REST API for checking delivery serviceability.
 * Base path: /api/v1/serviceability
 */
@RestController
@RequestMapping(ApiConstants.API_V1 + ApiConstants.SERVICEABILITY)
@RequiredArgsConstructor
@Slf4j
public class ServiceabilityController {
    private final ServiceabilityService serviceabilityService;

    /**
     * POST /api/v1/serviceability
     * Check if delivery is possible and get available options.
     */
    @PostMapping
    public ResponseEntity<GenericResponse<ServiceabilityResponse>> checkServiceability(
            @RequestBody ServiceabilityRequest request) {

        log.info("Serviceability check: {} -> {}",
                request.getPickupPincode(), request.getDeliveryPincode());

        ServiceabilityResponse response = serviceabilityService.checkServiceability(request);

        return ResponseEntity.ok(GenericResponse.success(response));
    }
}
