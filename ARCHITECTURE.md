# RouteMind System Architecture

This document contains comprehensive Mermaid diagrams for the RouteMind system architecture.

## 1. High-Level System Architecture

```mermaid
graph TB
    subgraph Client["Client Layer"]
        WebApp["E-Commerce Web Application"]
    end
    
    subgraph API["API Gateway Layer"]
        REST["REST API Endpoints"]
    end
    
    subgraph Controllers["Controller Layer"]
        OrderCtrl["OrderController"]
        ServiceCtrl["ServiceabilityController"]
        TrackCtrl["TrackingController"]
        WebhookCtrl["WebhookController"]
    end
    
    subgraph Services["Business Logic Layer"]
        OrderSvc["OrderService"]
        ServiceabilitySvc["ServiceabilityService"]
        TrackingSvc["TrackingService"]
        EstimationSvc["EstimationService"]
        DataAccessSvc["OrderDataAccessService"]
    end
    
    subgraph Adapters["Adapter Pattern Layer"]
        AdapterInterface["DeliveryProviderAdapter<br/>Interface"]
        BlueDartImpl["BlueDartAdapter"]
        DtdcImpl["DtdcAdapter"]
        FedexImpl["FedexAdapter"]
    end
    
    subgraph ExternalAPIs["External APIs"]
        BlueDartAPI["BlueDart API"]
        DtdcAPI["DTDC API"]
        FedexAPI["FedEx API"]
    end
    
    subgraph Persistence["Data Persistence Layer"]
        Repos["Repository Layer<br/>JPA Repositories"]
        MySQL["MySQL Database"]
    end
    
    WebApp -->|HTTP| REST
    REST --> Controllers
    
    OrderCtrl --> OrderSvc
    ServiceCtrl --> ServiceabilitySvc
    TrackCtrl --> TrackingSvc
    WebhookCtrl --> TrackingSvc
    
    OrderSvc --> AdapterInterface
    ServiceabilitySvc --> AdapterInterface
    TrackingSvc --> AdapterInterface
    EstimationSvc --> Repos
    DataAccessSvc --> Repos
    
    AdapterInterface --> BlueDartImpl
    AdapterInterface --> DtdcImpl
    AdapterInterface --> FedexImpl
    
    BlueDartImpl --> BlueDartAPI
    DtdcImpl --> DtdcAPI
    FedexImpl --> FedexAPI
    
    OrderSvc --> DataAccessSvc
    TrackingSvc --> DataAccessSvc
    Repos --> MySQL
```

## 2. Request Processing Flow - Order Creation

```mermaid
sequenceDiagram
    participant Client as E-Commerce App
    participant Controller as OrderController
    participant Service as OrderService
    participant Factory as ProviderFactory
    participant Adapter as DeliveryProviderAdapter
    participant API as Provider API
    participant DataAccess as OrderDataAccessService
    participant DB as MySQL Database

    Client->>Controller: POST /api/v1/orders (CreateOrderRequest)
    activate Controller
    Controller->>Service: createOrder(request)
    activate Service
    
    Service->>Service: validateOrderRequest()
    Service->>DataAccess: saveOrder()
    activate DataAccess
    DataAccess->>DB: INSERT Order
    DataAccess-->>Service: Order (with UUID)
    deactivate DataAccess
    
    Service->>Factory: getAdapter(ProviderCode)
    activate Factory
    Factory-->>Service: DeliveryProviderAdapter instance
    deactivate Factory
    
    Service->>Adapter: createShipment(request)
    activate Adapter
    Adapter->>API: POST Create Shipment
    API-->>Adapter: OrderResponse (with AWB/Tracking ID)
    deactivate Adapter
    
    Service->>DataAccess: saveShipmentAndMarkOrder(orderId, shipment)
    activate DataAccess
    DataAccess->>DB: INSERT Shipment<br/>UPDATE Order Status
    DataAccess-->>Service: Saved Shipment
    deactivate DataAccess
    
    Service-->>Controller: OrderResponse
    deactivate Service
    
    Controller-->>Client: GenericResponse<OrderResponse>
    deactivate Controller
```

## 3. Adapter Pattern - Multi-Provider Support

```mermaid
graph TB
    subgraph Client["Service Layer"]
        OrderService["OrderService<br/>ServiceabilityService<br/>TrackingService"]
    end
    
    subgraph AdapterPattern["Adapter Pattern Implementation"]
        AdapterInterface["<<Interface>><br/>DeliveryProviderAdapter<br/>---<br/>+ checkServiceability()<br/>+ calculateRate()<br/>+ createShipment()<br/>+ trackShipment()<br/>+ cancelShipment()"]
        
        subgraph Implementations["Concrete Implementations"]
            BlueDart["BlueDartAdapter<br/>---<br/>+ getProviderCode()<br/>+ checkServiceability()<br/>+ calculateRate()<br/>+ createShipment()<br/>+ trackShipment()"]
            Dtdc["DtdcAdapter<br/>---<br/>+ getProviderCode()<br/>+ checkServiceability()<br/>+ calculateRate()<br/>+ createShipment()<br/>+ trackShipment()"]
            Fedex["FedexAdapter<br/>---<br/>+ getProviderCode()<br/>+ checkServiceability()<br/>+ calculateRate()<br/>+ createShipment()<br/>+ trackShipment()"]
        end
    end
    
    subgraph Factory["Factory Pattern"]
        ProviderFactory["ProviderFactory<br/>---<br/>+ getAdapter(ProviderCode)<br/>Returns appropriate adapter<br/>based on provider code"]
    end
    
    OrderService -->|uses| ProviderFactory
    ProviderFactory -->|instantiates| AdapterInterface
    AdapterInterface -->|implements| BlueDart
    AdapterInterface -->|implements| Dtdc
    AdapterInterface -->|implements| Fedex
    
    BlueDart -.->|calls| BlueDartAPI["BlueDart API"]
    Dtdc -.->|calls| DtdcAPI["DTDC API"]
    Fedex -.->|calls| FedexAPI["FedEx API"]
```

