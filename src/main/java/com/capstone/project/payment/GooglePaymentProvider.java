package com.capstone.project.payment;

import com.capstone.project.payment.dto.request.PaymentRequestDto;
import com.capstone.project.payment.dto.response.PaymentResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.beans.factory.annotation.Value;  // @Value를 추가
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;


@Slf4j
 // The bean will not be loaded during the "test" profile
public class GooglePaymentProvider extends AbstractPaymentProvider {

    private final String apiKey;
    private final String requestUrl;

    // @Value 애너테이션을 사용하여 application.properties에서 값을 주입
    public GooglePaymentProvider(RestTemplate restTemplate,
                                 @Value("${google.payment.api-key}") String apiKey,  // @Value로 프로퍼티 값 주입
                                 @Value("${google.payment.request-url}") String requestUrl) {  // @Value로 프로퍼티 값 주입
        super(restTemplate);
        if (apiKey == null || apiKey.isBlank() || requestUrl == null || requestUrl.isBlank()) {
            log.warn("Google Payment API key or request URL is missing. Please configure application.properties properly.");
        }
        this.apiKey = apiKey;
        this.requestUrl = requestUrl;
    }

    @Override
    public boolean supports(String provider) {
        return "google".equalsIgnoreCase(provider);
    }

    @Override
    protected String getRequestUrl() {
        return requestUrl;
    }

    @Override
    protected HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey); // API 키로 인증 설정
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON); // JSON 요청 설정
        return headers;
    }

    @Override
    protected Object createPayload(PaymentRequestDto requestDto) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("amount", requestDto.getAmount()); // 결제 금액 설정
        payload.put("currency", "USD"); // 통화 설정
        payload.put("description", requestDto.getItemName()); // 설명 추가
        return payload;
    }

    @Override
    protected PaymentResponseDto handleResponse(ResponseEntity<?> response) {
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        if (responseBody == null) {
            throw new RuntimeException("Failed to process payment. Response body is null.");
        }
        return PaymentResponseDto.builder()
                .transactionId(responseBody.get("transactionId").toString()) // 트랜잭션 ID
                .redirectUrl(null) // 리디렉션 URL은 제공되지 않을 수 있음
                .status(PaymentResponseDto.Status.COMPLETED) // 결제 상태
                .build();
    }
}
