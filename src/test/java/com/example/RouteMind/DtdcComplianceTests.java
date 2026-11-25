package com.example.RouteMind;

import com.example.RouteMind.adapter.implementation.DtdcAdapter;
import com.example.RouteMind.controller.WebhookController;
import com.example.RouteMind.dto.Request.CreateOrderRequest;
import com.example.RouteMind.dto.Request.ServiceabilityRequest;
import com.example.RouteMind.dto.Response.OrderResponse;
import com.example.RouteMind.dto.Response.ServiceabilityResponse;
import com.example.RouteMind.dto.Response.TrackingResponse;
import com.example.RouteMind.entity.Serviceability;
import com.example.RouteMind.enums.DeliveryStatus;
import com.example.RouteMind.enums.ProviderCode;
import com.example.RouteMind.factory.ProviderFactory;
import com.example.RouteMind.repository.RateCardRepository;
import com.example.RouteMind.repository.ServiceabilityRepository;
import com.example.RouteMind.service.ServiceabilityService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DtdcComplianceTests {

    private final DtdcAdapter adapter = new DtdcAdapter();

    @Test
    void shipmentAndTrackingResponsesIdentifySampleData() {
        OrderResponse shipment = adapter.createShipment(
                CreateOrderRequest.builder().externalOrderId("order-123").build());
        TrackingResponse tracking = adapter.trackShipment(shipment.getTrackingId());

        assertTrue(shipment.getTrackingId().startsWith("SAMPLE-MOCK-DT-"));
        assertTrue(shipment.getMessage().contains("SAMPLE / MOCK DATA"));
        assertEquals(DeliveryStatus.ORDER_CONFIRMED, shipment.getStatus());
        assertTrue(tracking.getDataNotice().contains("SAMPLE / MOCK DATA"));
        assertEquals(DeliveryStatus.IN_TRANSIT, tracking.getCurrentStatus());
    }

    @Test
    void serviceabilityOptionIdentifiesTheIllustrativeRateAndAvailability() {
        ServiceabilityRepository serviceabilityRepository = mock(ServiceabilityRepository.class);
        RateCardRepository rateCardRepository = mock(RateCardRepository.class);
        ProviderFactory providerFactory = mock(ProviderFactory.class);
        when(serviceabilityRepository.findByPincodeAndIsDeliveryAvailableTrue("10001"))
                .thenReturn(List.of(Serviceability.builder()
                        .providerCode(ProviderCode.DTDC)
                        .isCodAvailable(true)
                        .deliveryDaysMin(2)
                        .deliveryDaysMax(4)
                        .build()));
        when(providerFactory.getAdapter(ProviderCode.DTDC)).thenReturn(adapter);

        ServiceabilityResponse response = new ServiceabilityService(
                serviceabilityRepository, rateCardRepository, providerFactory)
                .checkServiceability(ServiceabilityRequest.builder()
                        .pickupPincode("10000")
                        .deliveryPincode("10001")
                        .build());

        assertEquals(new BigDecimal("60.00"), response.getDeliveryOptions().get(0).getCost());
        assertTrue(response.getDeliveryOptions().get(0).getDataNotice()
                .contains("SAMPLE / MOCK DATA"));
    }

    @Test
    void unsupportedAdapterOperationsDoNotReturnOperationalSuccess() {
        assertThrows(UnsupportedOperationException.class,
                () -> adapter.checkServiceability("10000", "10001"));
        assertThrows(UnsupportedOperationException.class,
                () -> adapter.cancelShipment("SAMPLE-MOCK-DT-123"));
    }

    @Test
    void webhookReturnsNotImplementedWithoutProcessingPayload() throws Exception {
        WebhookController controller = new WebhookController(null, null);

        var response = controller.dtdcWebhook(Map.of(
                "consignment_no", "SAMPLE-MOCK-DT-123",
                "status", "DELIVERED"));

        assertEquals("NOT_IMPLEMENTED", response.getError().getCode());
        assertTrue(response.getError().getMessage().contains("no payload was processed"));
        ResponseStatus responseStatus = WebhookController.class
                .getMethod("dtdcWebhook", Map.class)
                .getAnnotation(ResponseStatus.class);
        assertEquals(HttpStatus.NOT_IMPLEMENTED, responseStatus.value());
    }
}