## 4. Data Model - Entity Relationships

```mermaid
graph TB
    subgraph Entities["Core Entities"]
        Order["Order<br/>---<br/>id: UUID<br/>externalOrderId: String<br/>status: DeliveryStatus<br/>pickupAddress: Address<br/>deliveryAddress: Address<br/>createdAt: LocalDateTime<br/>updatedAt: LocalDateTime"]
        
        Shipment["Shipment<br/>---<br/>id: UUID<br/>orderId: UUID<br/>providerId: UUID<br/>trackingId: String<br/>status: DeliveryStatus<br/>estimatedDelivery: LocalDate<br/>actualDelivery?: LocalDate"]
        
        TrackingEvent["TrackingEvent<br/>---<br/>id: UUID<br/>shipmentId: UUID<br/>status: DeliveryStatus<br/>location: String<br/>description: String<br/>timestamp: LocalDateTime<br/>source: String"]
        
        DeliveryProvider["DeliveryProvider<br/>---<br/>id: UUID<br/>providerCode: ProviderCode<br/>providerTag: ProviderTag<br/>name: String<br/>apiKey: String<br/>apiSecret: String"]
        
        RateCard["RateCard<br/>---<br/>id: UUID<br/>providerId: UUID<br/>serviceType: ServiceType<br/>origin: String<br/>destination: String<br/>basePrice: BigDecimal<br/>weightSlab: Double<br/>pricePerKg: BigDecimal"]
        
        Serviceability["Serviceability<br/>---<br/>id: UUID<br/>providerId: UUID<br/>origin: String<br/>destination: String<br/>isServiceable: Boolean<br/>serviceTypes: List"]
        
        OrderDimensions["OrderDimensions<br/>---<br/>id: UUID<br/>orderId: UUID<br/>length: Double<br/>width: Double<br/>height: Double<br/>weight: Double<br/>weightUnit: WeightUnit"]
    end
    
    Order -->|has| Shipment
    Order -->|has| OrderDimensions
    Shipment -->|uses| DeliveryProvider
    Shipment -->|generates| TrackingEvent
    DeliveryProvider -->|has| RateCard
    DeliveryProvider -->|has| Serviceability
```

## 5. API Endpoints Overview

```mermaid
graph TB
    subgraph Container["RouteMind API v1"]
        subgraph Orders["Orders API<br/>/api/v1/orders"]
            Create["POST /<br/>Create Order"]
            GetById["GET /{id}<br/>Get by ID"]
            GetByExtId["GET /external/{extId}<br/>Get by External ID"]
        end
        
        subgraph Serviceability["Serviceability API<br/>/api/v1/serviceability"]
            CheckService["POST /check<br/>Check if serviceable"]
        end
        
        subgraph Tracking["Tracking API<br/>/api/v1/tracking"]
            Track["GET /{trackingId}<br/>Track Shipment"]
        end
        
        subgraph Webhooks["Webhooks<br/>/api/v1/webhooks"]
            ShipmentStatus["POST /shipment-status<br/>Handle Status Update"]
            Delivery["POST /delivery<br/>Handle Delivery Update"]
        end
    end
    
    Client["E-Commerce App"] -->|REST| Container
    Providers["Delivery Providers"] -->|Webhooks| Webhooks
```

## 6. Component Dependencies

```mermaid
graph LR
    Controller["Controllers"]
    Services["Services<br/>OrderService<br/>ServiceabilityService<br/>TrackingService<br/>EstimationService"]
    
    DataAccess["OrderDataAccessService"]
    Repositories["Repositories<br/>OrderRepository<br/>ShipmentRepository<br/>TrackingEventRepository<br/>DeliveryProviderRepository<br/>RateCardRepository<br/>ServiceabilityRepository"]
    
    AdapterLayer["Adapter Layer<br/>ProviderFactory<br/>DeliveryProviderAdapter<br/>BlueDart/Dtdc/Fedex"]
    
    Mapper["AppModelMapper<br/>MapStruct"]
    Configuration["Configuration<br/>ClientConfiguration<br/>FedexProperties"]
    Exceptions["Exception Handling<br/>GlobalExceptionHandling<br/>Custom Exceptions"]
    Constants["Constants<br/>ApiConstants<br/>MapperConstants"]
    
    Controller -->|uses| Services
    Services -->|uses| DataAccess
    Services -->|uses| AdapterLayer
    Services -->|uses| Mapper
    DataAccess -->|uses| Repositories
    AdapterLayer -->|calls| Configuration
    Controller -->|uses| Exceptions
    Services -->|use| Constants
```

