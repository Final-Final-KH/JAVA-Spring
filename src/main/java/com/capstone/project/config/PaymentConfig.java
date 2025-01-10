package com.capstone.project.config;

import com.capstone.project.payment.GooglePaymentProvider;
import com.capstone.project.payment.KakaoPaymentProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.HttpClientErrorException;
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

    // Flask API에서 뉴스 가져오기
    public String getNewsFromFlaskApi(String searchQuery) {
        String flaskApiUrl = "http://localhost:5000/news";  // Flask API URL

        if (searchQuery != null && !searchQuery.isEmpty()) {
            flaskApiUrl += "?search=" + searchQuery;
        }

        System.out.println("Sending request to Flask API: " + flaskApiUrl); // 요청 URL 출력

        try {
            String response = restTemplate().getForObject(flaskApiUrl, String.class);
            System.out.println("Flask API Response: " + response); // 응답 출력
            return response;
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            return "Error fetching data from Flask API.";
        }
    }
}
