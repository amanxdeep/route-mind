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
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class ClientConfiguration {

    private final FedexProperties fedexProperties;

    /**
     * Creates and configures OkHttpClient with logging interceptor.
     */
    @Bean
    public OkHttpClient okHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS);

        // Add logging interceptor if enabled
        if (fedexProperties.isEnableLogging()) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(log::info);
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(loggingInterceptor);
        }

        // TODO: Add authentication interceptor here when you have FedEx auth details
        // builder.addInterceptor(new FedexAuthInterceptor(fedexProperties));

        return builder.build();
    }

    /**
     * Creates Retrofit instance for FedEx API.
     */
    @Bean
    public Retrofit fedexRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(fedexProperties.getBaseUrl())
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    /**
     * Creates FedEx API client bean.
     */
    @Bean
    public FedexApiClient fedexApiClient(Retrofit fedexRetrofit) {
        return fedexRetrofit.create(FedexApiClient.class);
    }
}


