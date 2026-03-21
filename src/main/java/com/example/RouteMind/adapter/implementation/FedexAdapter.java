package com.example.RouteMind.adapter.implementation;

import com.example.RouteMind.adapter.DeliveryProviderAdapter;
import com.example.RouteMind.client.fedex.FedexApiClient;
import com.example.RouteMind.client.fedex.dto.FedexOAuthResponse;
import com.example.RouteMind.client.fedex.dto.serviceability.FedexRateRequest;
import com.example.RouteMind.client.fedex.dto.serviceability.FedexRateResponse;
import com.example.RouteMind.client.fedex.dto.shipment.FedexShipmentRequest;
import com.example.RouteMind.client.fedex.dto.shipment.FedexShipmentResponse;
import com.example.RouteMind.client.fedex.dto.tracking.FedexTrackingRequest;
import com.example.RouteMind.client.fedex.dto.tracking.FedexTrackingResponse;
import com.example.RouteMind.config.FedexProperties;
import com.example.RouteMind.dto.Request.CreateOrderRequest;
import com.example.RouteMind.dto.Response.OrderResponse;
import com.example.RouteMind.dto.Response.TrackingResponse;
import com.example.RouteMind.dto.Response.TrackingEventDto;
import com.example.RouteMind.enums.DeliveryStatus;
import com.example.RouteMind.enums.ProviderCode;
import com.example.RouteMind.enums.ServiceType;
import com.example.RouteMind.enums.PaymentMode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import retrofit2.Response;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


