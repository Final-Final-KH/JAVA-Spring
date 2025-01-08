package com.capstone.project.feed.repository;

import com.capstone.project.feed.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Integer> {
    boolean existsByComment_CommentIdAndMemberId(Integer commentId, Integer memberId);
}
