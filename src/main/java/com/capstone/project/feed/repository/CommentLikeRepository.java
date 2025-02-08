package com.capstone.project.feed.repository;

import com.capstone.project.feed.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Integer> {
    boolean existsByComment_CommentIdAndMemberId(Integer commentId, Integer memberId);
    Optional<CommentLike> findByComment_CommentIdAndMemberId(Integer commentId, Integer memberId);
    Long countByComment_CommentId(Integer commentId); // 좋아요 개수 메서드 추가

    @Transactional
    void deleteByComment_CommentId(Integer commentId);
}
