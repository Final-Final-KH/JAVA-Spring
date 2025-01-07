package com.capstone.project.feed.service;

import com.capstone.project.feed.dto.request.FeedRequestDto;
import com.capstone.project.feed.dto.response.CommentResponseDto;
import com.capstone.project.feed.dto.response.FeedResponseDto;
import com.capstone.project.feed.entity.*;
import com.capstone.project.feed.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedRepository feedRepository;
    private final FeedCommentRepository feedCommentRepository;
    private final SavedPostRepository savedPostRepository;

    // 피드 생성
    @Transactional
    public FeedResponseDto createFeed(FeedRequestDto requestDto) {
        Feed feed = Feed.builder()
                .memberId(requestDto.getMemberId())
                .content(requestDto.getContent())
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .likesCount(0)
                .build();
        Feed savedFeed = feedRepository.save(feed);
        return mapToResponseDto(savedFeed);
    }

    // 피드 수정
    @Transactional
    public FeedResponseDto editFeed(Integer feedId, FeedRequestDto requestDto) {
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new IllegalArgumentException("Feed not found with ID: " + feedId));
        feed.setContent(requestDto.getContent());
        feed.setUpdatedAt(java.time.LocalDateTime.now());
        return mapToResponseDto(feedRepository.save(feed));
    }

    // 피드 삭제
    @Transactional
    public void deleteFeed(Integer feedId) {
        feedRepository.deleteById(feedId);
    }

    // 전체 피드 조회
    @Transactional(readOnly = true)
    public List<FeedResponseDto> getAllFeeds() {
        return feedRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    // 특정 피드 조회
    @Transactional(readOnly = true)
    public FeedResponseDto getFeedById(Integer feedId) {
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new IllegalArgumentException("Feed not found with ID: " + feedId));
        return mapToResponseDto(feed);
    }

    // 댓글 조회
    @Transactional(readOnly = true)
    public List<CommentResponseDto> getCommentsByFeedId(Integer feedId) {
        return feedCommentRepository.findAll()
                .stream()
                .filter(comment -> comment.getFeed().getFeedId().equals(feedId))
                .map(comment -> CommentResponseDto.builder()
                        .commentId(comment.getCommentId())
                        .memberId(comment.getMemberId())
                        .comment(comment.getComment())
                        .createdAt(comment.getCreatedAt())
                        .updatedAt(comment.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    // Helper 메서드: Feed -> FeedResponseDto 변환
    private FeedResponseDto mapToResponseDto(Feed feed) {
        return FeedResponseDto.builder()
                .feedId(feed.getFeedId())
                .memberId(feed.getMemberId())
                .content(feed.getContent())
                .createdAt(feed.getCreatedAt())
                .updatedAt(feed.getUpdatedAt())
                .likesCount(feed.getLikesCount())
                .repostedFrom(feed.getRepostedFrom() != null ? feed.getRepostedFrom().getFeedId() : null)
                .repostedFromContent(feed.getRepostedFrom() != null ? feed.getRepostedFrom().getContent() : null)
                .comments(feed.getComments()
                        .stream()
                        .map(comment -> CommentResponseDto.builder()
                                .commentId(comment.getCommentId())
                                .memberId(comment.getMemberId())
                                .comment(comment.getComment())
                                .createdAt(comment.getCreatedAt())
                                .updatedAt(comment.getUpdatedAt())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
