package com.example.RouteMind.entity;



import com.example.RouteMind.enums.DeliveryStatus;
import com.example.RouteMind.enums.PaymentMode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity //THIS CLAS MAPS TO A DATABASE TABLE
@Table(name = "orders")//THIS IS THE TABLE NAME IN THE DATABASE
@Data//THIS IS A LOMBOK ANNOTATION THAT GENERATES GETTERS, SETTERS, TO STRING, EQUALS, AND HASHCODE METHODS
@NoArgsConstructor//GENERATES EMPTY CONSTRUCTOR
@AllArgsConstructor//GENERATES CONSTRUCTOR WITH ALL FIELDS
@Builder//GENERATES BUILDER PATTERN

public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String externalOrderId;


    private String customerId;

    private String pickUpName;
    private String pickUpPhone;
    private String pickUpAddress;
    private String pickUpCity;
    private String pickUpState;
    private String pickUpPinCode;
    private String pickUpEmail;

    private String deliverName;
    private String deliveryPhone;
    private String deliveryAddress;
    private String deliveryCity;
    private String deliveryState;
    private String deliveryPinCode;
    private String deliveryEmail;

    @Embedded
    private OrderDimensions orderDimensions;
    private String productDescription ;

    @Enumerated(EnumType.STRING)
    private PaymentMode paymentMode;
    private BigDecimal codValue;

    private LocalDateTime createdAt;
    private DeliveryStatus status;
    private LocalDateTime updatedAt;
    @PrePersist                        // Called before INSERT
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        status = DeliveryStatus.ORDER_PLACED;
    }
    @PreUpdate                         // Called before UPDATE
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
