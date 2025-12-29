package com.example.RouteMind.factory;

import com.example.RouteMind.adapter.DeliveryProviderAdapter;
import com.example.RouteMind.enums.ProviderCode;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
/**
 * Factory to get the correct adapter for a provider.
 *
 * Usage: providerFactory.getAdapter(ProviderCode.BLUEDART)
 */
@Component
public class ProviderFactory {

    // Map of ProviderCode → Adapter
    private final Map<ProviderCode, DeliveryProviderAdapter> adapterMap;
    /**
     * Spring injects all DeliveryProviderAdapter implementations.
     * We convert the list to a map for quick lookup.
     */
    public ProviderFactory(List<DeliveryProviderAdapter> adapters) {
        this.adapterMap = adapters.stream()
                .collect(Collectors.toMap(
                        DeliveryProviderAdapter::getProviderCode,  // Key: BLUEDART, DELHIVERY, etc.
                        Function.identity()                         // Value: The adapter itself
                ));
    }
    /**
     * Get adapter for a specific provider.
     *
     * @param code Provider code (BLUEDART, FEDEX, DTDC)
     * @return The adapter for that provider
     * @throws IllegalArgumentException if provider not found
     */
    public DeliveryProviderAdapter getAdapter(ProviderCode code) {
        DeliveryProviderAdapter adapter = adapterMap.get(code);
        if (adapter == null) {
            throw new IllegalArgumentException("No adapter found for provider: " + code);
        }
        return adapter;
    }
    /**
     * Get all available adapters.
     */
    public List<DeliveryProviderAdapter> getAllAdapters() {
        return List.copyOf(adapterMap.values());
    }
    /**
     * Check if adapter exists for provider.
     */
    public boolean hasAdapter(ProviderCode code) {
        return adapterMap.containsKey(code);
    }
}