/**
 * FedEx API integration adapter.
 * Implements real FedEx API calls for serviceability, rate calculation, and tracking.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class FedexAdapter implements DeliveryProviderAdapter {


        // Inject FedEx API client and properties
        private final FedexApiClient fedexApiClient;
        private final FedexProperties fedexProperties;

         // Simple in-memory token cache (for production, consider Redis or database)
        private volatile String accessToken;
        private volatile Instant tokenExpiry;

        @Override
        public ProviderCode getProviderCode() {
            return ProviderCode.FEDEX;
        }

        /**
         * Check if FedEx can deliver from pickup pincode to delivery pincode.
         * Uses Rate API - if we get a valid rate, it means serviceable.
         */
        @Override
        public boolean checkServiceability(String pickupPincode, String deliveryPincode) {
            log.info("FedEx: Checking serviceability {} -> {}", pickupPincode, deliveryPincode);

            try {
                // Try to get a rate with default weight (0.5 kg)
                BigDecimal rate = fetchRateFromFedEx(pickupPincode, deliveryPincode, 0.5);

                // If we got a positive rate, FedEx can deliver
                if (rate != null && rate.compareTo(BigDecimal.ZERO) > 0) {
                    log.info("FedEx serviceable: {} -> {}", pickupPincode, deliveryPincode);
                    return true;
                }

                log.warn("FedEx not serviceable: {} -> {}", pickupPincode, deliveryPincode);
                return false;
            } catch (Exception e) {
                log.error("FedEx serviceability check failed", e);
                return false;
            }
        }

        /**
         * Calculate shipping rate using FedEx Rate API.
         */
        @Override
        public BigDecimal calculateRate(String pickupPincode, String deliveryPincode, Double weightKg) {
            log.info("FedEx: Calculating rate for {} kg ({} -> {})",
                    weightKg, pickupPincode, deliveryPincode);

            try {
                BigDecimal rate = fetchRateFromFedEx(pickupPincode, deliveryPincode, weightKg);

                if (rate != null) {
                    log.info("FedEx rate calculated: {} INR", rate);
                    return rate;
                }
            } catch (Exception e) {
                log.error("FedEx rate calculation failed", e);
            }

            // Fallback to default rate if API call fails
            log.warn("FedEx rate not available, using fallback rate");
            return new BigDecimal("80.00");
        }

        /**
         * Create shipment using FedEx Ship API.
         * Sends minimal mandatory fields to FedEx shipment creation endpoint.
         */
        @Override
        public OrderResponse createShipment(CreateOrderRequest request) {
            log.info("FedEx: Creating shipment for order {}", request.getExternalOrderId());

            try {
                // Validate input
                if (request == null || request.getPickupAddress() == null
                        || request.getDeliveryAddress() == null
                        || request.getPackageDetails() == null) {
                    log.error("Missing required fields in CreateOrderRequest");
                    return buildErrorResponse(request, "Missing required order information");
                }

                // Step 1: Get valid access token
                String token = getValidAccessToken();
                String authHeader = "Bearer " + token;

                // Step 2: Build FedEx shipment request
                FedexShipmentRequest shipmentRequest = buildShipmentRequest(request);
                log.info("FedEx Ship API Request prepared for order: {}", request.getExternalOrderId());

                // Step 3: Call FedEx Ship API
                log.debug("Calling FedEx Ship API for order {}", request.getExternalOrderId());
                Response<FedexShipmentResponse> response =
                        fedexApiClient.createShipment(authHeader, shipmentRequest).execute();
                log.info("FedEx Ship API Response received for order: {} (HTTP {})",
                        request.getExternalOrderId(), response.code());
                if (!response.isSuccessful()) {
                    String errorBody = response.errorBody() != null ?
                            response.errorBody().string() : "no error body";
                    log.error("FedEx Ship API error for order {}: HTTP {} - {}",
                            request.getExternalOrderId(), response.code(), errorBody);
                    return buildErrorResponse(request, "FedEx Ship API failed: HTTP " + response.code());
                }

                FedexShipmentResponse body = response.body();
                if (body == null) {
                    log.error("FedEx Ship API returned null response for order {}",
                            request.getExternalOrderId());
                    return buildErrorResponse(request, "FedEx Ship API returned no response");
                }

                // Step 5: Check for API errors/warnings in response
                if (body.getOutput() != null && body.getOutput().getAlerts() != null && !body.getOutput().getAlerts().isEmpty()) {
                    List<String> errorMessages = body.getOutput().getAlerts().stream()
                            .filter(alert -> alert.getCode() != null && !alert.getCode().toUpperCase().contains("SUCCESS"))
                            .map(FedexShipmentResponse.Alert::getMessage)
                            .collect(Collectors.toList());

                    if (!errorMessages.isEmpty()) {
                        log.warn("FedEx Ship API returned alerts for order {}: {}", request.getExternalOrderId(), String.join("; ", errorMessages));
                        // Do not return an error immediately, as some alerts are warnings and the shipment might still be created.
                    }
                }

                // Step 6: Extract tracking number from response
                String trackingNumber = null;
                if (body.getOutput() != null && body.getOutput().getTransactionShipments() != null && !body.getOutput().getTransactionShipments().isEmpty()) {
                    FedexShipmentResponse.TransactionShipment shipment = body.getOutput().getTransactionShipments().get(0);

                    // Prefer master tracking number
                    trackingNumber = shipment.getMasterTrackingNumber();

                    // Fallback to the tracking number from the first piece response
                    if (trackingNumber == null && shipment.getPieceResponses() != null && !shipment.getPieceResponses().isEmpty()) {
                        trackingNumber = shipment.getPieceResponses().get(0).getTrackingNumber();
                    }
                }

                // If tracking number is still not found, it's an error
                if (trackingNumber == null) {
                    log.error("FedEx Ship API: tracking number not found in response for order {}",
                            request.getExternalOrderId());
                    String errorMessage = "FedEx did not return a tracking number.";
                    if (body.getOutput() != null && body.getOutput().getAlerts() != null && !body.getOutput().getAlerts().isEmpty()) {
                        errorMessage = body.getOutput().getAlerts().get(0).getMessage();
                    }
                    return buildErrorResponse(request, errorMessage);
                }

                log.info("FedEx shipment created successfully. Order: {}, Tracking: {}",
                        request.getExternalOrderId(), trackingNumber);

                // Step 7: Build and return success response
                return OrderResponse.builder()
                        .externalOrderId(request.getExternalOrderId())
                        .trackingId(trackingNumber)
                        .provider(ProviderCode.FEDEX)
                        .status(DeliveryStatus.ORDER_CONFIRMED)
                        .message("Shipment created successfully with FedEx")
                        .build();

            } catch (Exception e) {
                log.error("Error creating FedEx shipment for order {}",
                        request != null ? request.getExternalOrderId() : "unknown", e);
                return buildErrorResponse(request, "Exception: " + e.getMessage());
            }
        }

        /**
         * Build FedEx Shipment Request from CreateOrderRequest with minimal mandatory fields.
         */
        private FedexShipmentRequest buildShipmentRequest(CreateOrderRequest request) {
            // Get service type (default to STANDARD_OVERNIGHT)
            String fedexServiceType = mapServiceTypeToFedex(request.getServiceType());

            // Map payment mode to FedEx payment type
            String paymentType = mapPaymentModeToFedex(request.getPaymentMode());

            // Build shipper party
            FedexShipmentRequest.Party shipper = FedexShipmentRequest.Party.builder()
                    .contact(FedexShipmentRequest.Contact.builder()
                            .personName(request.getPickupAddress().getName())
                            .phoneNumber(request.getPickupAddress().getPhone())
                            .build())
                    .address(FedexShipmentRequest.Address.builder()
                            .streetLines(List.of(request.getPickupAddress().getAddress()))
                            .city(request.getPickupAddress().getCity())
                            .stateOrProvinceCode(request.getPickupAddress().getStateCode())
                            .postalCode(request.getPickupAddress().getPincode())
                            .countryCode(request.getPickupAddress().getCountryCode() != null ?
                                    request.getPickupAddress().getCountryCode() : "IN")
                            .build())
                    .build();

            // Build recipient party
            FedexShipmentRequest.Party recipient = FedexShipmentRequest.Party.builder()
                    .contact(FedexShipmentRequest.Contact.builder()
                            .personName(request.getDeliveryAddress().getName())
                            .phoneNumber(request.getDeliveryAddress().getPhone())
                            .build())
                    .address(FedexShipmentRequest.Address.builder()
                            .streetLines(List.of(request.getDeliveryAddress().getAddress()))
                            .city(request.getDeliveryAddress().getCity())
                            .stateOrProvinceCode(request.getDeliveryAddress().getStateCode())
                            .postalCode(request.getDeliveryAddress().getPincode())
                            .countryCode(request.getDeliveryAddress().getCountryCode() != null ?
                                    request.getDeliveryAddress().getCountryCode() : "IN")
                            .build())
                    .build();

            // Build shipping charges payment
            FedexShipmentRequest.ShippingChargesPayment payment =
                    FedexShipmentRequest.ShippingChargesPayment.builder()
                    .paymentType(paymentType)
                    .payor(FedexShipmentRequest.Payor.builder()
                            .responsibleParty(FedexShipmentRequest.ResponsibleParty.builder()
                                    .accountNumber(FedexShipmentRequest.ValueKey.builder()
                                            .value(fedexProperties.getAccountNumber())
                                            .key("")
                                            .build())
                                    .build())
                            .build())
                    .build();

            // Build package line item with weight
            Double weightInKg = request.getPackageDetails().getWeightInKg();
            if (weightInKg == null || weightInKg <= 0) {
                weightInKg = 1.0; // Default weight in KG
            }
            log.debug("Weight converted to KG: {} KG (original: {} {})",
                    weightInKg,
                    request.getPackageDetails().getWeight() != null ?
                        request.getPackageDetails().getWeight() : request.getPackageDetails().getWeightKg(),
                    request.getPackageDetails().getWeightUnit() != null ?
                        request.getPackageDetails().getWeightUnit().getCode() : "KG");

            FedexShipmentRequest.PackageLineItem packageItem =
                    FedexShipmentRequest.PackageLineItem.builder()
                    .weight(FedexShipmentRequest.Weight.builder()
                            .units("KG")
                            .value(String.format("%.2f", weightInKg))
                            .build())
                    .build();

            // Build requested shipment
            FedexShipmentRequest.RequestedShipment requestedShipment =
                    FedexShipmentRequest.RequestedShipment.builder()
                    .shipper(shipper)
                    .recipients(List.of(recipient))
                    .serviceType(fedexServiceType)
                    .packagingType("YOUR_PACKAGING")
                    .pickupType("USE_SCHEDULED_PICKUP")
                    .shippingChargesPayment(payment)
                    .labelSpecification(new FedexShipmentRequest.LabelSpecification())
                    .requestedPackageLineItems(List.of(packageItem))
                    .build();

            // Build and return complete shipment request
            return FedexShipmentRequest.builder()
                    .requestedShipment(requestedShipment)
                    .accountNumber(FedexShipmentRequest.AccountNumber.builder()
                            .value(fedexProperties.getAccountNumber())
                            .build())
                    .build();
        }

        /**
         * Map internal ServiceType enum to FedEx service type string.
         */
        private String mapServiceTypeToFedex(ServiceType serviceType) {
            if (serviceType == null) {
                return "STANDARD_OVERNIGHT";
            }

            return switch (serviceType) {
                case SAME_DAY -> "SAME_DAY";
                case EXPRESS -> "PRIORITY_OVERNIGHT";
                case ECONOMY -> "SMART_POST";
                case STANDARD -> "STANDARD_OVERNIGHT";
                default -> "STANDARD_OVERNIGHT";
            };
        }

        /**
         * Map PaymentMode enum to FedEx payment type.
         */
        private String mapPaymentModeToFedex(PaymentMode paymentMode) {
            if (paymentMode == null) {
                return "SENDER";
            }

            return switch (paymentMode) {
                case PREPAID -> "SENDER";
                case COD -> "RECIPIENT"; // Collect on delivery - recipient pays
                case POSTPAID -> "SENDER"; // Postpaid also means sender bears cost
                default -> "SENDER";
            };
        }

        /**
         * Build error response for failed shipment creation.
         */
        private OrderResponse buildErrorResponse(CreateOrderRequest request, String errorMessage) {
            return OrderResponse.builder()
                    .externalOrderId(request != null ? request.getExternalOrderId() : "unknown")
                    .provider(ProviderCode.FEDEX)
                    .status(DeliveryStatus.PENDING)
                    .message(errorMessage)
                    .build();
        }

        /**
         * Cancel shipment - TODO: Implement cancel API later
         */
        @Override
        public boolean cancelShipment(String trackingId) {
            log.info("FedEx: Cancelling shipment {}", trackingId);
            // TODO: Implement cancel API if available
            return false;
        }

        /**
         * Track shipment using FedEx Track API.
         * Extracts current status, location, estimated delivery date, and full event history.
         */
        @Override
        public TrackingResponse trackShipment(String trackingId) {
            log.info("FedEx: Tracking shipment {}", trackingId);

            try {
                // 1) Get valid OAuth token
                String token = getValidAccessToken();
                String authHeader = "Bearer " + token;

                // 2) Build FedEx tracking request
                FedexTrackingRequest request = FedexTrackingRequest.builder()
                        .includeDetailedScans(true)
                        .trackingInfo(List.of(
                                FedexTrackingRequest.TrackingInfo.builder()
                                        .trackingNumberInfo(
                                                FedexTrackingRequest.TrackingNumberInfo.builder()
                                                        .trackingNumber(trackingId)
                                                        .build()
                                        )
                                        .build()
                        ))
                        .build();

                // 3) Call FedEx Track API
                Response<FedexTrackingResponse> response =
                        fedexApiClient.trackShipment(authHeader, request).execute();

                if (!response.isSuccessful() || response.body() == null) {
                    String errorBody = response.errorBody() != null
                            ? response.errorBody().string()
                            : "no error body";
                    log.error("FedEx Track API error: HTTP {} - {}", response.code(), errorBody);

                    // Fallback: return basic IN_TRANSIT status
                    return TrackingResponse.builder()
                            .trackingId(trackingId)
                            .provider(ProviderCode.FEDEX)
                            .currentStatus(DeliveryStatus.IN_TRANSIT)
                            .events(new ArrayList<>())
                            .build();
                }

                FedexTrackingResponse body = response.body();
                if (body.getOutput() == null ||
                        body.getOutput().getCompleteTrackResults() == null ||
                        body.getOutput().getCompleteTrackResults().isEmpty()) {
                    log.warn("FedEx Track API: no track results for {}", trackingId);
                    return TrackingResponse.builder()
                            .trackingId(trackingId)
                            .provider(ProviderCode.FEDEX)
                            .currentStatus(DeliveryStatus.IN_TRANSIT)
                            .events(new ArrayList<>())
                            .build();
                }

                FedexTrackingResponse.CompleteTrackResult result =
                        body.getOutput().getCompleteTrackResults().get(0);

                if (result.getTrackResults() == null || result.getTrackResults().isEmpty()) {
                    log.warn("FedEx Track API: empty trackResults for {}", trackingId);
                    return TrackingResponse.builder()
                            .trackingId(trackingId)
                            .provider(ProviderCode.FEDEX)
                            .currentStatus(DeliveryStatus.IN_TRANSIT)
                            .events(new ArrayList<>())
                            .build();
                }

                FedexTrackingResponse.TrackResult trackResult = result.getTrackResults().get(0);
                FedexTrackingResponse.StatusDetail statusDetail = trackResult.getLatestStatusDetail();

                // 4) Map FedEx status code to your DeliveryStatus
                DeliveryStatus mappedStatus = mapFedexStatus(statusDetail != null ? statusDetail.getCode() : null);

                // 5) Build location string
                String location = "";
                if (statusDetail != null && statusDetail.getScanLocation() != null) {
                    location = buildLocationString(statusDetail.getScanLocation());
                }

                // 6) Extract estimated delivery date
                LocalDate estimatedDeliveryDate = extractEstimatedDeliveryDate(trackResult);

                // 7) Extract and map all scan events
                List<TrackingEventDto> events = extractScanEvents(trackResult);

                // 8) Build and return your TrackingResponse
                return TrackingResponse.builder()
                        .trackingId(trackingId)
                        .provider(ProviderCode.FEDEX)
                        .currentStatus(mappedStatus)
                        .currentLocation(location)
                        .estimatedDeliveryDate(estimatedDeliveryDate)
                        .events(events)
                        .build();

            } catch (Exception e) {
                log.error("Error tracking FedEx shipment {}", trackingId, e);
                return TrackingResponse.builder()
                        .trackingId(trackingId)
                        .provider(ProviderCode.FEDEX)
                        .currentStatus(DeliveryStatus.IN_TRANSIT)
                        .events(new ArrayList<>())
                        .build();
            }
        }

        // ===================== PRIVATE HELPER METHODS =====================

        /**
         * Main helper: Calls FedEx Rate API and returns the first rate found.
         * This is the core method that does the actual API call.
         */
        private BigDecimal fetchRateFromFedEx(String pickupPincode, String deliveryPincode, Double weightKg) throws Exception {
            // Step 1: Get valid access token
            String token = getValidAccessToken();
            String authHeader = "Bearer " + token;

            // Step 2: Build the rate request
            FedexRateRequest request = buildRateRequest(pickupPincode, deliveryPincode, weightKg);

            // Step 3: Call FedEx Rate API
            log.debug("Calling FedEx Rate API...");
            Response<FedexRateResponse> response = fedexApiClient.getRates(authHeader, request).execute();

            // Step 4: Check if request was successful
            if (!response.isSuccessful()) {
                String errorBody = response.errorBody() != null ?
                        response.errorBody().string() : "no error body";
                log.error("FedEx Rate API error: HTTP {} - {}", response.code(), errorBody);
                throw new RuntimeException("FedEx Rate API failed: " + response.code());
            }

            // Step 5: Parse response
            FedexRateResponse body = response.body();
            if (body == null || body.getOutput() == null ||
                    body.getOutput().getRateReplyDetails() == null ||
                    body.getOutput().getRateReplyDetails().isEmpty()) {
                log.warn("FedEx Rate API returned no rate details");
                return null;
            }

            // Step 6: Extract first rate
            FedexRateResponse.RateReplyDetail firstDetail =
                    body.getOutput().getRateReplyDetails().get(0);

            if (firstDetail.getRatedShipmentDetails() == null ||
                    firstDetail.getRatedShipmentDetails().isEmpty()) {
                log.warn("FedEx Rate API: no ratedShipmentDetails");
                return null;
            }

            BigDecimal amount = firstDetail.getRatedShipmentDetails().get(0).getTotalNetCharge();
            if (amount == null) {
                log.warn("FedEx Rate API: totalNetCharge missing");
                return null;
            }

            log.info("FedEx rate retrieved: {}", amount);
            return amount;
        }

        /**
         * Build FedEx Rate API request from pincodes and weight.
         */
        private FedexRateRequest buildRateRequest(String pickupPincode, String deliveryPincode, Double weightKg) {
            // Default weight if not provided
            if (weightKg == null || weightKg <= 0) {
                weightKg = 0.5;
            }

            // Build origin address (shipper)
            FedexRateRequest.Address originAddress = FedexRateRequest.Address.builder()
                    .postalCode(pickupPincode)
                    .countryCode("IN")
                    .build();

            // Build destination address (recipient)
            FedexRateRequest.Address destAddress = FedexRateRequest.Address.builder()
                    .postalCode(deliveryPincode)
                    .countryCode("IN")
                    .build();

            // Build package weight
            FedexRateRequest.PackageLineItem pkg = FedexRateRequest.PackageLineItem.builder()
                    .weight(FedexRateRequest.Weight.builder()
                            .units("KG")
                            .value(weightKg)
                            .build())
                    .build();

            // Build shipment request
            FedexRateRequest.RequestedShipment shipment = FedexRateRequest.RequestedShipment.builder()
                    .shipper(FedexRateRequest.Party.builder()
                            .address(originAddress)
                            .build())
                    .recipient(FedexRateRequest.Party.builder()
                            .address(destAddress)
                            .build())
                    .pickupType("DROPOFF_AT_FEDEX_LOCATION")
                    .rateRequestType(List.of("ACCOUNT", "LIST"))
                    .packagingType("YOUR_PACKAGING")
                    .requestedPackageLineItems(List.of(pkg))
                    .build();

            // Build complete request
            return FedexRateRequest.builder()
                    .accountNumber(FedexRateRequest.AccountNumber.builder()
                            .value(fedexProperties.getAccountNumber())
                            .build())
                    .requestedShipment(shipment)
                    .build();
        }

        /**
         * Get valid access token: reuse cached token if still valid, otherwise fetch new one.
         * Thread-safe using synchronized.
         */
        private synchronized String getValidAccessToken() throws Exception {
            // Check if we have a cached token that's still valid
            if (accessToken != null && tokenExpiry != null) {
                // Keep 60 second safety margin before expiry
                if (Instant.now().isBefore(tokenExpiry.minusSeconds(60))) {
                    log.debug("Using cached FedEx token");
                    return accessToken;
                }
            }

            // Need to fetch new token
            log.info("Fetching new FedEx OAuth token...");
            Response<FedexOAuthResponse> response = fedexApiClient
                    .getAccessToken(
                            "client_credentials",
                            fedexProperties.getClientId(),
                            fedexProperties.getClientSecret()
                    )
                    .execute();

            // Check if OAuth call was successful
            if (!response.isSuccessful() || response.body() == null ||
                    response.body().getAccessToken() == null) {
                String errorBody = response.errorBody() != null ?
                        response.errorBody().string() : "no error body";
                throw new RuntimeException("FedEx OAuth failed: HTTP " +
                        response.code() + " - " + errorBody);
            }

            // Cache the token
            FedexOAuthResponse body = response.body();
            this.accessToken = body.getAccessToken();
            this.tokenExpiry = Instant.now().plusSeconds(body.getExpiresIn());

            log.info("FedEx token obtained, expires in {} seconds", body.getExpiresIn());
            return this.accessToken;
        }

        /**
         * Extract all scan events from FedEx response and convert to TrackingEventDto list.
         * This method processes both scanEvents array and latestStatusDetail.
         */
        private List<TrackingEventDto> extractScanEvents(FedexTrackingResponse.TrackResult trackResult) {
            List<TrackingEventDto> events = new ArrayList<>();

            // Extract from scanEvents array (detailed scans)
            if (trackResult.getScanEvents() != null && !trackResult.getScanEvents().isEmpty()) {
                for (FedexTrackingResponse.ScanEvent scanEvent : trackResult.getScanEvents()) {
                    // Map FedEx status code to our DeliveryStatus
                    DeliveryStatus status = mapFedexStatus(scanEvent.getEventType());

                    // Build location string
                    String location = "";
                    if (scanEvent.getScanLocation() != null) {
                        location = buildLocationString(scanEvent.getScanLocation());
                    }

                    // Parse timestamp
                    LocalDateTime timestamp = parseFedexTimestamp(scanEvent.getDate());

                    // Get description (prefer eventDescription, fallback to eventType)
                    String description = scanEvent.getEventDescription() != null && !scanEvent.getEventDescription().isEmpty()
                            ? scanEvent.getEventDescription()
                            : (scanEvent.getEventType() != null ? scanEvent.getEventType() : "Status update");

                    events.add(TrackingEventDto.builder()
                            .status(status)
                            .description(description)
                            .location(location)
                            .timestamp(timestamp)
                            .build());
                }
            }

            // Also include latest status if not already in scanEvents
            if (trackResult.getLatestStatusDetail() != null) {
                FedexTrackingResponse.StatusDetail latest = trackResult.getLatestStatusDetail();
                LocalDateTime latestTimestamp = null; // StatusDetail doesn't have timestamp

                // Check if this event is already in the list (avoid duplicates)
                boolean alreadyExists = events.stream()
                        .anyMatch(e -> e.getTimestamp() != null && latestTimestamp != null
                                && e.getTimestamp().equals(latestTimestamp)
                                && e.getStatus().equals(mapFedexStatus(latest.getCode())));

                if (!alreadyExists) {
                    String latestLocation = "";
                    if (latest.getScanLocation() != null) {
                        latestLocation = buildLocationString(latest.getScanLocation());
                    }

                    String latestDescription = latest.getDescription() != null && !latest.getDescription().isEmpty()
                            ? latest.getDescription()
                            : "Status update"; // StatusDetail only has description

                    events.add(TrackingEventDto.builder()
                            .status(mapFedexStatus(latest.getCode()))
                            .description(latestDescription)
                            .location(latestLocation)
                            .timestamp(latestTimestamp)
                            .build());
                }
            }

            // Sort by timestamp (oldest first, most recent last)
            events.sort(Comparator.comparing(
                    TrackingEventDto::getTimestamp,
                    Comparator.nullsLast(Comparator.naturalOrder())
            ));

            log.debug("Extracted {} tracking events from FedEx response", events.size());
            return events;
        }

        /**
         * Extract estimated delivery date from FedEx response.
         * Looks for ESTIMATED_DELIVERY in dateDetail array.
         */
        private LocalDate extractEstimatedDeliveryDate(FedexTrackingResponse.TrackResult trackResult) {
            if (trackResult.getServiceCommitMessage() != null
                    && "ESTIMATED_DELIVERY_DATE_UNAVAILABLE".equals(trackResult.getServiceCommitMessage().getType())) {
                return null; // No date available
            }

            // Try to get from estimatedDeliveryTimeWindow
            if (trackResult.getEstimatedDeliveryTimeWindow() != null && trackResult.getEstimatedDeliveryTimeWindow().getWindow() != null) {
                String begins = trackResult.getEstimatedDeliveryTimeWindow().getWindow().getBegins();
                if (begins != null) {
                    try {
                        return parseFedexTimestamp(begins).toLocalDate();
                    } catch (Exception e) {
                        log.warn("Failed to parse estimated delivery date from time window: {}", begins, e);
                    }
                }
            }

            // Fallback to dateAndTimes array
            if (trackResult.getDateAndTimes() != null && !trackResult.getDateAndTimes().isEmpty()) {
                for (FedexTrackingResponse.DateAndTime dateDetail : trackResult.getDateAndTimes()) {
                    if ("ESTIMATED_DELIVERY".equals(dateDetail.getType()) && dateDetail.getDateTime() != null) {
                        try {
                            return parseFedexTimestamp(dateDetail.getDateTime()).toLocalDate();
                        } catch (Exception e) {
                            log.warn("Failed to parse estimated delivery date from dateAndTimes: {}", dateDetail.getDateTime(), e);
                        }
                    }
                }
            }
            return null;
        }

        /**
         * Parse FedEx timestamp string to LocalDateTime.
         * FedEx typically returns ISO 8601 format: "2024-01-15T10:30:00" or "2024-01-15T10:30:00Z"
         * Handles various formats and provides fallback to current time.
         */
        private LocalDateTime parseFedexTimestamp(String timestampStr) {
            if (timestampStr == null || timestampStr.isEmpty()) {
                return LocalDateTime.now(); // Fallback to current time
            }

            try {
                // Remove timezone suffix if present (Z, +05:30, etc.)
                String cleaned = timestampStr.replaceAll("[Zz]|[+-]\\d{2}:\\d{2}$", "").trim();

                // Try parsing ISO format with time (e.g., "2024-01-15T10:30:00")
                if (cleaned.length() >= 19) {
                    return LocalDateTime.parse(cleaned.substring(0, 19),
                            DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                }
                // Try parsing date only (e.g., "2024-01-15")
                else if (cleaned.length() >= 10) {
                    return LocalDate.parse(cleaned.substring(0, 10))
                            .atStartOfDay();
                }
            } catch (Exception e) {
                log.warn("Failed to parse FedEx timestamp: {}. Using current time.", timestampStr, e);
            }

            return LocalDateTime.now(); // Fallback to current time
        }

        /**
         * Map FedEx status codes to your DeliveryStatus enum.
         */
        private DeliveryStatus mapFedexStatus(String fedexCode) {
            if (fedexCode == null) {
                return DeliveryStatus.IN_TRANSIT;
            }

            String code = fedexCode.toUpperCase();
            return switch (code) {
                case "OC", "AP", "AA" -> DeliveryStatus.ORDER_CONFIRMED;
                case "PU", "IP"      -> DeliveryStatus.PICKED_UP;
                case "IT", "OD"      -> DeliveryStatus.IN_TRANSIT;
                case "ODD", "OF"     -> DeliveryStatus.OUT_FOR_DELIVERY;
                case "DL"            -> DeliveryStatus.DELIVERY_SUCCESS;
                case "CA"            -> DeliveryStatus.CANCELLED;
                case "RS", "SE"      -> DeliveryStatus.RTO_INITIATED;
                default              -> DeliveryStatus.IN_TRANSIT;
            };
        }

        /**
         * Build a human-readable location string from FedEx scan location.
         */
        private String buildLocationString(FedexTrackingResponse.ScanLocation scanLocation) {
            String city = scanLocation.getCity() != null ? scanLocation.getCity() : "";
            String state = scanLocation.getStateOrProvinceCode() != null ? scanLocation.getStateOrProvinceCode() : "";
            if (!city.isEmpty() && !state.isEmpty()) {
                return city + ", " + state;
            }
            return city + state;
        }
    }
