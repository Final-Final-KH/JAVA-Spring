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
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        return headers;
    }

    @Override
    // Map<String, Object> 또는 JSON과 유사한 페이로드를 사용
    // 이는 Google의 API가 JSON 요청 본문을 기대하기 때문일 가능성이 높기 때문.
    // 리디렉션 URL을 생략하고, 트랜잭션 ID와 결제 금액에 집중
    protected Object createPayload(PaymentRequestDto requestDto) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("amount", requestDto.getAmount());
        payload.put("currency", "USD");
        payload.put("description", requestDto.getItemName());
        return payload;
    }

    @Override
    protected PaymentResponseDto handleResponse(ResponseEntity<?> response) {
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assert responseBody != null;
        return PaymentResponseDto.builder()
                .transactionId(responseBody.get("transactionId").toString())
                .redirectUrl(null) // Google may not provide a redirect URL
                .status(PaymentResponseDto.Status.COMPLETED)
                .build();
    }
}
