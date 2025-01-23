package com.capstone.project.jwt.repository;

import com.capstone.project.jwt.entity.RefreshToken;
import com.capstone.project.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // 특정 사용자에 대해 리프레시 토큰을 찾기 위한 메소드
    Optional<RefreshToken> findByMemberId(Long memberId);
    Optional<RefreshToken> findByMember(Member member);
    Optional<RefreshToken> findByRefreshToken(String token);

    // 리프레시 토큰을 갱신하기 위한 메소드
    void deleteByMemberId(Long memberId);  // 특정 사용자의 리프레시 토큰 삭제

}

