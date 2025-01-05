package com.capstone.project.payment.dto.request;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PaymentRequestDto {
    private String provider; // Google or Kakao
    private Integer memberId;          // 결제 (유발)하는 멤버 ID
    private String orderId;         // 각 주문 고유 식별자
    private String itemName;        // 유저가 구입하는 아이템 이름
    private Integer quantity;       // 아이템 갯수
    private BigDecimal amount;      // 총 지불 금액
    private String approvalUrl;     // 결제 성공시 리다이레긑될 URL 주소
    private String cancelUrl;       // 결제 취소시 리다이레긑될 URL 주소
    private String failUrl;         // 결제 실패시 리다이레긑될 URL 주소
}
