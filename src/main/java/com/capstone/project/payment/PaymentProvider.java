package com.capstone.project.payment;

import com.capstone.project.payment.dto.request.PaymentRequestDto;
import com.capstone.project.payment.dto.response.PaymentResponseDto;

// 인터페이스는 모든 payment provider가 supports / processPayment 메서드를 가지게 보장.
// 이것이 contract = 계약!
public interface PaymentProvider {
    boolean supports(String provider); // Check if the provider matches
    PaymentResponseDto processPayment(PaymentRequestDto requestDto); // Process payment
}
