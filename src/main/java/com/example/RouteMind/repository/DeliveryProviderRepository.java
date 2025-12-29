package com.example.RouteMind.repository;

import com.example.RouteMind.entity.DeliveryProvider;
import com.example.RouteMind.enums.ProviderCode;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.example.RouteMind.entity.DeliveryProvider;

public interface DeliveryProviderRepository extends JpaRepository<DeliveryProvider,UUID>{

    // Find provider by code
    Optional<DeliveryProvider> findByCode(ProviderCode code);

    // Get all active providers
    List<DeliveryProvider> findByIsActiveTrueOrderByPriorityAsc();

    // Check if provider exists and is active
    boolean existsByCodeAndIsActiveTrue(ProviderCode code);

}
