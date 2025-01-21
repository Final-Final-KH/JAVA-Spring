package com.capstone.project.forum.repository;

import com.capstone.project.forum.entity.ForumPostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ForumPostLikeRepository extends JpaRepository<ForumPostLike, Integer> {

    /**
     * 게시글에 대한 좋아요 수를 계산
     *
     * @param postId 게시글 ID
     * @return 좋아요 개수
     */
    @Query("SELECT COUNT(l) FROM ForumPostLike l WHERE l.postId = :postId")
    int countLikesForPost(@Param("postId") Integer postId);

    /**
     * 댓글에 대한 좋아요 수를 계산
     *
     * @param commentId 댓글 ID
     * @return 좋아요 개수
     */
    @Query("SELECT COUNT(l) FROM ForumPostLike l WHERE l.commentId = :commentId")
    int countLikesForComment(@Param("commentId") Integer commentId);

    /**
     * 특정 사용자가 특정 게시글에 좋아요를 눌렀는지 확인
     *
     * @param postId 게시글 ID
     * @param memberId 사용자 ID
     * @return 좋아요 엔티티 (없을 경우 Optional.empty)
     */
    @Query("SELECT l FROM ForumPostLike l WHERE l.postId = :postId AND l.member.id = :memberId")
    Optional<ForumPostLike> findByPostIdAndMemberId(@Param("postId") Integer postId, @Param("memberId") Integer memberId);

    /**
     * 특정 사용자가 특정 댓글에 좋아요를 눌렀는지 확인
     *
     * @param commentId 댓글 ID
     * @param memberId 사용자 ID
     * @return 좋아요 엔티티 (없을 경우 Optional.empty)
     */
    @Query("SELECT l FROM ForumPostLike l WHERE l.commentId = :commentId AND l.member.id = :memberId")
    Optional<ForumPostLike> findByCommentIdAndMemberId(@Param("commentId") Integer commentId, @Param("memberId") Integer memberId);
}
