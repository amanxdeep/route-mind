package com.example.RouteMind.entity;


import com.example.RouteMind.enums.*;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;
@Entity
@Table(name = "rate_cards")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class RateCard {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Enumerated(EnumType.STRING)
    private ProviderCode providerCode;
    private String zone;                    // "A", "B", "C", "D", "E"
    @Enumerated(EnumType.STRING)
    private ServiceType serviceType;        // EXPRESS, STANDARD, ECONOMY
    @Enumerated(EnumType.STRING)
    private TransportMode transportMode;    // AIR, ROAD
    private BigDecimal basePrice;           // Base rate
    private BigDecimal pricePerKg;          // Additional per kg
    private BigDecimal codChargePercent;    // COD fee %
    private BigDecimal codChargeMin;        // Minimum COD fee

    private Integer minDays;                // Min delivery days
    private Integer maxDays;                // Max delivery days
    private Boolean isActive;
}
