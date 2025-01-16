package com.capstone.project.forum.service;

import com.capstone.project.forum.dto.request.ForumPostRequestDto;
import com.capstone.project.forum.dto.response.ForumPostResponseDto;
import com.capstone.project.forum.dto.response.PaginationDto;
import com.capstone.project.forum.entity.*;
import com.capstone.project.forum.repository.*;
import com.capstone.project.member.repository.MemberRepository;
import com.capstone.project.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final ForumPostRepository forumPostRepository;
    private final ForumPostCommentRepository commentRepository; // 댓글 데이터베이스 접근
    private final ForumPostCommentHistoryRepository commentHistoryRepository;
    private final ForumPostHistoryRepository historyRepository; // 삭제 이력 관련 데이터베이스 접근
    private final PostReportRepository postReportRepository;
    private final MemberService memberService;

    private static final int REPORT_THRESHOLD = 10; // 신고 누적 기준값

    /**
     * 특정 카테고리에 속한 게시글을 페이지네이션하여 가져오는 메서드
     *
     * @param categoryId 카테고리 ID
     * @param page 페이지 번호
     * @param size 페이지당 게시글 수
     * @return PaginationDto<ForumPostResponseDto> 페이지네이션된 게시글 응답 DTO
     */
    public PaginationDto<ForumPostResponseDto> getPostsByCategory(Integer categoryId, int page, int size) {
        log.info("Fetching posts for category ID: {}, page: {}, size: {}", categoryId, page, size);

        Pageable pageable = PageRequest.of(page, size); // 페이지 요청 객체 생성
        Page<ForumPost> postPage = postRepository.findPostsByCategory(categoryId, pageable); // DB에서 게시글 조회

        // 게시글 리스트를 DTO로 변환
        List<ForumPostResponseDto> postDtos = postPage.getContent()
                .stream()
                .map(post -> ForumPostResponseDto.builder()
                        .id(post.getId())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .authorName(post.getMember().getName())
                        .sticky(post.getSticky())
                        .viewsCount(post.getViewsCount())
                        .likesCount(post.getLikesCount())
                        .hidden(post.getHidden())
                        .removedBy(post.getRemovedBy())
                        .createdAt(post.getCreatedAt())
                        .updatedAt(post.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());

        return new PaginationDto<>(postDtos, postPage.getNumber(), postPage.getTotalPages(), postPage.getTotalElements());
    }

    /**
     * 게시글 생성
     *
     * @param requestDto 게시글 요청 DTO
     * @return 생성된 게시글 응답 DTO
     */
    @Transactional
    public ForumPostResponseDto createPost(ForumPostRequestDto requestDto) {
        log.info("Creating post with title: {}", requestDto.getTitle());

        // Validate member ID
        if (requestDto.getMemberId() == null) {
            throw new IllegalArgumentException("Member ID must not be null.");
        }

        // Validate category ID
        if (requestDto.getCategoryId() == null) {
            throw new IllegalArgumentException("Category ID must not be null.");
        }

        // Retrieve member and category
        var member = memberRepository.findById(requestDto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID: " + requestDto.getMemberId()));

        var category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID: " + requestDto.getCategoryId()));

        // Build the post entity
        var post = ForumPost.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .sticky(requestDto.getSticky() != null ? requestDto.getSticky() : false) // Default to false if not provided
                .viewsCount(0)
                .likesCount(0)
                .fileUrls(requestDto.getFileUrls() != null ? requestDto.getFileUrls() : new ArrayList<>())
                .hidden(false)
                .member(member)
                .forumCategory(category)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();


        // Save the post
        var savedPost = postRepository.save(post);

        // Return the response DTO
        return ForumPostResponseDto.builder()
                .id(savedPost.getId())
                .title(savedPost.getTitle())
                .content(savedPost.getContent())
                .authorName(member.getName())
                .sticky(savedPost.getSticky())
                .viewsCount(savedPost.getViewsCount())
                .likesCount(savedPost.getLikesCount())
                .fileUrls(savedPost.getFileUrls())
                .hidden(savedPost.getHidden())
                .removedBy(savedPost.getRemovedBy())
                .createdAt(savedPost.getCreatedAt())
                .updatedAt(savedPost.getUpdatedAt())
                .build();
    }





    /**
     * 게시글 수정
     *
     * @param postId 수정할 게시글 ID
     * @param requestDto 수정 요청 DTO
     * @param loggedInMemberId 요청 사용자 ID
     * @param isAdmin 관리자 여부
     * @return 수정된 게시글 응답 DTO
     */
    @Transactional
    public ForumPostResponseDto updatePost(Integer postId, ForumPostRequestDto requestDto, Integer loggedInMemberId, boolean isAdmin) {
        log.info("Updating post with ID: {}", postId);

        var post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with ID: " + postId));

        if (!isAdmin && !post.getMember().getId().equals(loggedInMemberId)) {
            throw new SecurityException("Not authorized to edit this post");
        }

        post.setTitle(requestDto.getTitle());
        post.setContent(requestDto.getContent());
        post.setSticky(requestDto.getSticky());
        post.setFileUrls(requestDto.getFileUrls());
        post.setUpdatedAt(LocalDateTime.now());

        var updatedPost = postRepository.save(post);

        return ForumPostResponseDto.builder()
                .id(updatedPost.getId())
                .title(updatedPost.getTitle())
                .content(updatedPost.getContent())
                .authorName(post.getMember().getName())
                .sticky(updatedPost.getSticky())
                .viewsCount(updatedPost.getViewsCount())
                .likesCount(updatedPost.getLikesCount())
                .fileUrls(updatedPost.getFileUrls())
                .hidden(updatedPost.getHidden())
                .removedBy(updatedPost.getRemovedBy())
                .createdAt(updatedPost.getCreatedAt())
                .updatedAt(updatedPost.getUpdatedAt())
                .build();
    }





    /**
     * 게시글 숨김 처리
     *
     * @param postId 숨길 게시글 ID
     */
    @Transactional
    public void hidePost(Integer postId) {
        log.info("Hiding post ID: {}", postId);
        ForumPost post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID: " + postId));
        post.setHidden(true);
        postRepository.save(post);
        log.info("Post ID: {} has been marked as hidden.", postId);
    }

    /**
     * 게시글 복구
     * 삭제된 게시글을 삭제 이력을 사용하여 복구
     *
     * @param postId 복구할 게시글 ID
     */
    @Transactional
    public void restorePost(Integer postId) {
        log.info("Restoring post ID: {}", postId);

        // 게시글 조회
        ForumPost post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID: " + postId));

        // 삭제된 상태 확인
        if (!"[Deleted]".equals(post.getTitle())) {
            throw new IllegalStateException("The post is not in a deleted state.");
        }

        // 삭제 이력 조회
        ForumPostHistory history = historyRepository.findTopByPostIdOrderByDeletedAtDesc(postId)
                .orElseThrow(() -> new IllegalArgumentException("No history found for post ID: " + postId));

        // 게시글 복구
        post.setTitle(history.getTitle()); // 제목 복구
        post.setContent(history.getContent()); // 내용 복구
        post.setHidden(false); // 숨김 해제
        post.setRemovedBy(null); // 삭제자 정보 초기화
        postRepository.save(post); // 저장

        log.info("Post ID: {} successfully restored.", postId);
    }



    /**
     * 게시글 삭제
     * 게시글과 해당 게시글의 댓글을 삭제 상태로 표시하며, 삭제 이력을 기록합니다.
     * 댓글 삭제는 cascadeComments 플래그에 따라 조건적으로 수행됩니다.
     *
     * @param postId         삭제할 게시글 ID
     * @param loggedInMemberId 삭제 요청을 보낸 사용자 ID
     * @param cascadeComments 댓글을 삭제할지 여부를 결정하는 플래그
     * @param removedBy      삭제를 수행한 사용자 정보 (ADMIN 또는 작성자 이름)
     * @throws IllegalArgumentException 유효하지 않은 게시글 ID 또는 권한이 없는 사용자일 경우 예외 발생
     * @throws AccessDeniedException    삭제 권한이 없는 사용자가 요청할 경우 예외 발생
     */
    @Transactional
    public void deletePost(Integer postId, Integer loggedInMemberId, boolean cascadeComments, String removedBy) {
        log.info("Deleting post ID: {} by member ID: {}", postId, loggedInMemberId);

        // 1. 게시글 조회
        ForumPost post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID: " + postId));

        // 2. 삭제 권한 확인
        boolean isAdmin = memberService.isAdmin(loggedInMemberId);
        if (!post.getMember().getId().equals(loggedInMemberId) && !isAdmin) {
            throw new AccessDeniedException("You are not allowed to delete this post.");
        }

        // 3. cascadeComments 플래그에 따라 댓글 처리
        if (cascadeComments) {
            // 댓글 백업 및 삭제
            for (ForumPostComment comment : post.getComments()) {
                ForumPostCommentHistory history = ForumPostCommentHistory.builder()
                        .commentId(comment.getId()) // 댓글 ID
                        .content(comment.getContent()) // 댓글 내용
                        .authorName(comment.getMember().getName()) // 작성자 이름
                        .deletedAt(LocalDateTime.now()) // 삭제 시간
                        .build();
                commentHistoryRepository.save(history); // 댓글 이력 저장
                commentRepository.delete(comment); // 댓글 삭제
                log.info("Comment ID: {} deleted and backed up to history.", comment.getId());
            }
        } else {
            // 댓글 백업만 수행 (삭제하지 않음)
            for (ForumPostComment comment : post.getComments()) {
                ForumPostCommentHistory history = ForumPostCommentHistory.builder()
                        .commentId(comment.getId()) // 댓글 ID
                        .content(comment.getContent()) // 댓글 내용
                        .authorName(comment.getMember().getName()) // 작성자 이름
                        .deletedAt(LocalDateTime.now()) // 삭제 시간
                        .build();
                commentHistoryRepository.save(history); // 댓글 이력 저장
                log.info("Comment ID: {} backed up to history without deletion.", comment.getId());
            }
        }

        // 4. 게시글 삭제 이력 저장
        ForumPostHistory postHistory = ForumPostHistory.builder()
                .postId(post.getId()) // 게시글 ID
                .title(post.getTitle()) // 삭제 전 게시글 제목
                .content(post.getContent()) // 삭제 전 게시글 내용
                .authorName(post.getMember().getName()) // 작성자 이름
                .deletedAt(LocalDateTime.now()) // 삭제 시간
                .build();
        historyRepository.save(postHistory); // 게시글 이력 저장
        log.info("Post ID: {} backed up to history.", postId);

        // 5. 게시글 상태를 삭제됨으로 업데이트
        post.setTitle("[Deleted]"); // 제목을 "[Deleted]"로 변경
        post.setContent("This post has been deleted."); // 내용을 삭제됨으로 표시
        post.setRemovedBy(isAdmin ? "ADMIN" : post.getMember().getName()); // 삭제자 정보 설정
        post.setHidden(true); // 게시글 숨김 처리
        postRepository.save(post); // 업데이트된 게시글 저장

        log.info("Post ID: {} marked as deleted.", postId);
    }



//    기능 중복 같기에 일단 주석처리. restorePost로 충분하다고 판단되면 추후 삭제.
//    /**
//     * 게시글 복구
//     * 저장된 이력 데이터를 사용하여 삭제된 게시글을 복구
//     *
//     * @param postId 복구할 게시글 ID
//     */
//    @Transactional
//    public void undeletePost(Integer postId) {
//        log.info("Restoring post ID: {}", postId);
//
//        // 삭제 이력 조회
//        ForumPostHistory history = historyRepository.findByPostId(postId)
//                .orElseThrow(() -> new IllegalArgumentException("No history found for post ID: " + postId));
//
//        // 게시글 복구
//        ForumPost post = postRepository.findById(postId)
//                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID: " + postId));
//        post.setTitle(history.getTitle());
//        post.setContent(history.getContent());
//        post.setFileUrls(history.getFileUrls()); // 파일 URL 복구
//        postRepository.save(post);
//
//        // 복구 완료 후 삭제 이력 제거
//        historyRepository.delete(history);
//    }


    // canEditPost와 canDeletePost는 수정 및 삭제 권한을 사전에 확인하는 API
    // 이는 프론트엔드가 수정/삭제 요청 전에 해당 사용자의 권한을 확인하기 위해 사용
    // 데이터 수정 및 삭제 작업 (updatePost, deletePost)에서는 여전히 권한을 검증하지만, 이 메서드는 권한 확인 전용
    /**
     * 게시글 수정 권한 확인
     *
     * @param postId 게시글 ID
     * @param loggedInMemberId 요청 사용자 ID
     * @return 게시글을 수정할 권한이 있는지 여부
     */
    public boolean canEditPost(Integer postId, Integer loggedInMemberId) {
        log.info("Checking edit permissions for post ID: {} by member ID: {}", postId, loggedInMemberId);

        // 게시글 조회
        var post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID: " + postId));

        // 수정 권한 확인
        boolean canEdit = post.getMember().getId().equals(loggedInMemberId);
        log.info("Member ID: {} has edit permissions: {}", loggedInMemberId, canEdit);
        return canEdit;
    }

    /**
     * 게시글 삭제 권한 확인
     *
     * @param postId 게시글 ID
     * @param loggedInMemberId 요청 사용자 ID
     * @return 게시글을 삭제할 권한이 있는지 여부
     */
    public boolean canDeletePost(Integer postId, Integer loggedInMemberId) {
        log.info("Checking delete permissions for post ID: {} by member ID: {}", postId, loggedInMemberId);

        // 게시글 조회
        var post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID: " + postId));

        // 삭제 권한 확인
        boolean canDelete = post.getMember().getId().equals(loggedInMemberId);
        log.info("Member ID: {} has delete permissions: {}", loggedInMemberId, canDelete);
        return canDelete;
    }


    /**
     * 게시글 상세 조회
     *
     * @param postId 게시글 ID
     * @return Optional<ForumPostResponseDto> 게시글 상세 정보
     */
    public Optional<ForumPostResponseDto> getPostDetails(Integer postId) {
        log.info("Fetching details for post ID: {}", postId);

        return postRepository.findById(postId)
                .map(post -> ForumPostResponseDto.builder()
                        .id(post.getId()) // 게시글 ID
                        .title(post.getTitle()) // 게시글 제목
                        .content(post.getContent()) // 게시글 내용
                        .authorName(post.getMember().getName()) // 작성자 이름
                        .sticky(post.getSticky()) // 상단 고정 여부
                        .viewsCount(post.getViewsCount()) // 조회수
                        .likesCount(post.getLikesCount()) // 좋아요 수
                        .hidden(post.getHidden()) // 숨김 상태
                        .removedBy(post.getRemovedBy()) // 삭제자 정보
                        .createdAt(post.getCreatedAt()) // 생성 시간
                        .updatedAt(post.getUpdatedAt()) // 수정 시간
                        .build());
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
     *
     * @param quotingMemberId 인용하는 회원 ID
     * @param quotedPostId    인용할 게시글 ID
     * @param commentContent  추가 댓글 내용
     * @return 생성된 인용 게시글의 응답 DTO
     */
    @Transactional
    public ForumPostResponseDto quotePost(Integer quotingMemberId, Integer quotedPostId, String commentContent) {
        log.info("Quoting post ID: {} by member ID: {}", quotedPostId, quotingMemberId);

        // 인용 대상 게시글 조회
        ForumPost quotedPost = postRepository.findById(quotedPostId)
                .orElseThrow(() -> new IllegalArgumentException("Quoted post not found"));

        // 숨김 또는 삭제된 게시글 검증
        if (quotedPost.getHidden() || quotedPost.getRemovedBy() != null) {
            throw new IllegalStateException("Cannot quote a hidden or deleted post.");
        }

        // 인용 내용 생성
        String quotedText = String.format(
                "<blockquote><strong>%s</strong> wrote:<br><em>%s</em></blockquote><p>%s</p>",
                quotedPost.getMember().getName(), // 인용한 작성자 이름
                quotedPost.getContent(), // 인용한 게시글 내용
                commentContent // 추가 내용
        );

        // 인용 게시글 작성자 확인
        var quotingMember = memberRepository.findById(quotingMemberId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid quoting member ID"));

        // 새 인용 게시글 생성
        var quotedPostEntity = ForumPost.builder()
                .title("Reply to: " + quotedPost.getTitle()) // 제목 설정
                .content(quotedText) // 내용 설정
                .member(quotingMember) // 작성자 매핑
                .forumCategory(quotedPost.getForumCategory()) // 카테고리 매핑
                .createdAt(LocalDateTime.now()) // 생성 시간 설정
                .updatedAt(LocalDateTime.now()) // 수정 시간 초기화
                .build();

        // DB에 저장
        ForumPost savedPost = postRepository.save(quotedPostEntity);

        // 응답 DTO 생성
        return ForumPostResponseDto.builder()
                .id(savedPost.getId())
                .title(savedPost.getTitle())
                .content(savedPost.getContent())
                .authorName(savedPost.getMember().getName())
                .sticky(savedPost.getSticky())
                .viewsCount(savedPost.getViewsCount())
                .likesCount(savedPost.getLikesCount())
                .hidden(savedPost.getHidden())
                .removedBy(savedPost.getRemovedBy())
                .createdAt(savedPost.getCreatedAt())
                .updatedAt(savedPost.getUpdatedAt())
                .build();
    }

    /**
     * 게시글 신고 처리
     *
     * @param postId 신고 대상 게시글 ID
     * @param reporterId 신고자 ID
     * @param reason 신고 사유
     */
    @Transactional
    public void reportPost(Integer postId, Integer reporterId, String reason) {
        log.info("Reporting post ID: {} by reporter ID: {}", postId, reporterId);

        // 게시글 존재 여부 확인
        ForumPost post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID: " + postId));

        // 중복 신고 방지
        boolean alreadyReported = postReportRepository.existsByPostIdAndReporterId(postId, reporterId);
        if (alreadyReported) {
            throw new IllegalArgumentException("You have already reported this post.");
        }

        // 신고 엔티티 생성 및 저장
        PostReport report = PostReport.builder()
                .forumPost(post)
                .reporterId(reporterId)
                .reason(reason)
                .createdAt(LocalDateTime.now())
                .build();
        postReportRepository.save(report);

        // 신고 누적 확인 및 게시글 숨김 처리
        long reportCount = postReportRepository.countByPostId(postId);
        log.info("Post ID: {} has {} reports.", postId, reportCount);

        if (reportCount >= REPORT_THRESHOLD) {
            post.setHidden(true); // 신고 임계값 초과 시 게시글 숨김 처리
            postRepository.save(post);
            log.info("Post ID: {} has been hidden due to exceeding report threshold.", postId);
        }
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
