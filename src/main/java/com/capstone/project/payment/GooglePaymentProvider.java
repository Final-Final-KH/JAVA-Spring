package com.capstone.project.payment;

import com.capstone.project.payment.dto.request.PaymentRequestDto;
import com.capstone.project.payment.dto.response.PaymentResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class GooglePaymentProvider extends AbstractPaymentProvider {

    private final String apiKey;
    private final String requestUrl;

    public GooglePaymentProvider(RestTemplate restTemplate, String apiKey, String requestUrl) {
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
        headers.set("Authorization", "Bearer " + apiKey); // API 키로 인증 설정 / Configure authentication with API key
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON); // JSON 요청 설정 / Set JSON request
        return headers;
    }

    @Override
    protected Object createPayload(PaymentRequestDto requestDto) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("amount", requestDto.getAmount()); // 결제 금액 설정 / Set payment amount
        payload.put("currency", "USD"); // 통화 설정 (예: USD) / Set currency (e.g., USD)
        payload.put("description", requestDto.getItemName()); // 설명 추가 / Add description
        return payload;
    }

    @Override
    protected PaymentResponseDto handleResponse(ResponseEntity<?> response) {
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        if (responseBody == null) {
            throw new RuntimeException("Failed to process payment. Response body is null.");
        }
        return PaymentResponseDto.builder()
                .transactionId(responseBody.get("transactionId").toString()) // Google의 트랜잭션 ID / Transaction ID
                .redirectUrl(null) // Google은 리디렉션 URL을 제공하지 않을 수 있음 / Google may not provide redirect URL
                .status(PaymentResponseDto.Status.COMPLETED) // 결제 상태 설정 / Set payment status
                .build();
    }
}
