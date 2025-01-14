package com.capstone.project.forum.repository;

import com.capstone.project.forum.entity.ForumCategory;
import com.capstone.project.forum.entity.ForumPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ForumCategoryRepository
 * 포럼 카테고리와 관련된 데이터베이스 작업을 처리하는 Repository
 */
@Repository
public interface ForumCategoryRepository extends JpaRepository<ForumCategory, Integer> {

    /**
     * 카테고리 제목으로 검색
     * @param title 카테고리 제목
     * @return 주어진 제목과 일치하는 ForumCategory(Optional)
     */
    Optional<ForumCategory> findByTitle(String title);

    /**
     * 모든 카테고리를 최신 수정 시간 순으로 정렬하여 조회
     * @return ForumCategory의 리스트 (최신 수정 시간 순 정렬)
     */
    @Query("SELECT c FROM ForumCategory c ORDER BY c.updatedAt DESC")
    List<ForumCategory> findAllCategoriesOrderedByUpdateTime();

    /**
     * 특정 카테고리에 포함된 게시글을 최신 순으로 조회
     * @param categoryId 조회할 카테고리 ID
     * @return ForumPost의 리스트 (최신 생성 시간 순 정렬)
     */
    @Query("SELECT p FROM ForumPost p WHERE p.forumCategory.id = :categoryId ORDER BY p.createdAt DESC")
    List<ForumPost> findLatestPostsByCategoryId(@Param("categoryId") Integer categoryId);

    /**
     * 특정 카테고리에 포함된 게시글 수 가져오기
     * @param categoryId 조회할 카테고리 ID
     * @return 해당 카테고리에 포함된 게시글 수 (long 타입)
     */
    @Query("SELECT COUNT(p) FROM ForumPost p WHERE p.forumCategory.id = :categoryId")
    long countPostsByCategoryId(@Param("categoryId") Integer categoryId);
}
