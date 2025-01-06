package com.capstone.project.payment.dto.response;


import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PaymentResponseDto {
    private Integer paymentId;
    private BigDecimal amount;
    private String currency;
    private String provider;
    private String transactionId;  // 결제에 관한 고유 식별자 (e.g. 카카오 페이 거래 일련 번호 Transaction ID)
    private String redirectUrl;    // 승인을 위해 사용자를 리디렉션하는 URL(해당되는 경우)
    private Status status;         // 결제 상태 (e.g., PENDING, COMPLETED, FAILED)
    private String createdAt;      // Add created_at
    private String updatedAt;      // Add updated_at


    public enum Status {
        PENDING, COMPLETED, FAILED
    }
}
