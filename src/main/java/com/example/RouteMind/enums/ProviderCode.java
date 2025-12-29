package com.example.RouteMind.enums;
/*
 * Identifies each delivery provider.
 * Used to: select adapter, store in database, identify in logs
 */


public enum ProviderCode {

    BLUEDART("Bluedart", "BD"),
    FEDEX("FedEx", "FX"),
    DTDC("DTDC", "DC");

    private final String displayName;
    private final String shortCode;

    ProviderCode(String displayName, String shortCode) {
        this.displayName = displayName;
        this.shortCode = shortCode;
    }
    public String getDisplayName(){
        return displayName;
    }
    public String getShortCode(){
        return shortCode;
    }
}
