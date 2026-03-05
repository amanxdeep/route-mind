package com.example.RouteMind.client.fedex;

import com.example.RouteMind.client.fedex.dto.tracking.FedexTrackingRequest;
import com.example.RouteMind.client.fedex.dto.tracking.FedexTrackingResponse;
import com.example.RouteMind.client.fedex.dto.FedexOAuthResponse;
import com.example.RouteMind.client.fedex.dto.serviceability.FedexRateRequest;
import com.example.RouteMind.client.fedex.dto.serviceability.FedexRateResponse;
import com.example.RouteMind.client.fedex.dto.shipment.FedexShipmentRequest;
import com.example.RouteMind.client.fedex.dto.shipment.FedexShipmentResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * FedEx API Client interface using Retrofit.
 * Each method here represents one FedEx HTTP endpoint.
 */
public interface FedexApiClient {

    /**
     * OAuth: Get access token
     *
     * POST https://apis-sandbox.fedex.com/oauth/token
     * Content-Type: application/x-www-form-urlencoded
     *
     * Body:
     *   grant_type=client_credentials
     *   client_id=...
     *   client_secret=...
     */
    @POST("/oauth/token")
    @FormUrlEncoded
    Call<FedexOAuthResponse> getAccessToken(
            @Field("grant_type") String grantType,
            @Field("client_id") String clientId,
            @Field("client_secret") String clientSecret
    );

    /**
     * Rate API: Get shipping rates / serviceability
     *
     * POST https://apis-sandbox.fedex.com/rate/v1/rates/quotes
     * Headers:
     *   Authorization: Bearer <token>
     * Body: JSON defined in FedexRateRequest
     */
    @POST("/rate/v1/rates/quotes")
    Call<FedexRateResponse> getRates(
            @Header("Authorization") String authHeader,
            @Body FedexRateRequest request
    );

    @POST("/track/v1/trackingnumbers")
    Call<FedexTrackingResponse> trackShipment(
            @Header("Authorization") String authHeader,
            @Body FedexTrackingRequest request
    );

    @POST("/ship/v1/shipments")
    Call<FedexShipmentResponse> createShipment(
            @Header("Authorization") String authHeader,
            @Body FedexShipmentRequest request
    );
}
