package com.capstone.project.feed.repository;

import com.capstone.project.feed.entity.SavedPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavedPostRepository extends JpaRepository<SavedPost, Integer> {
    //이 부분 필요없으면 밑 boolean은 쳐낸다.
//    boolean existsByMember_IdAndFeed_FeedId(Integer memberId, Integer feedId);
}
