package com.example.RouteMind.service;

import com.example.RouteMind.dto.Request.ServiceabilityRequest;
import com.example.RouteMind.dto.Response.DeliveryOptionDto;
import com.example.RouteMind.dto.Response.ServiceabilityResponse;
import com.example.RouteMind.adapter.DeliveryProviderAdapter;
import com.example.RouteMind.entity.Serviceability;
import com.example.RouteMind.enums.ProviderTag;
import com.example.RouteMind.factory.ProviderFactory;
import com.example.RouteMind.repository.RateCardRepository;
import com.example.RouteMind.repository.ServiceabilityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
/**
 * Checks if delivery is possible and returns available options.
 * Called BEFORE order to show customer what's available.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceabilityService {
    private final ServiceabilityRepository serviceabilityRepository;
    private final RateCardRepository rateCardRepository;
    private final ProviderFactory providerFactory;
    /**
     * Check serviceability for a route.
     * Returns all available providers with their cost & ETA.
     */
    public ServiceabilityResponse checkServiceability(ServiceabilityRequest request) {
        log.info("Checking serviceability: {} -> {}",
                request.getPickupPincode(), request.getDeliveryPincode());
        // 1. Find all providers serving delivery pincode
        List<Serviceability> availableProviders = serviceabilityRepository
                .findByPincodeAndIsDeliveryAvailableTrue(request.getDeliveryPincode());
        // 2. If no providers, return not serviceable
        if (availableProviders.isEmpty()) {
            return ServiceabilityResponse.builder()
                    .serviceable(false)
                    .pickupPincode(request.getPickupPincode())
                    .deliveryPincode(request.getDeliveryPincode())
                    .message("Delivery not available to this pincode")
                    .build();
        }
        // 3. Build delivery options for each provider
        List<DeliveryOptionDto> options = new ArrayList<>();

        for (Serviceability service : availableProviders) {
            // Get rate for this provider & zone
            DeliveryOptionDto option = buildOption(service, request);
            if (option != null) {
                options.add(option);
            }
        }
        // 4. Sort by cost (cheapest first)
        options.sort(Comparator.comparing(DeliveryOptionDto::getCost));
        // 5. Add tags (CHEAPEST to first, FASTEST to quickest)
        addTags(options);
        // 6. Return response
        return ServiceabilityResponse.builder()
                .serviceable(true)
                .pickupPincode(request.getPickupPincode())
                .deliveryPincode(request.getDeliveryPincode())
                .deliveryOptions(options)
                .build();
    }
    /**
     * Build one delivery option from serviceability data.
     */
    private DeliveryOptionDto buildOption(Serviceability service, ServiceabilityRequest request) {
        // Get weight from request (or default 0.5 kg)
        Double weight = request.getPackageDetails() != null
                ? request.getPackageDetails().getChargeableWeight()
                : 0.5;
        // Calculate rate using adapter
        DeliveryProviderAdapter adapter = providerFactory.getAdapter(service.getProviderCode());
        BigDecimal cost = adapter.calculateRate(
                request.getPickupPincode(),
                request.getDeliveryPincode(),
                weight
        );
        return DeliveryOptionDto.builder()
                .provider(service.getProviderCode())
                .providerName(service.getProviderCode().getDisplayName())
                .cost(cost)
                .etaDaysMin(service.getDeliveryDaysMin())
                .etaDaysMax(service.getDeliveryDaysMax())
                .codAvailable(service.getIsCodAvailable())
                .tags(new ArrayList<>())
                .build();
    }
    /**
     * Add tags like CHEAPEST, FASTEST to options.
     */
    private void addTags(List<DeliveryOptionDto> options) {
        if (options.isEmpty()) return;
        // First option is cheapest (already sorted)
        options.get(0).getTags().add(ProviderTag.CHEAPEST);
        // Find fastest (minimum ETA)
        DeliveryOptionDto fastest = options.stream()
                .min(Comparator.comparing(DeliveryOptionDto::getEtaDaysMin))
                .orElse(null);
        if (fastest != null) {
            fastest.getTags().add(ProviderTag.FASTEST);
        }
    }
}