## 7. Database Schema Overview

```mermaid
graph TB
    Orders["ORDERS<br/>---<br/>id (PK)<br/>external_order_id<br/>status<br/>pickup_address<br/>delivery_address<br/>created_at<br/>updated_at"]
    
    Shipments["SHIPMENTS<br/>---<br/>id (PK)<br/>order_id (FK)<br/>provider_id (FK)<br/>tracking_id<br/>status<br/>estimated_delivery<br/>actual_delivery"]
    
    TrackingEvents["TRACKING_EVENTS<br/>---<br/>id (PK)<br/>shipment_id (FK)<br/>status<br/>location<br/>description<br/>timestamp<br/>source"]
    
    Providers["DELIVERY_PROVIDERS<br/>---<br/>id (PK)<br/>provider_code<br/>provider_tag<br/>name<br/>api_key<br/>api_secret"]
    
    RateCards["RATE_CARDS<br/>---<br/>id (PK)<br/>provider_id (FK)<br/>service_type<br/>origin<br/>destination<br/>base_price<br/>weight_slab<br/>price_per_kg"]
    
    Serviceability["SERVICEABILITY<br/>---<br/>id (PK)<br/>provider_id (FK)<br/>origin<br/>destination<br/>is_serviceable<br/>service_types"]
    
    OrderDimensions["ORDER_DIMENSIONS<br/>---<br/>id (PK)<br/>order_id (FK)<br/>length<br/>width<br/>height<br/>weight<br/>weight_unit"]
    
    Orders -->|1:N| Shipments
    Shipments -->|N:1| Providers
    Shipments -->|1:N| TrackingEvents
    Providers -->|1:N| RateCards
    Providers -->|1:N| Serviceability
    Orders -->|1:1| OrderDimensions
```

## 8. Technology Stack

```mermaid
graph TB
    subgraph Language["Language & Runtime"]
        Java["Java 21"]
    end
    
    subgraph Framework["Framework & Web"]
        SpringBoot["Spring Boot 4.0.1"]
        SpringWeb["Spring Web"]
        SpringJPA["Spring Data JPA"]
        SpringValidation["Spring Validation"]
        SpringActuator["Spring Actuator"]
    end
    
    subgraph Database["Database"]
        MySQL["MySQL Database"]
        JPA["JPA ORM"]
    end
    
    subgraph ApiIntegration["API Integration"]
        Retrofit["Retrofit HTTP Client"]
        OkHttp["OkHttp"]
        WebClient["Spring WebClient"]
    end
    
    subgraph Utilities["Utilities & Libraries"]
        Lombok["Lombok"]
        MapStruct["MapStruct"]
        Swagger["SpringDoc OpenAPI<br/>Swagger UI"]
    end
    
    Java -->|runs| SpringBoot
    SpringBoot -->|uses| SpringWeb
    SpringBoot -->|uses| SpringJPA
    SpringBoot -->|uses| SpringValidation
    SpringBoot -->|uses| SpringActuator
    SpringJPA -->|access| JPA
    JPA -->|connect| MySQL
    SpringBoot -->|uses| Retrofit
    Retrofit -->|uses| OkHttp
    SpringBoot -->|uses| WebClient
    SpringBoot -->|uses| Lombok
    SpringBoot -->|uses| MapStruct
    SpringBoot -->|documents| Swagger
```

## Architecture Patterns Used

### 1. **Adapter Pattern**
- Allows integration with multiple delivery providers (BlueDart, DTDC, FedEx)
- Each provider has its own adapter implementation
- Services interact with adapters through a common interface

### 2. **Factory Pattern**
- `ProviderFactory` creates appropriate adapter instances based on provider code
- Decouples service layer from concrete adapter implementations

### 3. **Repository Pattern**
- Data access through Spring Data JPA repositories
- Abstracts database operations
- All repositories extend JpaRepository interface

### 4. **Data Transfer Object (DTO) Pattern**
- Separate request and response DTOs
- Cleaner API contracts
- Decouples internal entities from external APIs

### 5. **Service Layer Pattern**
- Business logic centralized in service classes
- Transaction management
- Orchestrates use of adapters and repositories

### 6. **Configuration Externalization**
- Properties-based configuration
- Environment-specific settings via properties files

## Key Features

- **Multi-Provider Support**: Seamless integration with multiple delivery providers
- **Event-Driven Architecture**: Webhook support for real-time updates
- **RESTful API**: Well-structured REST endpoints
- **Type Safety**: MapStruct for compile-time object mapping
- **Comprehensive Error Handling**: Global exception handler with custom exceptions
- **API Documentation**: Swagger/OpenAPI for easy exploration
- **Transaction Management**: Proper ACID compliance with Spring transactions
- **Scalability**: Stateless service design suitable for horizontal scaling
