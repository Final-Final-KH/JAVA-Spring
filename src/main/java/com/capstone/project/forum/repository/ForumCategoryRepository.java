package com.capstone.project.forum.repository;

import com.capstone.project.forum.entity.ForumCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ForumCategoryRepository extends JpaRepository<ForumCategory, Integer> {
    // 카테고리 제목으로 검색
    Optional<ForumCategory> findByTitle(String title);

    // 카테고리를 최신 수정 시간 순으로 정렬하여 조회
    @Query("SELECT c FROM ForumCategory c ORDER BY c.updatedAt DESC")
    List<ForumCategory> findAllCategoriesOrderedByUpdateTime();
}
