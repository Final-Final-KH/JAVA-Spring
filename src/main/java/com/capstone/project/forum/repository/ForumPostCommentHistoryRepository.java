package com.capstone.project.forum.repository;

import com.capstone.project.forum.entity.ForumPostCommentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ForumPostCommentHistoryRepository extends JpaRepository<ForumPostCommentHistory, Long> {

    /**
     * 댓글 ID로 삭제 히스토리를 조회
     *
     * @param commentId 댓글 ID
     * @return 삭제 히스토리 리스트
     */
    List<ForumPostCommentHistory> findAllByCommentId(Integer commentId);

    Optional<ForumPostCommentHistory> findTopByCommentIdOrderByDeletedAtDesc(Integer commentId);

}