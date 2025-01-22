package com.capstone.project.payment;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Google Payment 관련 API 키와 URL을 설정하기 위한 Bean을 정의합니다.
 * Google Payment API와 관련된 설정을 관리하는 Configuration 클래스
 *
 * - WHY: API 키와 URL을 외부 설정에서 읽어와 동적으로 주입합니다.
 *        현재는 모의(mock) 데이터를 사용하며, 실제 결제 기능 활성화 전까지는 이 상태로 유지됩니다.
 *
 * - LATER: production 환경에서는 System.getProperty 대신 application.properties나
 *          환경 변수를 통해 값을 읽도록 설정해야 합니다.
 */
@Configuration
public class GooglePaymentConfiguration {

    @Bean
    public String googleApiKey() {
        // Read the API key from properties or environment variables
        return System.getProperty("google.payment.api-key", "mock_api_key");
    }

    @Bean
    public String googleRequestUrl() {
        // Read the request URL from properties or environment variables
        return System.getProperty("google.payment.request-url", "https://mock-google-api.com/payments");
    }
}

