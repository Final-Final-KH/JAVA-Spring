package com.capstone.project.config;

import com.capstone.project.payment.GooglePaymentProvider;
import com.capstone.project.payment.KakaoPaymentProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class PaymentConfig {

    @Value("${google.payment.api-key}")
    private String googleApiKey;

    @Value("${google.payment.request-url}")
    private String googleRequestUrl;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
