package com.capstone.project.payment;

import com.capstone.project.payment.dto.request.PaymentRequestDto;
import com.capstone.project.payment.dto.response.PaymentResponseDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
@Getter
@Setter
@Slf4j
public class KakaoPaymentProvider extends AbstractPaymentProvider {

    private final String apiKey;
    private final String requestUrl;

    public KakaoPaymentProvider(RestTemplate restTemplate, String apiKey, String requestUrl) {
        super(restTemplate); // Call the superclass constructor
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
        headers.set("Authorization", "KakaoAK " + apiKey);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return headers;
    }

    @Override
    protected Object createPayload(PaymentRequestDto requestDto) {
        Map<String, String> payload = new HashMap<>();
        payload.put("cid", "TC0ONETIME"); // Kakao의 경우, cid ("TC0ONETIME")와 같은 필드는 테스트 환경에서 사용되는 플레이스홀더
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
        assert responseBody != null;
        return PaymentResponseDto.builder()
                .transactionId(responseBody.get("tid").toString())
                .redirectUrl(responseBody.get("next_redirect_pc_url").toString())
                .status(PaymentResponseDto.Status.PENDING)
                .build();
    }
}
