package com.capstone.project.forum.repository;

import com.capstone.project.forum.entity.ForumPostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ForumPostCommentRepository extends JpaRepository<ForumPostComment, Integer> {

    /**
     * 특정 게시글의 댓글을 최신 순으로 조회
     *
     * @param postId 게시글 ID
     * @return 댓글 리스트
     */
    @Query("SELECT c FROM ForumPostComment c WHERE c.forumPost.id = :postId ORDER BY c.createdAt DESC")
    List<ForumPostComment> findCommentsByPostId(@Param("postId") Integer postId);

    /**
     * 특정 사용자가 작성한 댓글 조회
     *
     * @param memberId 사용자 ID
     * @return 댓글 리스트
     */
    @Query("SELECT c FROM ForumPostComment c WHERE c.member.id = :memberId")
    List<ForumPostComment> findCommentsByMember(@Param("memberId") Integer memberId);

    /**
     * 특정 게시글의 댓글을 최신 순으로 조회 (숨김 댓글 제외)
     *
     * @param postId 게시글 ID
     * @return 댓글 리스트
     */
    @Query("SELECT c FROM ForumPostComment c WHERE c.forumPost.id = :postId AND c.hidden = false ORDER BY c.createdAt DESC")
    List<ForumPostComment> findVisibleCommentsByPostId(@Param("postId") Integer postId);

    /**
     * 특정 게시글 댓글의 좋아요 수 업데이트
     *
     * @param commentId 댓글 ID
     */
    @Modifying
    @Query("UPDATE ForumPostComment c SET c.likesCount = c.likesCount + 1 WHERE c.id = :commentId")
    void incrementLikes(@Param("commentId") Integer commentId);


    /**
     * 특정 게시글에 속한 모든 댓글 삭제
     *
     * @param postId 게시글 ID
     */
    @Modifying
    @Query("DELETE FROM ForumPostComment c WHERE c.forumPost.id = :postId")
    void deleteByForumPostId(@Param("postId") Integer postId);

    /**
     * 부모 댓글에 속한 자식 댓글 조회 --> 특정 댓글의 답글 조회
     *
     * @param parentCommentId 부모 댓글 ID
     * @return 자식 댓글 리스트
     */
    @Query("SELECT c FROM ForumPostComment c WHERE c.parentComment.id = :parentCommentId ORDER BY c.createdAt DESC")
    List<ForumPostComment> findRepliesByParentCommentId(@Param("parentCommentId") Integer parentCommentId);

    /**
     * 댓글의 숨김 상태 업데이트
     *
     * @param commentId 댓글 ID
     * @param hidden 숨김 여부
     */
    @Modifying
    @Query("UPDATE ForumPostComment c SET c.hidden = :hidden WHERE c.id = :commentId")
    void updateHiddenStatus(@Param("commentId") Integer commentId, @Param("hidden") boolean hidden);

    // 특정 댓글 숨김 상태 복구
    @Modifying
    @Query("UPDATE ForumPostComment c SET c.hidden = false WHERE c.id = :commentId")
    void restoreCommentById(@Param("commentId") Integer commentId);

    // 특정 댓글 삭제 취소 (복구)
    @Modifying
    @Query("UPDATE ForumPostComment c SET c.content = 'Restored content', c.removedBy = null WHERE c.id = :commentId")
    void undeleteCommentById(@Param("commentId") Integer commentId);

    /**
     * 댓글 삭제 상태를 업데이트
     * (댓글을 실제로 삭제하지 않고, 삭제된 상태로 표시)
     *
     * @param commentId 댓글 ID
     * @param removedBy 삭제자 ("OP", "ADMIN" 등)
     */
    @Modifying
    @Query("UPDATE ForumPostComment c SET c.content = '[Removed]', c.removedBy = :removedBy WHERE c.id = :commentId")
    void markCommentAsRemoved(@Param("commentId") Integer commentId, @Param("removedBy") String removedBy);

}
