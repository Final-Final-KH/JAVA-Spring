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

    // 특정 게시글의 댓글을 최신 순으로 조회
    @Query("SELECT c FROM ForumPostComment c WHERE c.forumPost.id = :postId ORDER BY c.createdAt DESC")
    List<ForumPostComment> findCommentsByPostId(@Param("postId") Integer postId);

    // 특정 사용자가 작성한 댓글 조회
    @Query("SELECT c FROM ForumPostComment c WHERE c.member.id = :memberId")
    List<ForumPostComment> findCommentsByMember(@Param("memberId") Integer memberId);

    // 특정 게시글 댓글의 좋아요 수 업데이트
    @Modifying
    @Query("UPDATE ForumPostComment c SET c.likesCount = c.likesCount + 1 WHERE c.id = :commentId")
    void incrementLikes(@Param("commentId") Integer commentId);

    // 특정 게시글에 속한 모든 댓글 삭제
    @Modifying
    @Query("DELETE FROM ForumPostComment c WHERE c.forumPost.id = :postId")
    void deleteByForumPostId(@Param("postId") Integer postId);
}
