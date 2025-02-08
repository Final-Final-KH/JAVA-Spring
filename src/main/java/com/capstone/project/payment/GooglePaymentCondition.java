package com.capstone.project.payment;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * 이 클래스는 GooglePaymentProvider를 조건부로 로드하기 위한 조건을 정의합니다.
 *
 * 현재 google.payment.api-key 및 google.payment.request-url 설정이 존재하는 경우에만
 * GooglePaymentProvider가 활성화됩니다.
 *
 * - WHY: 아직 결제 기능이 구현되지 않았거나, API 키와 URL이 없는 상황에서
 *        불필요한 초기화를 방지하기 위함입니다.
 *
 * - LATER: 실제 결제 기능 활성화 시 application.properties나 환경 변수에
 *          google.payment.api-key와 google.payment.request-url 값을 명확히 설정해야 합니다.
 */
public class GooglePaymentCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String apiKey = context.getEnvironment().getProperty("google.payment.api-key");
        String requestUrl = context.getEnvironment().getProperty("google.payment.request-url");

        // apiKey와 requestUrl이 모두 설정된 경우에만 true 반환
        return apiKey != null && !apiKey.isBlank() && requestUrl != null && !requestUrl.isBlank();
    }
}
