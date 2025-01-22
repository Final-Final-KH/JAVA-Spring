package com.capstone.project.payment;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * KakaoPaymentProvider를 조건부로 로드하기 위한 조건 클래스
 * KakaoPaymentProvider의 로딩 여부를 결정하는 조건입니다.
 *
 * - WHY: 카카오 결제 API가 설정되지 않은 상태에서 초기화를 방지하기 위함입니다.
 *
 * - LATER: API 키와 URL 설정이 추가되면 application.properties에 kakao.payment.api-key와
 *          kakao.payment.request-url을 설정해야 합니다.
 */
public class KakaoPaymentCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String apiKey = context.getEnvironment().getProperty("kakao.payment.api-key");
        String requestUrl = context.getEnvironment().getProperty("kakao.payment.request-url");

        return apiKey != null && !apiKey.isBlank() && requestUrl != null && !requestUrl.isBlank();
    }
}
