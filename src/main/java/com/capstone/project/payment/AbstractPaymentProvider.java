package com.capstone.project.payment;

import com.capstone.project.payment.dto.request.PaymentRequestDto;
import com.capstone.project.payment.dto.response.PaymentResponseDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


// 추상 클래스인 AbstractPaymentProvider가 인터페이스를 구현하고,
// HTTP 요청 핸들링, 에러 같은 공통 기능을 제공한다.
// 서브 클래스들이 이러한 공유된 동작을 상속 받는다.
public abstract class AbstractPaymentProvider implements PaymentProvider {

    private final RestTemplate restTemplate;

    protected AbstractPaymentProvider(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public abstract boolean supports(String provider);

    protected abstract String getRequestUrl();

    protected abstract HttpHeaders createHeaders();

    protected abstract Object createPayload(PaymentRequestDto requestDto);

    protected abstract PaymentResponseDto handleResponse(ResponseEntity<?> response);

    @Override
    public PaymentResponseDto processPayment(PaymentRequestDto requestDto) {
        try {
            // 헤더랑 페이로드 준비
            HttpHeaders headers = createHeaders();
            Object payload = createPayload(requestDto);

            // HTTP 요청 실행
            HttpEntity<Object> requestEntity = new HttpEntity<>(payload, headers);
            ResponseEntity<?> response = restTemplate.postForEntity(getRequestUrl(), requestEntity, Object.class);

            // 응답 핸들링
            return handleResponse(response);
        } catch (Exception e) {
            throw new RuntimeException("Error processing payment: " + e.getMessage(), e);
        }
    }
}
