package com.example.RouteMind.repository;
import com.example.RouteMind.entity.Serviceability;
import com.example.RouteMind.enums.ProviderCode;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
/**
 * Database operations for Serviceability entity.
 * Used to check if delivery is possible to a pincode.
 */
public interface ServiceabilityRepository extends JpaRepository<Serviceability, UUID>  {

    // Find all providers serving a pincode
    List<Serviceability> findByPincodeAndIsDeliveryAvailableTrue(String pincode);

    // Find specific provider's serviceability for pincode
    Optional<Serviceability> findByProviderCodeAndPincode(ProviderCode providerCode, String pincode);

    // Find providers with COD available
    List<Serviceability> findByPincodeAndIsCodAvailableTrue(String pincode);

    // Find providers with AIR transport available
    List<Serviceability> findByPincodeAndIsAirAvailableTrue(String pincode);

    // Check if pickup available from pincode
    List<Serviceability> findByPincodeAndIsPickupAvailableTrue(String pincode);

}
