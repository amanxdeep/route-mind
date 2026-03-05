package com.example.RouteMind.enums;

/**
 * Supported weight units for shipments.
 * Values can be used for API communication and conversions.
 */
public enum WeightUnit {
    
    KG("KG", 1.0),              // Kilogram
    LB("LB", 0.453592),         // Pound
    G("G", 0.001),              // Gram
    OZ("OZ", 0.0283495);        // Ounce

    // The string representation for API calls
    private final String code;
    
    // Conversion factor to KG
    private final Double toKgFactor;

    WeightUnit(String code, Double toKgFactor) {
        this.code = code;
        this.toKgFactor = toKgFactor;
    }

    /**
     * Get the API code for this weight unit
     */
    public String getCode() {
        return code;
    }

    /**
     * Convert the given weight from this unit to KG
     */
    public Double toKg(Double weight) {
        if (weight == null || weight <= 0) {
            return null;
        }
        return weight * this.toKgFactor;
    }

    /**
     * Get WeightUnit enum from string code (case-insensitive)
     */
    public static WeightUnit fromCode(String code) {
        if (code == null || code.isEmpty()) {
            return KG; // Default to KG
        }
        
        String upperCode = code.toUpperCase();
        for (WeightUnit unit : WeightUnit.values()) {
            if (unit.code.equals(upperCode)) {
                return unit;
            }
        }
        
        return KG; // Default to KG if code not found
    }
}
