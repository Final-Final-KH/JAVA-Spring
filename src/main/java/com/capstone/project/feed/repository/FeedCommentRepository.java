package com.capstone.project.feed.repository;

import com.capstone.project.feed.entity.Feed;
import com.capstone.project.feed.entity.FeedComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// FeedComment에 대한 리포지토리
@Repository
public interface FeedCommentRepository extends JpaRepository<FeedComment, Integer> {
    // 특정 회원의 모든 댓글 조회
    List<FeedComment> findByMemberId(Integer memberId);

    // 특정 피드의 최상위 댓글 (대댓글 제외) 조회
    // 이 기능이 없을시 피드에 대한 대댓들을 포함한 모든 댓글들을 긁어오게될 것이므로 따로 하나 만들기.
    // 리포지토리에 이렇게 추가함으로서 서비스 레이어가 간단하게 남아서 비즈니스 로직에 더 집중할 수 있게 처리.
    List<FeedComment> findByFeedAndParentCommentIsNull(Feed feed);
}
