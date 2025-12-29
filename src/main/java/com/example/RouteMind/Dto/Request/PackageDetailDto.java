package com.example.RouteMind.Dto.Request;

import com.example.RouteMind.enums.ProductCategory;
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

    // Actual weight in kilograms
    private Double weightKg;

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
     * Chargeable weight = max(actual, volumetric)
     */
    public Double getChargeableWeight() {
        return Math.max(weightKg != null ? weightKg : 0, getVolumetricWeight());
    }
}
