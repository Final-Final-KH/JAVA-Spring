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
