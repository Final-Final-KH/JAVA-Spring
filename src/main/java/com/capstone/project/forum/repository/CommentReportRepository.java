package com.capstone.project.forum.repository;

import com.capstone.project.forum.entity.CommentReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 댓글 신고 Repository 인터페이스
 * 댓글 신고 데이터를 관리하는 JPA 인터페이스
 */
@Repository
public interface CommentReportRepository extends JpaRepository<CommentReport, Integer> {

    /**
     * 특정 댓글(commentId)에 대해 특정 사용자(reporterId)가 신고했는지 여부를 확인합니다.
     * 중복 신고를 방지하기 위해 사용됩니다.
     *
     * @param commentId 댓글 ID
     * @param reporterId 신고자 ID
     * @return 사용자가 해당 댓글을 이미 신고했으면 true, 그렇지 않으면 false
     */
    @Query("SELECT CASE WHEN COUNT(cr) > 0 THEN true ELSE false END " +
            "FROM CommentReport cr WHERE cr.forumPostComment.id = :commentId AND cr.member.id = :reporterId")
    boolean existsByCommentIdAndReporterId(@Param("commentId") Integer commentId, @Param("reporterId") Integer reporterId);

    /**
     * 특정 댓글(commentId)에 대한 총 신고 횟수를 반환합니다.
     * 신고 누적 수를 기준으로 댓글 숨김 처리를 결정할 때 사용됩니다.
     *
     * @param commentId 댓글 ID
     * @return 해당 댓글에 대한 신고 횟수
     */
    @Query("SELECT COUNT(cr) FROM CommentReport cr WHERE cr.forumPostComment.id = :commentId")
    long countByCommentId(@Param("commentId") Integer commentId);


    /**
     * 특정 댓글 ID 목록에 대한 신고 횟수를 반환
     *
     * @param commentIds 댓글 ID 목록
     * @return 각 댓글 ID별 신고 횟수를 포함한 Map (key: 댓글 ID, value: 신고 횟수)
     */
    @Query("SELECT c.id AS commentId, COUNT(r.id) AS count " +
            "FROM CommentReport r JOIN r.forumPostComment c " +
            "WHERE c.id IN :commentIds " +
            "GROUP BY c.id")
    List<Object[]> countByCommentIds(@Param("commentIds") List<Integer> commentIds);


}
