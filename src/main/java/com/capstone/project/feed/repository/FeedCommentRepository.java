package com.capstone.project.feed.repository;

import com.capstone.project.feed.entity.FeedComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// FeedComment에 대한 리포지토리
public interface FeedCommentRepository extends JpaRepository<FeedComment, Integer> {
    // 특정 회원의 모든 댓글 조회
    List<FeedComment> findByMemberId(Integer memberId);
}
