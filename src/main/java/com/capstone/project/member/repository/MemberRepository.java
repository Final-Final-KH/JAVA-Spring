package com.capstone.project.member.repository;

import com.capstone.project.member.entity.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 회원 Repository
 * <p>
 * 회원 관련 데이터베이스 작업을 수행하는 인터페이스입니다.
 * </p>
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByProviderId(String providerId);
    boolean existsByEmail(String email);
    Optional<Member> findByProviderAndProviderId(String provider, String providerId);
    boolean existsByPhoneNumber(String phoneNumber);

    /**
     * 현재 사용자(memberId)를 제외한 랜덤 회원 목록 조회
     *
     * @param memberId 현재 로그인한 회원 ID
     * @param pageable 페이징 정보
     * @return 랜덤 회원 목록
     */
    @Query("SELECT m FROM Member m WHERE m.id <> :memberId ORDER BY FUNCTION('RAND')")
    List<Member> findRandomMembersExcludingUser(Integer memberId, Pageable pageable);
}
