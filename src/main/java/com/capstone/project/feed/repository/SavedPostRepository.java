package com.capstone.project.feed.repository;

import com.capstone.project.feed.entity.SavedPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavedPostRepository extends JpaRepository<SavedPost, Integer> {
    List<SavedPost> findByMemberId(Integer memberId); // 특정 회원의 저장된 게시물 조회
}
