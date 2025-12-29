package com.example.RouteMind.entity;


import com.example.RouteMind.enums.ProviderCode;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;
@Entity
@Table(name = "delivery_providers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class DeliveryProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    private ProviderCode code;           // BLUEDART, FEDEX, DTDC

    private String name;                  // "Blue Dart Express"
    private String baseUrl;               // Url link to provider's API
    private String apiKey;                // API key (encrypted)
    private String apiSecret;             // API secret (encrypted)
    private Boolean isActive;             // Enable/disable provider
    private Integer priority;             // Selection priority (1 = highest)

    private Double successRate;           // Historical success %

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        isActive = true;
    }
}
