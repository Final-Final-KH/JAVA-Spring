package com.capstone.project.forum.repository;

import com.capstone.project.forum.entity.ForumPostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ForumPostLikeRepository extends JpaRepository<ForumPostLike, Integer> {
    // 특정 게시글에 대한 좋아요 수 조회
    @Query("SELECT COUNT(l) FROM ForumPostLike l WHERE l.forumPost.id = :postId")
    int countLikesForPost(@Param("postId") Integer postId);

    // 특정 댓글에 대한 좋아요 수 조회
    @Query("SELECT COUNT(l) FROM ForumPostLike l WHERE l.forumPostComment.id = :commentId")
    int countLikesForComment(@Param("commentId") Integer commentId);

    // 특정 사용자가 특정 게시글에 좋아요를 눌렀는지 확인
    @Query("SELECT l FROM ForumPostLike l WHERE l.member.id = :memberId AND l.forumPost.id = :postId")
    Optional<ForumPostLike> findLikeForPostByMember(@Param("memberId") Integer memberId, @Param("postId") Integer postId);

    // 특정 사용자가 특정 댓글에 좋아요를 눌렀는지 확인
    @Query("SELECT l FROM ForumPostLike l WHERE l.member.id = :memberId AND l.forumPostComment.id = :commentId")
    Optional<ForumPostLike> findLikeForCommentByMember(@Param("memberId") Integer memberId, @Param("commentId") Integer commentId);
}

