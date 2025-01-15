package com.capstone.project.forum.repository;

import com.capstone.project.forum.entity.PostReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 게시글 신고 Repository 인터페이스
 * 게시글 신고 데이터를 관리하는 JPA 인터페이스
 */
@Repository
public interface PostReportRepository extends JpaRepository<PostReport, Integer> {

    /**
     * 특정 게시글에 대해 특정 사용자가 신고했는지 여부를 확인
     *
     * @param postId 게시글 ID
     * @param reporterId 신고자 ID
     * @return true: 이미 신고, false: 신고하지 않음
     */
    @Query("SELECT CASE WHEN COUNT(pr) > 0 THEN true ELSE false END " +
            "FROM PostReport pr WHERE pr.forumPost.id = :postId AND pr.reporterId = :reporterId")
    boolean existsByPostIdAndReporterId(@Param("postId") Integer postId, @Param("reporterId") Integer reporterId);

    /**
     * 특정 게시글에 대한 총 신고 수를 반환
     *
     * @param postId 게시글 ID
     * @return 신고 수
     */
    @Query("SELECT COUNT(pr) FROM PostReport pr WHERE pr.forumPost.id = :postId")
    long countByPostId(@Param("postId") Integer postId);
}
