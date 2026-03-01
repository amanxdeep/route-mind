package com.example.RouteMind.entity;
import com.example.RouteMind.enums.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "shipments")
@Accessors(chain = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JoinColumn(name = "order_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    @Column(unique = true, nullable = false)
    private String trackingId;

    @Enumerated(EnumType.STRING)
    private ProviderCode providerCode;

    @Enumerated(EnumType.STRING)
    private ServiceType serviceType;

    @Enumerated(EnumType.STRING)
    private TransportMode transportMode;

    private BigDecimal shippingCost;     // Cost charged by provider
    private LocalDate estimatedDeliveryDate;
    private LocalDateTime actualDeliveryTime;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus currentStatus;

    private String currentLocation;
    private Boolean isActive;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        isActive = true;
    }
}
