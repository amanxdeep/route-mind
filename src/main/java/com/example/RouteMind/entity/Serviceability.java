package com.example.RouteMind.entity;
import com.example.RouteMind.enums.ProviderCode;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
@Entity
@Table(name = "serviceability")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Serviceability {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private ProviderCode providerCode;

    private String pincode;                // "400001"
    private String city;                   // "Mumbai"
    private String state;                  // "Maharashtra"
    private String zone;                   // "A", "B", "C", "D"

    private Boolean isDeliveryAvailable;   // Can deliver here?
    private Boolean isPickupAvailable;     // Can pickup from here?
    private Boolean isCodAvailable;        // COD supported?
    private Boolean isAirAvailable;        // AIR transport available?
    private Boolean isRoadAvailable;       // ROAD transport available?

    private Integer deliveryDaysMin;       // Minimum ETA
    private Integer deliveryDaysMax;       // Maximum ETA
}
