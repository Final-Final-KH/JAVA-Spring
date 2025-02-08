package com.capstone.project.payment.service;

import com.capstone.project.payment.PaymentProvider;
import com.capstone.project.payment.dto.request.PaymentRequestDto;
import com.capstone.project.payment.dto.response.PaymentResponseDto;
import com.capstone.project.payment.entity.Payment;
import com.capstone.project.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final List<PaymentProvider> paymentProviders;

    public PaymentResponseDto processPayment(PaymentRequestDto requestDto) {
        log.info("Processing payment for provider: {}", requestDto.getProvider());

        // 맞는 제공자 찾기 (구글? 카카오?)
        PaymentProvider provider = paymentProviders.stream()
                .filter(p -> p.supports(requestDto.getProvider()))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Unsupported payment provider: {}", requestDto.getProvider());
                    return new IllegalArgumentException("Unsupported payment provider: " + requestDto.getProvider());
                });

        // 해당 제공자를 통해서 결제 과정 진행
        PaymentResponseDto responseDto;
        try {
            responseDto = provider.processPayment(requestDto);
        } catch (Exception e) {
            log.error("Error processing payment with provider {}: {}", requestDto.getProvider(), e.getMessage(), e);
            throw new RuntimeException("Failed to process payment: " + e.getMessage(), e);
        }

        // 엔티티 문자열에 DTO 상태를 매핑 처리 (Map DTO status to Entity string )
        String statusAsString = responseDto.getStatus().name();

        // 결제를 DB에 저장
        Payment payment = Payment.builder()
                .memberId(requestDto.getMemberId())
                .amount(requestDto.getAmount())
                .status(statusAsString) // Save as String
                .transactionId(responseDto.getTransactionId())
                .provider(responseDto.getProvider())
                .build();
        paymentRepository.save(payment);

        log.info("Payment processed and saved with transaction ID: {}", responseDto.getTransactionId());

        return responseDto;
    }

    // 각 멤버의 지불 내역을 fetch 해오기
    public List<PaymentResponseDto> getPaymentsByMember(Integer memberId) {
        List<Payment> payments = paymentRepository.findByMemberId(memberId);

        return payments.stream()
                .map(payment -> PaymentResponseDto.builder()
                        .paymentId(payment.getPaymentId())
                        .amount(payment.getAmount())
                        .currency(payment.getCurrency())
                        .provider(payment.getProvider())
                        .transactionId(payment.getTransactionId())
                        .status(PaymentResponseDto.Status.valueOf(payment.getStatus().toUpperCase()))
                        .createdAt(payment.getCreatedAt().toString())
                        .updatedAt(payment.getUpdatedAt().toString())
                        .build()
                )
                .toList();
    }

    // 지불 상세 내역을 거래 ID (transaction Id)로 fetch 해오기
    public Payment getPaymentByTransactionId(String transactionId) {
        log.info("Fetching payment details for transaction ID: {}", transactionId);
        return paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> {
                    log.error("Payment not found for transaction ID: {}", transactionId);
                    return new IllegalArgumentException("Payment not found for transaction ID: " + transactionId);
                });
    }
}
