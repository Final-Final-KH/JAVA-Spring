package com.capstone.project.forum.service;

import com.capstone.project.forum.dto.request.ForumPostRequestDto;
import com.capstone.project.forum.dto.response.ForumPostResponseDto;
import com.capstone.project.forum.dto.response.PaginationDto;
import com.capstone.project.forum.entity.ForumPost;
import com.capstone.project.forum.entity.ForumPostComment;
import com.capstone.project.forum.entity.ForumPostHistory;
import com.capstone.project.forum.repository.ForumCategoryRepository;
import com.capstone.project.forum.repository.ForumPostCommentRepository;
import com.capstone.project.forum.repository.ForumPostHistoryRepository;
import com.capstone.project.forum.repository.ForumPostRepository;
import com.capstone.project.member.repository.MemberRepository;
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
    private final ForumPostCommentRepository commentRepository; // 댓글 데이터베이스 접근
    private final ForumPostHistoryRepository historyRepository; // 삭제 이력 관련 데이터베이스 접근

    /**
     * 특정 카테고리에 속한 게시글 가져오기 (페이지네이션 지원)
     * @param categoryId 카테고리 ID
     * @param page 페이지 번호
     * @param size 페이지당 게시글 개수
     * @return PaginationDto<ForumPostResponseDto>
     */
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
     * 새로운 게시글을 생성하는 메서드
     *
     * @param requestDto 게시글 생성 요청 DTO
     * @return ForumPostResponseDto 생성된 게시글의 응답 DTO
     */
    @Transactional
    public ForumPostResponseDto createPost(ForumPostRequestDto requestDto) {
        log.info("Creating new post in category ID: {}", requestDto.getCategoryId());

        // 작성자 정보 조회
        var member = memberRepository.findById(requestDto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID: " + requestDto.getMemberId()));

        // 카테고리 정보 조회
        var category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID: " + requestDto.getCategoryId()));

        // 게시글 엔티티 생성
        var post = ForumPost.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .sticky(requestDto.getSticky())
                .viewsCount(0)
                .likesCount(0)
                .hidden(false)
                .member(member)
                .forumCategory(category)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        var savedPost = postRepository.save(post); // DB에 저장

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
     * 게시글 수정
     *
     * @param postId       수정할 게시글 ID
     * @param requestDto   수정 요청 DTO
     * @param loggedInMemberId 수정 요청 사용자 ID
     * @param isAdmin      관리자 여부
     * @return 수정된 게시글 응답 DTO
     */
    @Transactional
    public ForumPostResponseDto updatePost(Integer postId, ForumPostRequestDto requestDto, Integer loggedInMemberId, boolean isAdmin) {
        log.info("Updating post ID: {} by member ID: {}, isAdmin: {}", postId, loggedInMemberId, isAdmin);

        // 게시글 조회
        var post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID: " + postId));

        // 권한 검증
        if (!post.getMember().getId().equals(loggedInMemberId) && !isAdmin) {
            throw new AccessDeniedException("You are not allowed to edit this post.");
        }

        // 게시글 수정
        post.setTitle(requestDto.getTitle()); // 제목 업데이트
        post.setContent(requestDto.getContent()); // 내용 업데이트
        post.setSticky(requestDto.getSticky()); // 상단 고정 여부 업데이트
        post.setUpdatedAt(LocalDateTime.now()); // 수정 시간 갱신

        // 업데이트된 게시글 저장
        var updatedPost = postRepository.save(post);

        // 응답 DTO 생성 및 반환
        return ForumPostResponseDto.builder()
                .id(updatedPost.getId())
                .title(updatedPost.getTitle())
                .content(updatedPost.getContent())
                .authorName(updatedPost.getMember().getName())
                .sticky(updatedPost.getSticky())
                .viewsCount(updatedPost.getViewsCount())
                .likesCount(updatedPost.getLikesCount())
                .hidden(updatedPost.getHidden())
                .removedBy(updatedPost.getRemovedBy())
                .createdAt(updatedPost.getCreatedAt())
                .updatedAt(updatedPost.getUpdatedAt())
                .build();
    }


    /**
     * 게시글 숨김 처리
     */
    @Transactional
    public void hidePost(Integer postId) {
        log.info("Hiding post ID: {}", postId);
        postRepository.updateHiddenStatus(postId, true);
    }

    /**
     * 숨김된 게시글 복구
     */
    @Transactional
    public void restorePost(Integer postId) {
        log.info("Restoring post ID: {}", postId);
        postRepository.updateHiddenStatus(postId, false);
    }


    /**
     * 게시글 삭제
     * 실제 데이터를 삭제하지 않고 삭제 상태로 표시하며, 삭제 이력을 저장
     *
     * @param postId         삭제할 게시글 ID
     * @param loggedInMemberId 삭제 요청 사용자 ID
     * @param isAdmin        관리자 여부
     */
    @Transactional
    public void deletePost(Integer postId, Integer loggedInMemberId, boolean isAdmin) {
        log.info("Deleting post ID: {} by member ID: {}, isAdmin: {}", postId, loggedInMemberId, isAdmin);

        // 게시글 조회
        var post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID: " + postId));

        // 권한 검증
        if (!post.getMember().getId().equals(loggedInMemberId) && !isAdmin) {
            throw new AccessDeniedException("You are not allowed to delete this post.");
        }

        // 삭제 이력 생성 및 저장
        var history = ForumPostHistory.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .authorName(post.getMember().getName())
                .deletedAt(LocalDateTime.now())
                .fileUrls(post.getFileUrls()) // 파일 URL 저장
                .build();
        historyRepository.save(history);

        // 게시글 삭제 상태로 업데이트
        post.setTitle("[Deleted]"); // 제목 삭제 처리
        post.setContent("This post has been deleted."); // 내용 삭제 처리
        post.setFileUrls(new ArrayList<>()); // 첨부 파일 삭제 처리
        post.setHidden(true); // 숨김 상태로 설정
        post.setRemovedBy(isAdmin ? "ADMIN" : "OP"); // 삭제자 정보 설정
        postRepository.save(post);
    }


    /**
     * 게시글 복구
     * 저장된 이력 데이터를 사용하여 삭제된 게시글을 복구
     *
     * @param postId 복구할 게시글 ID
     */
    @Transactional
    public void undeletePost(Integer postId) {
        log.info("Restoring post ID: {}", postId);

        // 삭제 이력 조회
        ForumPostHistory history = historyRepository.findByPostId(postId)
                .orElseThrow(() -> new IllegalArgumentException("No history found for post ID: " + postId));

        // 게시글 복구
        ForumPost post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID: " + postId));
        post.setTitle(history.getTitle());
        post.setContent(history.getContent());
        post.setFileUrls(history.getFileUrls()); // 파일 URL 복구
        postRepository.save(post);

        // 복구 완료 후 삭제 이력 제거
        historyRepository.delete(history);
    }


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
     * 파일 저장
     * @param file 업로드할 파일
     * @return 저장된 파일 URL
     */
    private String saveFile(MultipartFile file) {
        log.info("Saving file: {}", file.getOriginalFilename());
        return "http://localhost/files/" + file.getOriginalFilename(); // 로컬 저장 예제
    }
}
