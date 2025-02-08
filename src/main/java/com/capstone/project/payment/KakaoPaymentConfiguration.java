package com.capstone.project.payment;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Kakao Payment API와 관련된 설정을 관리하는 Configuration 클래스
 *
 * - WHY: API 키와 URL을 외부 설정에서 읽어와 동적으로 주입합니다.
 *
 * - LATER: production 환경에서는 System.getProperty 대신
 *          application.properties 또는 환경 변수를 통해 값을 읽도록 설정해야 합니다.
 */
@Configuration
public class KakaoPaymentConfiguration {

    @Bean
    public String kakaoApiKey() {
        return System.getProperty("kakao.payment.api-key", "mock_kakao_api_key");
    }

    @Bean
    public String kakaoRequestUrl() {
        return System.getProperty("kakao.payment.request-url", "https://mock-kakao-api.com/payments");
    }
}
