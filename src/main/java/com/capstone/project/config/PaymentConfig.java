package com.capstone.project.config;

import com.capstone.project.payment.GooglePaymentProvider;
import com.capstone.project.payment.KakaoPaymentProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class PaymentConfig {

    @Value("${kakao.payment.api-key}")
    private String kakaoApiKey;

    @Value("${kakao.payment.request-url}")
    private String kakaoRequestUrl;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean // 의존성 주입 처리
    public KakaoPaymentProvider kakaoPaymentProvider(RestTemplate restTemplate) {
        return new KakaoPaymentProvider(restTemplate, kakaoApiKey, kakaoRequestUrl);
    }
}
