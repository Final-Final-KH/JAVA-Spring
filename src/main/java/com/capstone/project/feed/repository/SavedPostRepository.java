package com.capstone.project.feed.repository;

import com.capstone.project.feed.entity.SavedPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavedPostRepository extends JpaRepository<SavedPost, Integer> {
    boolean existsByMember_IdAndFeed_FeedId(Integer memberId, Integer feedId);
    // Check if a post is already saved
}
