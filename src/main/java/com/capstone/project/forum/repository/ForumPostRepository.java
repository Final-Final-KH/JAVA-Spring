package com.capstone.project.forum.repository;

import com.capstone.project.forum.entity.ForumPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ForumPostRepository extends JpaRepository<ForumPost, Integer> {
    // 특정 카테고리에 속한 모든 게시글 조회 (Sticky 우선, 생성일 역순 정렬)
    @Query("SELECT p FROM ForumPost p WHERE p.forumCategory.id = :categoryId ORDER BY p.sticky DESC, p.createdAt DESC")
    Page<ForumPost> findPostsByCategory(@Param("categoryId") Integer categoryId, Pageable pageable);


    // 특정 사용자가 작성한 게시글 조회
    @Query("SELECT p FROM ForumPost p WHERE p.member.id = :memberId")
    List<ForumPost> findPostsByMember(@Param("memberId") Integer memberId);

    /**
     * 특정 카테고리에 속한 게시글 조회 (숨김된 게시글 제외)
     *
     * @param categoryId 카테고리 ID
     * @return 게시글 리스트
     */
    @Query("SELECT p FROM ForumPost p WHERE p.forumCategory.id = :categoryId AND p.hidden = false ORDER BY p.createdAt DESC")
    List<ForumPost> findVisiblePostsByCategory(@Param("categoryId") Integer categoryId);

    /**
     * 숨김 상태인 게시글 조회 (관리자용)
     *
     * @return 숨김된 게시글 리스트
     */
    @Query("SELECT p FROM ForumPost p WHERE p.hidden = true ORDER BY p.createdAt DESC")
    List<ForumPost> findHiddenPosts();

    /**
     * 게시글의 숨김 상태 업데이트
     *
     * @param postId 게시글 ID
     * @param hidden 숨김 여부
     */
    @Modifying
    @Query("UPDATE ForumPost p SET p.hidden = :hidden WHERE p.id = :postId")
    void updateHiddenStatus(@Param("postId") Integer postId, @Param("hidden") boolean hidden);

    /**
     * 게시글 삭제 상태 업데이트
     *
     * @param postId 게시글 ID
     * @param removedBy 삭제자 정보
     */
    @Modifying
    @Query("UPDATE ForumPost p SET p.removedBy = :removedBy, p.content = '[Removed]' WHERE p.id = :postId")
    void markPostAsRemoved(@Param("postId") Integer postId, @Param("removedBy") String removedBy);

    // 게시글 조회수 증가
    @Modifying
    @Query("UPDATE ForumPost p SET p.viewsCount = p.viewsCount + 1 WHERE p.id = :postId")
    void incrementViews(@Param("postId") Integer postId);

    // 게시글 제목으로 검색
    @Query("SELECT p FROM ForumPost p WHERE p.title LIKE %:keyword%")
    List<ForumPost> searchPostsByTitle(@Param("keyword") String keyword);
}
