package com.capstone.project.payment;

import com.capstone.project.payment.dto.request.PaymentRequestDto;
import com.capstone.project.payment.dto.response.PaymentResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;


@Slf4j

public class KakaoPaymentProvider extends AbstractPaymentProvider {

    private final String apiKey;
    private final String requestUrl;

    public KakaoPaymentProvider(RestTemplate restTemplate,
                                @Qualifier("kakaoApiKey") String apiKey,
                                @Qualifier("kakaoRequestUrl") String requestUrl) {
        super(restTemplate);
        this.apiKey = apiKey;
        this.requestUrl = requestUrl;
    }

    @Override
    public boolean supports(String provider) {
        return "kakao".equalsIgnoreCase(provider);
    }

    @Override
    protected String getRequestUrl() {
        return requestUrl;
    }

    @Override
    protected HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + apiKey); // API 키로 인증 설정 / Configure authentication with API key
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED); // 폼 URL-인코딩 설정 / Set form URL-encoding
        return headers;
    }

    @Override
    protected Object createPayload(PaymentRequestDto requestDto) {
        Map<String, String> payload = new HashMap<>();
        payload.put("cid", "TC0ONETIME"); // 테스트 환경의 CID 값 / CID value for test environment
        payload.put("partner_order_id", requestDto.getOrderId());
        payload.put("partner_user_id", requestDto.getMemberId().toString());
        payload.put("item_name", requestDto.getItemName());
        payload.put("quantity", String.valueOf(requestDto.getQuantity()));
        payload.put("total_amount", requestDto.getAmount().toString());
        payload.put("tax_free_amount", "0");
        payload.put("approval_url", requestDto.getApprovalUrl());
        payload.put("cancel_url", requestDto.getCancelUrl());
        payload.put("fail_url", requestDto.getFailUrl());
        return payload;
    }

    @Override
    protected PaymentResponseDto handleResponse(ResponseEntity<?> response) {
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        if (responseBody == null) {
            throw new RuntimeException("Failed to process payment. Response body is null.");
        }
        return PaymentResponseDto.builder()
                .transactionId(responseBody.get("tid").toString()) // Kakao의 트랜잭션 ID / Kakao transaction ID
                .redirectUrl(responseBody.get("next_redirect_pc_url").toString()) // 리디렉션 URL 제공 / Redirect URL
                .status(PaymentResponseDto.Status.PENDING) // 결제 상태 설정 / Set payment status
                .build();
    }
}
