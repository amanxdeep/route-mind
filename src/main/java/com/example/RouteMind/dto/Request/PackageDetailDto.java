package com.example.RouteMind.dto.Request;

import com.example.RouteMind.enums.ProductCategory;
import com.example.RouteMind.enums.WeightUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
/**
 * Package dimensions and product details.
 * Used for pricing and serviceability checks.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class PackageDetailDto {

    // Dimensions in centimeters
    private Double lengthCm;
    private Double breadthCm;
    private Double heightCm;

    // Actual weight in kilograms (for backward compatibility)
    private Double weightKg;

    // Generic weight value with configurable unit
    private Double weight;

    // Weight unit (KG, LB, G, OZ) - defaults to KG if not specified
    private WeightUnit weightUnit;

    // Product value for insurance
    private BigDecimal declaredValue;

    // What's being shipped
    private String productDescription;

    // Category for restrictions check
    private ProductCategory category;

    // Special handling flags
    private Boolean isFragile;
    private Boolean isDangerous;

    /**
     * Calculate volumetric weight: (L × B × H) / 5000
     */
    public Double getVolumetricWeight() {
        if (lengthCm == null || breadthCm == null || heightCm == null) {
            return 0.0;
        }
        return (lengthCm * breadthCm * heightCm) / 5000;
    }

    /**
     * Get weight in KG, handling both legacy weightKg and new weight/weightUnit fields.
     * Converts weight to KG if a different unit is specified.
     * Fallback priority: weight (converted to KG) -> weightKg -> null
     */
    public Double getWeightInKg() {
        // If new weight/weightUnit fields are provided, use them
        if (weight != null && weight > 0) {
            WeightUnit unit = weightUnit != null ? weightUnit : WeightUnit.KG;
            return unit.toKg(weight);
        }
        
        // Fallback to legacy weightKg field
        if (weightKg != null && weightKg > 0) {
            return weightKg;
        }
        
        return null;
    }
    /**
     * Chargeable weight = max(actual, volumetric)
     */
    public Double getChargeableWeight() {
        return Math.max(weightKg != null ? weightKg : 0, getVolumetricWeight());
    }
}
