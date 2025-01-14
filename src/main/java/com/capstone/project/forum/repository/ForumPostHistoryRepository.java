package com.capstone.project.forum.repository;

import com.capstone.project.forum.entity.ForumPostHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * ForumPostHistory를 관리하기 위한 Repository 인터페이스
 */
public interface ForumPostHistoryRepository extends JpaRepository<ForumPostHistory, Long> {

    /**
     * 게시글 ID로 삭제된 게시글 이력을 조회
     *
     * @param postId 게시글 ID
     * @return 해당 게시글 ID의 삭제된 게시글 이력 Optional
     */
    Optional<ForumPostHistory> findByPostId(Integer postId);

}
