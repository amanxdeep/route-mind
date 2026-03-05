package com.example.RouteMind.config;

import com.example.RouteMind.client.fedex.FedexApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

/**
 * Client configuration for FedEx API client.
 * Configures Retrofit with OkHttpClient, timeouts, and request/response logging.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class ClientConfiguration {

    private final FedexProperties fedexProperties;

    /**
     * Creates and configures OkHttpClient with logging interceptor.
     * Logs complete request/response body in JSON format.
     */
    @Bean
    public OkHttpClient okHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS);

        // Add logging interceptor if enabled
        if (fedexProperties.isEnableLogging()) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> {
                if (message != null && !message.isEmpty()) {
                    // Log complete serialized JSON request/response body
                    log.info("FedEx API: {}", message);
                }
            });
            // Set to BODY level to log complete request/response with headers and body
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(loggingInterceptor);
            log.info("FedEx API logging enabled at BODY level (complete JSON request/response)");
        }

        return builder.build();
    }

    /**
     * Creates Retrofit instance for FedEx API.
     * Configures base URL and JSON converter.
     */
    @Bean
    public Retrofit fedexRetrofit(OkHttpClient okHttpClient) {
        log.info("Initializing FedEx Retrofit client with base URL: {}", fedexProperties.getBaseUrl());
        return new Retrofit.Builder()
                .baseUrl(fedexProperties.getBaseUrl())
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    /**
     * Creates FedEx API client bean.
     * Used for making API calls to FedEx endpoints.
     */
    @Bean
    public FedexApiClient fedexApiClient(Retrofit fedexRetrofit) {
        log.info("Creating FedEx API client bean");
        return fedexRetrofit.create(FedexApiClient.class);
    }
}


