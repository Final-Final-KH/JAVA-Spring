package com.capstone.project.forum.service;

import com.capstone.project.forum.dto.request.ForumPostRequestDto;
import com.capstone.project.forum.dto.response.ForumPostResponseDto;
import com.capstone.project.forum.dto.response.PaginationDto;
import com.capstone.project.forum.entity.ForumPost;
import com.capstone.project.forum.repository.ForumCategoryRepository;
import com.capstone.project.forum.repository.ForumPostCommentRepository;
import com.capstone.project.forum.repository.ForumPostRepository;
import com.capstone.project.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private final ForumPostCommentRepository commentRepository;

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

    // 게시글 삭제 - 댓글 삭제 옵션 포함
    @Transactional
    public void deletePost(Integer postId, boolean cascadeComments) {
        log.info("Deleting post ID: {}, cascade comments: {}", postId, cascadeComments);

        ForumPost post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID: " + postId));

        if (cascadeComments) {
            // 댓글 삭제
            commentRepository.deleteByForumPostId(postId);
            log.info("All comments for post ID: {} have been deleted.", postId);
        } else {
            // 게시글만 제거 표시
            post.setTitle("[Deleted]");
            post.setContent("This post has been removed.");
            postRepository.save(post);
            log.info("Post ID: {} marked as removed.", postId);
        }
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

    // 파일 업로드
    @Transactional
    public String uploadFile(Integer postId, MultipartFile file) {
        log.info("Uploading file for post ID: {}", postId);

        // 게시글 확인
        ForumPost post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID: " + postId));

        // 파일 저장 로직 구현 (e.g., AWS S3, local storage)
        String fileUrl = saveFile(file);

        // 게시글에 파일 URL 추가 (파일 저장 로직 필요)
        post.addFileUrl(fileUrl);
        postRepository.save(post);

        return fileUrl;
    }

    // 게시글 인용 (예시 이미지의 포맷 적용)
    @Transactional
    public ForumPostResponseDto quotePost(Integer quotingMemberId, Integer quotedPostId, String commentContent) {
        log.info("Quoting post ID: {} by member ID: {}", quotedPostId, quotingMemberId);

        ForumPost quotedPost = postRepository.findById(quotedPostId)
                .orElseThrow(() -> new IllegalArgumentException("Quoted post not found"));

        // 인용 내용 처리
        String quotedText = String.format(
                "<blockquote><strong>%s</strong> wrote:<br><em>%s</em></blockquote><p>%s</p>",
                quotedPost.getMember().getName(),
                quotedPost.getContent(),
                commentContent
        );

        // 게시글 객체 생성
        var member = memberRepository.findById(quotingMemberId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid quoting member ID"));

        var quotedPostEntity = ForumPost.builder()
                .title("Reply to: " + quotedPost.getTitle())
                .content(quotedText)
                .member(member)
                .forumCategory(quotedPost.getForumCategory())
                .build();

        ForumPost savedPost = postRepository.save(quotedPostEntity);

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

    private String saveFile(MultipartFile file) {
        // 파일 저장 로직 (로컬 저장, S3 업로드 등 구현 필요)
        log.info("Saving file: {}", file.getOriginalFilename());
        // 예시: 로컬 파일 저장 로직
        return "http://localhost/files/" + file.getOriginalFilename();
    }
}
