package com.capstone.project.forum.service;

import com.capstone.project.forum.dto.request.ForumPostRequestDto;
import com.capstone.project.forum.dto.response.ForumPostResponseDto;
import com.capstone.project.forum.dto.response.PaginationDto;
import com.capstone.project.forum.entity.ForumPost;
import com.capstone.project.forum.repository.ForumCategoryRepository;
import com.capstone.project.forum.repository.ForumPostRepository;
import com.capstone.project.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j // 로그 기록을 위한 어노테이션 추가
public class ForumPostService {
    private final ForumPostRepository postRepository;
    private final MemberRepository memberRepository;
    private final ForumCategoryRepository categoryRepository;

    // 특정 카테고리에 속한 게시글 가져오기 (페이지네이션 및 Sticky 우선)
    public PaginationDto<ForumPostResponseDto> getPostsByCategory(Integer categoryId, int page, int size) {
        log.info("Fetching paginated posts for category ID: {}", categoryId); // 카테고리별 게시글 조회 로그
        Pageable pageable = PageRequest.of(page, size);
        Page<ForumPost> postPage = postRepository.findPostsByCategory(categoryId, pageable);

        List<ForumPostResponseDto> postDtos = postPage.getContent()
                .stream()
                .map(post -> new ForumPostResponseDto(
                        post.getId(),
                        post.getTitle(),
                        post.getContent(),
                        post.getMember().getName(),
                        post.getSticky(),
                        post.getViewsCount(),
                        post.getLikesCount(),
                        post.getCreatedAt(),
                        post.getUpdatedAt()
                ))
                .collect(Collectors.toList());

        return new PaginationDto<>(
                postDtos,
                postPage.getNumber(),
                postPage.getTotalPages(),
                postPage.getTotalElements()
        );
    }

    // 게시글 생성
    @Transactional
    public ForumPostResponseDto createPost(ForumPostRequestDto requestDto) {
        log.info("Creating new post for category ID: {} by member ID: {}", requestDto.getCategoryId(), requestDto.getMemberId());

        var member = memberRepository.findById(requestDto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID: " + requestDto.getMemberId()));

        // Retrieve ForumCategory entity by ID
        var category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID: " + requestDto.getCategoryId()));

        // Build the post object
        var post = ForumPost.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .sticky(requestDto.getSticky())
                .viewsCount(0)
                .likesCount(0)
                .member(member) // Associate the post with the member
                .forumCategory(category) // Associate the post with the category
                .build();

        ForumPost savedPost = postRepository.save(post);
        return new ForumPostResponseDto(
                savedPost.getId(),
                savedPost.getTitle(),
                savedPost.getContent(),
                savedPost.getMember().getName(),
                savedPost.getSticky(),
                savedPost.getViewsCount(),
                savedPost.getLikesCount(),
                savedPost.getCreatedAt(),
                savedPost.getUpdatedAt()
        );
    }


    // 게시글 수정
    @Transactional
    public ForumPostResponseDto updatePost(Integer postId, ForumPostRequestDto requestDto) {
        log.info("Updating post ID: {}", postId);
        ForumPost post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID: " + postId));
        post.setTitle(requestDto.getTitle());
        post.setContent(requestDto.getContent());
        post.setSticky(requestDto.getSticky());
        post.setUpdatedAt(java.time.LocalDateTime.now());
        ForumPost updatedPost = postRepository.save(post);
        return new ForumPostResponseDto(
                updatedPost.getId(),
                updatedPost.getTitle(),
                updatedPost.getContent(),
                updatedPost.getMember().getName(),
                updatedPost.getSticky(),
                updatedPost.getViewsCount(),
                updatedPost.getLikesCount(),
                updatedPost.getCreatedAt(),
                updatedPost.getUpdatedAt()
        );
    }

    // 게시글 삭제
    @Transactional
    public void deletePost(Integer postId) {
        log.info("Deleting post ID: {}", postId);
        if (!postRepository.existsById(postId)) {
            throw new IllegalArgumentException("Invalid post ID: " + postId);
        }
        postRepository.deleteById(postId);
    }

    // 특정 게시글 조회
    public Optional<ForumPostResponseDto> getPostDetails(Integer postId) {
        log.info("Fetching details for post ID: {}", postId);
        return postRepository.findById(postId)
                .map(post -> new ForumPostResponseDto(
                        post.getId(),
                        post.getTitle(),
                        post.getContent(),
                        post.getMember().getName(),
                        post.getSticky(),
                        post.getViewsCount(),
                        post.getLikesCount(),
                        post.getCreatedAt(),
                        post.getUpdatedAt()
                ));
    }

    // 게시글 조회수 증가
    @Transactional
    public void incrementViewCount(Integer postId) {
        log.info("Incrementing view count for post ID: {}", postId);
        postRepository.incrementViews(postId);
    }
}
