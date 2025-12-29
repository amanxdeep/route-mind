package com.example.RouteMind.repository;
import com.example.RouteMind.entity.RateCard;
import com.example.RouteMind.enums.ProviderCode;
import com.example.RouteMind.enums.ServiceType;
import com.example.RouteMind.enums.TransportMode;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
/**
 * Database operations for RateCard entity.
 * Used to get pricing for delivery.
 */
public interface RateCardRepository extends JpaRepository<RateCard, UUID>{

    // Get rate for specific provider, zone, and service
    Optional<RateCard> findByProviderCodeAndZoneAndServiceTypeAndIsActiveTrue(
            ProviderCode providerCode,
            String zone,
            ServiceType serviceType
    );

    // Get all active rates for a provider
    List<RateCard> findByProviderCodeAndIsActiveTrue(ProviderCode providerCode);

    // Get rates for a zone (to compare providers)
    List<RateCard> findByZoneAndServiceTypeAndIsActiveTrue(String zone,
                                                           ServiceType serviceType);

    // Get rates by transport mode
    List<RateCard> findByProviderCodeAndTransportModeAndIsActiveTrue(
            ProviderCode providerCode,
            TransportMode transportMode
    );
}
