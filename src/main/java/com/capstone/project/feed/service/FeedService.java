package com.capstone.project.feed.service;

import com.capstone.project.feed.dto.response.FeedResponseDto;
import com.capstone.project.feed.entity.Feed;
import com.capstone.project.feed.entity.SavedPost;
import com.capstone.project.feed.repository.FeedRepository;
import com.capstone.project.feed.repository.SavedPostRepository;
import com.capstone.project.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final FeedRepository feedRepository;
    private final SavedPostRepository savedPostRepository;

    // Edit a post / 게시물 수정
    @Transactional
    public FeedResponseDto editPost(Integer feedId, Integer memberId, String newContent) {
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new IllegalArgumentException("Feed not found / 게시물을 찾을 수 없습니다."));

        if (!feed.getMember().getId().equals(memberId)) {
            throw new SecurityException("You do not have permission to edit this post / 이 게시물을 수정할 권한이 없습니다.");
        }

        feed.setContent(newContent);
        feed.setUpdatedAt(LocalDateTime.now());
        feedRepository.save(feed);

        return new FeedResponseDto(
                feed.getFeedId(),
                feed.getMember().getId(),
                feed.getContent(),
                feed.getCreatedAt(),
                feed.getUpdatedAt(),
                feed.getLikesCount(),
                null // For repostedFrom, if implemented later / 나중에 리포스트 기능 추가 시 변경
        );
    }

    // Delete a post / 게시물 삭제
    @Transactional
    public void deletePost(Integer feedId, Integer memberId) {
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new IllegalArgumentException("Feed not found / 게시물을 찾을 수 없습니다."));

        if (!feed.getMember().getId().equals(memberId)) {
            throw new SecurityException("You do not have permission to delete this post / 이 게시물을 삭제할 권한이 없습니다.");
        }

        feedRepository.delete(feed);
    }

    // Save a post / 게시물 저장
    @Transactional
    public void savePost(Integer memberId, Integer feedId) {
        if (savedPostRepository.existsByMember_IdAndFeed_FeedId(memberId, feedId)) {
            throw new IllegalStateException("Post is already saved / 이미 저장된 게시물입니다.");
        }

        SavedPost savedPost = new SavedPost();
        savedPost.setMember(new Member(memberId)); // Assume constructor for Member with only ID / ID만 설정된 Member 생성자 사용
        savedPost.setFeed(new Feed(feedId));       // Assume constructor for Feed with only ID / ID만 설정된 Feed 생성자 사용
        savedPost.setSavedAt(LocalDateTime.now());

        savedPostRepository.save(savedPost);
    }
}
