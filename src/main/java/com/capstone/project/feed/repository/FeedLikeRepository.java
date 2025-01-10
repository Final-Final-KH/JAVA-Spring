package com.capstone.project.feed.repository;

import com.capstone.project.feed.entity.FeedLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// 피드 좋아요 리포지토리
@Repository
public interface FeedLikeRepository extends JpaRepository<FeedLike, Integer> {
    // 특정 피드와 회원 ID를 기반으로 좋아요 여부 확인
    Optional<FeedLike> findByFeed_FeedIdAndMemberId(Integer feedId, Integer memberId);

    // 특정 피드의 좋아요 개수 확인
    Long countByFeed_FeedId(Integer feedId);

    // 특정 피드와 회원 ID로 좋아요 존재 여부 확인
    boolean existsByFeed_FeedIdAndMemberId(Integer feedId, Integer memberId);
}
