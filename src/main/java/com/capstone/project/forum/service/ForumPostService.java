package com.capstone.project.forum.service;

import com.capstone.project.forum.dto.request.ForumPostRequestDto;
import com.capstone.project.forum.dto.response.ForumPostResponseDto;
import com.capstone.project.forum.dto.response.PaginationDto;
import com.capstone.project.forum.entity.ForumPost;
import com.capstone.project.forum.entity.ForumPostComment;
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
    private final ForumPostRepository postRepository; // 게시글 관련 데이터베이스 접근
    private final MemberRepository memberRepository; // 회원 데이터베이스 접근
    private final ForumCategoryRepository categoryRepository; // 카테고리 데이터베이스 접근
    private final ForumPostCommentRepository commentRepository; // 댓글 데이터베이스 접근

    /**
     * 특정 카테고리에 속한 게시글 가져오기 (페이지네이션 지원)
     * @param categoryId 카테고리 ID
     * @param page 페이지 번호
     * @param size 페이지당 게시글 개수
     * @return PaginationDto<ForumPostResponseDto>
     */
    public PaginationDto<ForumPostResponseDto> getPostsByCategory(Integer categoryId, int page, int size) {
        log.info("Fetching posts for category ID: {}, page: {}, size: {}", categoryId, page, size);

        Pageable pageable = PageRequest.of(page, size); // 페이지 요청 객체 생성
        Page<ForumPost> postPage = postRepository.findPostsByCategory(categoryId, pageable); // DB에서 게시글 조회

        // 게시글 리스트를 DTO로 변환
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
                .collect(Collectors.toList()); // Stream 결과를 리스트로 변환

        return new PaginationDto<>(postDtos, postPage.getNumber(), postPage.getTotalPages(), postPage.getTotalElements());
    }

    /**
     * 게시글 생성
     * @param requestDto 게시글 생성 요청 DTO
     * @return 생성된 게시글 응답 DTO
     */
    @Transactional
    public ForumPostResponseDto createPost(ForumPostRequestDto requestDto) {
        log.info("Creating new post in category ID: {} by member ID: {}", requestDto.getCategoryId(), requestDto.getMemberId());

        // 작성자 조회
        var member = memberRepository.findById(requestDto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID: " + requestDto.getMemberId()));

        // 카테고리 조회
        var category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID: " + requestDto.getCategoryId()));

        // 게시글 엔티티 생성
        var post = ForumPost.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .sticky(requestDto.getSticky())
                .viewsCount(0) // 초기 조회수 0
                .likesCount(0) // 초기 좋아요 수 0
                .member(member) // 작성자 연결
                .forumCategory(category) // 카테고리 연결
                .createdAt(java.time.LocalDateTime.now()) // 생성 시간
                .updatedAt(java.time.LocalDateTime.now()) // 수정 시간 초기화
                .build();

        ForumPost savedPost = postRepository.save(post); // DB에 저장

        // 응답 DTO 생성 및 반환
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

    /**
     * 게시글 수정
     * @param postId 수정할 게시글 ID
     * @param requestDto 수정 요청 DTO
     * @return 수정된 게시글 응답 DTO
     */
    @Transactional
    public ForumPostResponseDto updatePost(Integer postId, ForumPostRequestDto requestDto) {
        log.info("Updating post ID: {}", postId);

        // 게시글 조회
        ForumPost post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID: " + postId));

        // 게시글 내용 수정
        post.setTitle(requestDto.getTitle());
        post.setContent(requestDto.getContent());
        post.setSticky(requestDto.getSticky());
        post.setUpdatedAt(java.time.LocalDateTime.now());

        ForumPost updatedPost = postRepository.save(post); // 저장

        // 응답 DTO 생성 및 반환
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

    /**
            * 게시글 삭제 (삭제 상태로 변경)
     *
             * @param postId         삭제할 게시글 ID
     * @param cascadeComments 댓글도 함께 "삭제됨" 상태로 처리할지 여부
     * @param removedBy      삭제자 정보 (예: "OP", "ADMIN")
     */
    @Transactional
    public void deletePost(Integer postId, boolean cascadeComments, String removedBy) {
        log.info("Marking post ID: {} as removed by: {}", postId, removedBy);

        // 삭제할 게시글 조회
        ForumPost post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID: " + postId));

        // 게시글을 삭제된 상태로 표시
        post.setTitle("[Removed]");
        post.setContent("This post has been removed.");
        post.setRemovedBy(removedBy); // 삭제자 정보 저장
        postRepository.save(post); // 변경사항 저장

        if (cascadeComments) {
            // 해당 게시글에 연결된 모든 댓글을 삭제된 상태로 표시
            List<ForumPostComment> comments = commentRepository.findCommentsByPostId(postId);
            for (ForumPostComment comment : comments) {
                comment.setContent("This comment is linked to a removed post.");
                comment.setRemovedBy("SYSTEM"); // 시스템에 의해 처리된 것으로 설정
                commentRepository.save(comment);
            }
            log.info("All comments for post ID: {} marked as removed.", postId);
        }
    }

    /**
     * 게시글 조회
     * @param postId 게시글 ID
     * @return Optional<ForumPostResponseDto>
     */
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

    /**
     * 게시글 조회수 증가
     * @param postId 게시글 ID
     */
    @Transactional
    public void incrementViewCount(Integer postId) {
        log.info("Incrementing view count for post ID: {}", postId);
        postRepository.incrementViews(postId);
    }

    /**
     * 게시글 인용
     * @param quotingMemberId 인용하는 회원 ID
     * @param quotedPostId 인용할 게시글 ID
     * @param commentContent 추가 댓글 내용
     * @return 생성된 인용 게시글의 응답 DTO
     */
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

        // 새로운 인용 게시글 생성
        var member = memberRepository.findById(quotingMemberId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid quoting member ID"));

        var quotedPostEntity = ForumPost.builder()
                .title("Reply to: " + quotedPost.getTitle())
                .content(quotedText)
                .member(member)
                .forumCategory(quotedPost.getForumCategory())
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();

        ForumPost savedPost = postRepository.save(quotedPostEntity);

        // 응답 DTO 생성 및 반환
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

    /**
     * 파일 저장
     * @param file 업로드할 파일
     * @return 저장된 파일 URL
     */
    private String saveFile(MultipartFile file) {
        log.info("Saving file: {}", file.getOriginalFilename());
        return "http://localhost/files/" + file.getOriginalFilename(); // 로컬 저장 예제
    }
}
