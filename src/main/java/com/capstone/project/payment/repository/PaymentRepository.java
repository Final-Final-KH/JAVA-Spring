package com.capstone.project.payment.repository;

import com.capstone.project.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    // 특정 회원의 모든 결제를 검색
    List<Payment> findByMemberId(Integer memberId);

    // 상태별 결제 검색(관리 기능 옵션)
    List<Payment> findByStatus(String status);

    // 거래 ID로 결제 찾기
    Optional<Payment> findByTransactionId(String transactionId);
}
