package com.capstone.project.payment;

import com.capstone.project.payment.dto.request.PaymentRequestDto;
import com.capstone.project.payment.dto.response.PaymentResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Google 결제 기능을 구현하기 위한 Provider 클래스
 * GooglePaymentProvider는 Google 결제 API와 상호작용하는 클래스로, 조건부로 로드됩니다.
 *
 * - WHY: GooglePaymentCondition이 true일 때만 로드되며, 현재는 모의(mock) 데이터를 사용합니다.
 *        API 키나 URL이 유효하지 않을 경우 작동하지 않습니다.
 *
 * - LATER: 실제 구현 시, Google API에 맞는 payload와 응답 처리를 구현해야 하며,
 *          테스트 환경에서는 모의(mock) 데이터를 유지하되 production 환경에서는 실제 데이터를 사용해야 합니다.
 */
@Component
@Slf4j
@Conditional(GooglePaymentCondition.class) // 조건부로 로드
public class GooglePaymentProvider extends AbstractPaymentProvider {

    private final String apiKey;
    private final String requestUrl;

    public GooglePaymentProvider(RestTemplate restTemplate, @Qualifier("googleApiKey") String apiKey, @Qualifier("googleRequestUrl") String requestUrl) {
        super(restTemplate);
        this.apiKey = apiKey;
        this.requestUrl = requestUrl;

        if (apiKey.isBlank() || requestUrl.isBlank()) {
            log.warn("Google Payment API key or request URL is missing. Please configure application.properties properly.");
        }
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
        payload.put("amount", requestDto.getAmount()); // 결제 금액
        payload.put("currency", "USD"); // 통화 설정
        payload.put("description", requestDto.getItemName()); // 결제 설명
        return payload;
    }

    @Override
    protected PaymentResponseDto handleResponse(ResponseEntity<?> response) {
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        if (responseBody == null) {
            throw new RuntimeException("결제 처리 실패: 응답 본문이 비어있습니다.");
        }
        return PaymentResponseDto.builder()
                .transactionId(responseBody.get("transactionId").toString()) // Google의 트랜잭션 ID
                .redirectUrl(null) // 리디렉션 URL (Google은 제공하지 않을 수 있음)
                .status(PaymentResponseDto.Status.COMPLETED) // 결제 상태
                .build();
    }
}
