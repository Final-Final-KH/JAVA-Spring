package com.capstone.project.forum.repository;

import com.capstone.project.forum.entity.CommentReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentReportRepository extends JpaRepository<CommentReport, Integer> {

    /**
     * 특정 댓글에 대해 특정 사용자가 이미 신고했는지 확인
     *
     * @param commentId 신고 대상 댓글 ID
     * @param reporterId 신고자 ID
     * @return 신고 여부
     */
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN TRUE ELSE FALSE END " +
            "FROM CommentReport r WHERE r.forumPostComment.id = :commentId AND r.member.id = :reporterId")
    boolean existsByCommentIdAndReporterId(@Param("commentId") Integer commentId, @Param("reporterId") Integer reporterId);

    /**
     * 특정 댓글에 대한 총 신고 수
     *
     * @param commentId 신고 대상 댓글 ID
     * @return 신고 수
     */
    @Query("SELECT COUNT(r) FROM CommentReport r WHERE r.forumPostComment.id = :commentId")
    long countByCommentId(@Param("commentId") Integer commentId);
}
