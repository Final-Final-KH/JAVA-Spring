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

    // 게시글 조회수 증가
    @Modifying
    @Query("UPDATE ForumPost p SET p.viewsCount = p.viewsCount + 1 WHERE p.id = :postId")
    void incrementViews(@Param("postId") Integer postId);

    // 게시글 제목으로 검색
    @Query("SELECT p FROM ForumPost p WHERE p.title LIKE %:keyword%")
    List<ForumPost> searchPostsByTitle(@Param("keyword") String keyword);
}
