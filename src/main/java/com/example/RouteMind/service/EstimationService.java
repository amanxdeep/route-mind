package com.example.RouteMind.service;

import com.example.RouteMind.entity.RateCard;
import com.example.RouteMind.entity.Serviceability;
import com.example.RouteMind.enums.ProviderCode;
import com.example.RouteMind.enums.ServiceType;
import com.example.RouteMind.repository.RateCardRepository;
import com.example.RouteMind.repository.ServiceabilityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Optional;
/**
 * Calculates shipping cost and ETA.
 * Uses rate cards and zone information.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EstimationService {
    private final RateCardRepository rateCardRepository;
    private final ServiceabilityRepository serviceabilityRepository;
    /**
     * Calculate shipping cost for given parameters.
     *
     * Formula: basePrice + (pricePerKg × additionalWeight) + codCharge
     */
    public BigDecimal calculateCost(ProviderCode provider, String zone,
                                    ServiceType serviceType, Double weightKg,
                                    BigDecimal codAmount) {

        log.info("Calculating cost: provider={}, zone={}, weight={}kg", provider, zone, weightKg);
        // 1. Get rate card
        Optional<RateCard> rateCardOpt = rateCardRepository
                .findByProviderCodeAndZoneAndServiceTypeAndIsActiveTrue(provider, zone, serviceType);
        if (rateCardOpt.isEmpty()) {
            log.warn("No rate card found for {}/{}/{}", provider, zone, serviceType);
            return BigDecimal.ZERO;
        }
        RateCard rate = rateCardOpt.get();
        // 2. Calculate base cost
        // First 0.5 kg is covered by base price
        Double additionalWeight = Math.max(0, weightKg - 0.5);
        BigDecimal weightCharge = rate.getPricePerKg()
                .multiply(BigDecimal.valueOf(additionalWeight));
        BigDecimal totalCost = rate.getBasePrice().add(weightCharge);
        // 3. Add COD charge if applicable
        if (codAmount != null && codAmount.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal codCharge = codAmount
                    .multiply(rate.getCodChargePercent())
                    .divide(BigDecimal.valueOf(100));
            // Minimum COD charge
            if (codCharge.compareTo(rate.getCodChargeMin()) < 0) {
                codCharge = rate.getCodChargeMin();
            }
            totalCost = totalCost.add(codCharge);
        }
        log.info("Calculated cost: {}", totalCost);
        return totalCost;
    }
    /**
     * Get zone for a pincode.
     */
    public String getZone(ProviderCode provider, String pincode) {
        return serviceabilityRepository
                .findByProviderCodeAndPincode(provider, pincode)
                .map(Serviceability::getZone)
                .orElse("D"); // Default to zone D if not found
    }
    /**
     * Get estimated delivery days.
     */
    public int[] getEstimatedDays(ProviderCode provider, String zone, ServiceType serviceType) {
        return rateCardRepository
                .findByProviderCodeAndZoneAndServiceTypeAndIsActiveTrue(provider, zone, serviceType)
                .map(rate -> new int[]{rate.getMinDays(), rate.getMaxDays()})
                .orElse(new int[]{5, 7}); // Default 5-7 days
    }
}
