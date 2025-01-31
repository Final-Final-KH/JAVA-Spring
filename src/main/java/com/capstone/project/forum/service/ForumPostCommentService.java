package com.capstone.project.forum.service;

import com.capstone.project.forum.dto.request.ForumPostCommentRequestDto;
import com.capstone.project.forum.dto.response.ForumPostCommentResponseDto;
import com.capstone.project.forum.entity.CommentReport;
import com.capstone.project.forum.entity.ForumPost;
import com.capstone.project.forum.entity.ForumPostComment;
import com.capstone.project.forum.entity.ForumPostCommentHistory;
import com.capstone.project.forum.repository.CommentReportRepository;
import com.capstone.project.forum.repository.ForumPostCommentHistoryRepository;
import com.capstone.project.forum.repository.ForumPostCommentRepository;
import com.capstone.project.forum.repository.ForumPostRepository;
import com.capstone.project.member.entity.Member;
import com.capstone.project.member.repository.MemberRepository;
import com.capstone.project.member.service.MemberService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j // 로그 기록을 위한 어노테이션 추가
public class ForumPostCommentService {

    private final ForumPostCommentRepository commentRepository; // 댓글 데이터베이스 접근 객체
    private final MemberRepository memberRepository;
    private final CommentReportRepository commentReportRepository;
    private final MemberService memberService;
    private final ForumPostCommentHistoryRepository commentHistoryRepository;
    private final ForumPostRepository postRepository;

    private static final int REPORT_THRESHOLD = 10; // 신고 누적 기준값

    /**
     * 특정 게시글에 포함된 댓글을 조회하고 DTO 리스트로 변환
     *
     * @param postId 게시글 ID
     * @return 댓글 응답 DTO 리스트
     */
    public List<ForumPostCommentResponseDto> getCommentsForPost(Integer postId) {
        log.info("Fetching comments for post ID: {}", postId);

        // 1. 댓글 ID 리스트를 가져오기
        // 특정 게시글에 포함된 댓글 ID를 조회하여 List<Integer> 형태로 반환합니다.
        List<Integer> commentIds = commentRepository.findCommentIdsByPostId(postId);

        // 2. 댓글 ID에 대한 신고 횟수 가져오기
        // CommentReportRepository에서 countByCommentIds 메서드를 사용하여 댓글 ID 목록에 대한 신고 횟수를 Object[] 리스트로 가져옵니다.
        List<Object[]> rawReportCounts = commentReportRepository.countByCommentIds(commentIds);

        // 3. 로깅: rawReportCounts 데이터 확인
        // DB로부터 가져온 원본 데이터가 어떤 형태인지 확인하기 위해 로그로 출력합니다.
        log.info("Raw report counts: {}", rawReportCounts);

        // 4. 신고 횟수 데이터 변환
        // rawReportCounts(List<Object[]>)를 Map<Integer, Long>으로 변환합니다.
        // Object 배열의 첫 번째 요소(obj[0])는 댓글 ID이며, 두 번째 요소(obj[1])는 신고 횟수입니다.
        Map<Integer, Long> reportCounts = rawReportCounts.stream()
                .collect(Collectors.toMap(
                        obj -> ((Number) obj[0]).intValue(), // obj[0]을 안전하게 Integer로 변환
                        obj -> ((Number) obj[1]).longValue() // obj[1]을 Long으로 변환
                ));

        // 5. 로깅: 변환된 신고 횟수 데이터 확인
        // 변환된 Map 데이터의 내용을 로그로 출력하여 예상한 형태인지 확인합니다.
        log.info("Processed report counts: {}", reportCounts);

        // 6. 댓글 데이터를 가져오고 DTO로 변환
        // 댓글 데이터를 ForumPostCommentRepository에서 조회한 후, DTO로 변환합니다.
        // 각 댓글에 대해 신고 횟수(reportCount)를 설정하며, 기본값은 0L입니다.
        return commentRepository.findCommentsByPostId(postId).stream()
                .map(comment -> ForumPostCommentResponseDto.builder()
                        .id(comment.getId()) // 댓글 ID
                        .content(comment.getContent()) // 댓글 내용
                        .authorName(comment.getMember().getName()) // 작성자 이름
                        .memberId(comment.getMember().getId()) // 작성자 ID
                        .likesCount(comment.getLikesCount()) // 좋아요 수
                        .hidden(comment.getHidden()) // 숨김 여부
                        .removedBy(comment.getRemovedBy()) // 삭제자 정보
                        .editedBy(comment.getEditedBy()) // 수정자 정보
                        .locked(comment.getLocked()) // 잠금 여부
                        .createdAt(comment.getCreatedAt()) // 생성 시간
                        .updatedAt(comment.getUpdatedAt()) // 수정 시간
                        .fileUrl(comment.getFileUrl()) // 첨부 파일 URL
                        .reportCount(reportCounts.getOrDefault(comment.getId(), 0L)) // 신고 횟수 (기본값 0)
                        .build())
                .collect(Collectors.toList()); // DTO 리스트로 변환
    }

    private String sanitizeHtml(String content) {
        if (content == null || content.isEmpty()) return content;

        // 🔽 로그: 원본 content 확인
        log.info("Sanitizing content (before): {}", content);

        /**
         * 1) 기본적인 'relaxed' 정책을 사용하되,
         * 2) 블록 인용 태그(<blockquote>) 또는 전체 태그(:all)에 대해 'class' 속성을 허용하도록 확장합니다.
         *
         * - Safelist.relaxed(): Jsoup가 제공하는 "relaxed" 기본 정책(여러 태그/속성 허용)
         * - .addAttributes("blockquote", "class"):
         *     blockquote 태그에 "class" 속성을 허용 (ex. class="reply-quote")
         * - 만약 모든 태그에 대해 class를 허용하려면 .addAttributes(":all", "class")를 사용
         */
        Safelist safelist = Safelist.relaxed()
                .addAttributes("blockquote", "class") // 또는 .addAttributes(":all", "class")
                .addAttributes("a", "href", "rel", "target")
                // 아래 한 줄 추가: "href"에서 "#" (앵커)도 허용
                .addProtocols("a", "href", "#", "http", "https", "mailto", "tel", "ftp");


        // <a> 태그에 href, rel, target 속성 허용 (기존 코드)

        /**
         * 3) Jsoup.clean(content, safelist)를 이용해 HTML을 세척(sanitize)
         *    -> 지정된 태그/속성 외에는 모두 제거
         */
        String sanitizedContent = Jsoup.clean(content, safelist);

        // 🔽 로그: 최종 세척 후 content 확인
        log.info("Sanitized content (after): {}", sanitizedContent);

        return sanitizedContent;
    }


    

    /**
     * 새로운 댓글 생성
     *
     * @param requestDto 댓글 생성 요청 데이터 (게시글 ID, 작성자 ID, 내용, 파일 URL, 부모 댓글 ID 등)
     * @return 생성된 댓글 정보 (ForumPostCommentResponseDto)
     * @throws IllegalArgumentException 유효하지 않은 회원 ID, 게시글 ID 또는 부모 댓글 ID일 경우 예외 발생
     */
    @Transactional
    public ForumPostCommentResponseDto createComment(ForumPostCommentRequestDto requestDto) {
        log.info("Creating new comment for post ID: {} by member ID: {}", requestDto.getPostId(), requestDto.getMemberId());

        // 1️⃣ 사용자 ID 유효성 검사
        if (requestDto.getMemberId() == null) {
            throw new IllegalArgumentException("Member ID is null or invalid.");
        }

        // 2️⃣ 게시글 ID 유효성 검사 및 게시글 조회
        ForumPost forumPost = postRepository.findById(requestDto.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID: " + requestDto.getPostId()));

        // 3️⃣ 작성자 ID 유효성 검사 및 작성자 조회
        Member commentAuthor = memberRepository.findById(requestDto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID: " + requestDto.getMemberId()));

        // 4️⃣ 부모 댓글 ID가 존재하는 경우 유효성 검사 및 조회
        ForumPostComment parentComment = null; // 부모 댓글 초기화
        if (requestDto.getParentCommentId() != null) {
            parentComment = commentRepository.findById(requestDto.getParentCommentId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid parent comment ID: " + requestDto.getParentCommentId()));
        }

        // 5️⃣ 댓글 내용 HTML 정리 및 검사
        String sanitizedContent = sanitizeHtml(requestDto.getContent());
        log.info("Sanitized content: {}", sanitizedContent);

        // 6️⃣ 새로운 댓글 엔티티 생성
        ForumPostComment newComment = ForumPostComment.builder()
                .forumPost(forumPost) // 게시글 매핑
                .member(commentAuthor) // 작성자 매핑
                .content(sanitizedContent) // 정리된 내용 설정
                .parentComment(parentComment) // 부모 댓글 매핑 (답글의 경우)
                .fileUrl(requestDto.getFileUrl()) // 첨부 파일 URL
                .likesCount(0) // 초기 좋아요 수
                .hidden(false) // 숨김 상태 초기화
                .createdAt(LocalDateTime.now()) // 생성 시간
                .updatedAt(LocalDateTime.now()) // 수정 시간
                .build();

        // 7️⃣ 댓글 저장
        ForumPostComment savedComment = commentRepository.save(newComment);

        // 8️⃣ 저장된 댓글 정보 반환
        return ForumPostCommentResponseDto.builder()
                .id(savedComment.getId())
                .content(savedComment.getContent()) // 저장된 댓글 내용 반환
                .parentCommentId(parentComment != null ? parentComment.getId() : null) // 부모 댓글 ID 포함
                .parentContent(parentComment != null ? parentComment.getContent() : null) // 부모 댓글 내용 포함 (UI 표시용)
                .memberId(commentAuthor.getId())
                .authorName(commentAuthor.getName())
                .likesCount(savedComment.getLikesCount())
                .hidden(savedComment.getHidden())
                .removedBy(savedComment.getRemovedBy())
                .createdAt(savedComment.getCreatedAt())
                .updatedAt(savedComment.getUpdatedAt())
                .fileUrl(savedComment.getFileUrl())
                .build();
    }



    @Transactional
    public ForumPostCommentResponseDto updateComment(Integer commentId, ForumPostCommentRequestDto requestDto, Integer loggedInMemberId, boolean isAdmin) {
        log.info("Updating comment ID: {} by member ID: {}", commentId, loggedInMemberId);

        // 요청 데이터 유효성 확인
        if (requestDto.getContent() == null || requestDto.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Comment content cannot be empty."); // 비어 있는 댓글 금지
        }

        // 댓글 조회
        ForumPostComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid comment ID: " + commentId));

        // 권한 확인
        if (!isAdmin && !comment.getMember().getId().equals(loggedInMemberId)) {
            throw new SecurityException("You are not allowed to edit this comment."); // 권한 부족
        }

        // 숨김 처리된 댓글은 수정 불가
        if (comment.getHidden() || "[Removed]".equals(comment.getContent())) {
            throw new IllegalStateException("Cannot edit a hidden or removed comment.");
        }

        // 댓글 내용 HTML 정리 및 설정
        String sanitizedContent = sanitizeHtml(requestDto.getContent());
        comment.setContent(sanitizedContent);

        // 파일 URL 업데이트 (선택 사항)
        if (requestDto.getFileUrl() != null) {
            comment.setFileUrl(requestDto.getFileUrl());
        }
        comment.setUpdatedAt(LocalDateTime.now()); // 수정 시간 업데이트

        // 관리자 수정 여부 처리
        if (isAdmin) {
            comment.setEditedBy("ADMIN");
            comment.setLocked(true); // 추가 편집 잠금
        } else {
            comment.setEditedBy(comment.getMember().getName());
        }

        // 수정된 댓글 저장 및 반환
        ForumPostComment updatedComment = commentRepository.save(comment);

        return ForumPostCommentResponseDto.builder()
                .id(updatedComment.getId())
                .content(updatedComment.getContent()) // 수정된 댓글 내용 반환
                .authorName(updatedComment.getMember().getName())
                .memberId(updatedComment.getMember().getId())
                .likesCount(updatedComment.getLikesCount())
                .hidden(updatedComment.getHidden())
                .removedBy(updatedComment.getRemovedBy())
                .editedBy(updatedComment.getEditedBy())
                .locked(updatedComment.getLocked())
                .createdAt(updatedComment.getCreatedAt())
                .updatedAt(updatedComment.getUpdatedAt())
                .fileUrl(updatedComment.getFileUrl())
                .reportCount(commentReportRepository.countByCommentId(updatedComment.getId())) // 신고 횟수 포함
                .build();
    }










    /**
     * 댓글에 대한 답글 추가
     *
     * @param parentCommentId 부모 댓글 ID
     * @param requestDto 답글 요청 데이터
     * @return 생성된 답글 정보 (ForumPostCommentResponseDto)
     */
    @Transactional
    public ForumPostCommentResponseDto replyToComment(Integer parentCommentId, ForumPostCommentRequestDto requestDto) {
        log.info("Replying to comment ID: {} by member ID: {}", parentCommentId, requestDto.getMemberId());

        // 부모 댓글 조회
        ForumPostComment parentComment = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid parent comment ID: " + parentCommentId));

        // 답글 작성자 정보 조회
        Member replyAuthor = memberRepository.findById(requestDto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID: " + requestDto.getMemberId()));

        // 부모 댓글 내용을 포함한 답글 내용 생성
        String quotedContent = String.format("%s said: \"%s\"\n\n%s",
                parentComment.getMember().getName(), // 부모 댓글 작성자 이름
                parentComment.getContent(), // 부모 댓글 내용
                requestDto.getContent()); // 답글 내용

        // 답글 댓글 엔티티 생성
        ForumPostComment replyComment = ForumPostComment.builder()
                .forumPost(parentComment.getForumPost()) // 부모 댓글이 속한 게시글 정보
                .member(replyAuthor) // 답글 작성자 정보
                .content(quotedContent) // 답글 내용
                .likesCount(0) // 초기 좋아요 수
                .hidden(false) // 숨김 여부
                .locked(false) // 초기 잠금 상태
                .createdAt(LocalDateTime.now()) // 생성 시간
                .updatedAt(LocalDateTime.now()) // 수정 시간
                .build();

        // 데이터베이스에 답글 저장
        ForumPostComment savedReply = commentRepository.save(replyComment);

        // 응답 DTO 반환
        return ForumPostCommentResponseDto.builder()
                .id(savedReply.getId()) // 댓글 ID
                .content(savedReply.getContent()) // 댓글 내용
                .authorName(replyAuthor.getName()) // 작성자 이름
                .memberId(replyAuthor.getId()) // 작성자 ID
                .likesCount(savedReply.getLikesCount()) // 좋아요 수
                .hidden(savedReply.getHidden()) // 숨김 여부
                .locked(savedReply.getLocked()) // 잠금 상태 추가
                .createdAt(savedReply.getCreatedAt()) // 생성 시간
                .updatedAt(savedReply.getUpdatedAt()) // 수정 시간
                .fileUrl(savedReply.getFileUrl()) // 첨부 파일 URL
                .build();
    }



    /**
     * 게시글(OP)에 대한 답글 추가
     *
     * @param postId 게시글 ID
     * @param requestDto 답글 요청 데이터
     * @return 생성된 답글 정보 (ForumPostCommentResponseDto)
     */
    @Transactional
    public ForumPostCommentResponseDto replyToPost(Integer postId, ForumPostCommentRequestDto requestDto) {
        log.info("Replying to post ID: {} by member ID: {}", postId, requestDto.getMemberId());

        // 게시글 내용을 인용한 답글 내용 생성
        String quotedContent = String.format("%s (OP) said: \"%s\"\n\n%s",
                requestDto.getOpAuthorName(), // 게시글 작성자 이름
                requestDto.getOpContent(), // 게시글 내용
                requestDto.getContent()); // 답글 내용

        // 답글 댓글 엔티티 생성
        ForumPostComment replyComment = ForumPostComment.builder()
                .forumPost(ForumPost.builder().id(postId).build()) // 게시글 ID 매핑
                .member(Member.builder().id(requestDto.getMemberId()).build()) // 답글 작성자 ID 매핑
                .content(quotedContent) // 답글 내용
                .fileUrl(requestDto.getFileUrl()) // 첨부 파일 URL
                .likesCount(0) // 초기 좋아요 수
                .hidden(false) // 숨김 여부
                .locked(false) // 초기 잠금 상태
                .createdAt(LocalDateTime.now()) // 생성 시간
                .updatedAt(LocalDateTime.now()) // 수정 시간
                .build();

        // 데이터베이스에 답글 저장
        ForumPostComment savedReply = commentRepository.save(replyComment);

        // 응답 DTO 반환
        return ForumPostCommentResponseDto.builder()
                .id(savedReply.getId()) // 댓글 ID
                .content(savedReply.getContent()) // 댓글 내용
                .authorName(savedReply.getMember().getName()) // 작성자 이름
                .memberId(savedReply.getMember().getId()) // 작성자 ID
                .likesCount(savedReply.getLikesCount()) // 좋아요 수
                .hidden(savedReply.getHidden()) // 숨김 여부
                .locked(savedReply.getLocked()) // 잠금 상태 추가
                .createdAt(savedReply.getCreatedAt()) // 생성 시간
                .updatedAt(savedReply.getUpdatedAt()) // 수정 시간
                .fileUrl(savedReply.getFileUrl()) // 첨부 파일 URL
                .build();
    }



    /**
     * 댓글 삭제 (히스토리 생성 포함)
     *
     * @param commentId 삭제할 댓글 ID
     * @param loggedInMemberId 요청 사용자 ID
     */
    @Transactional
    public void deleteComment(Integer commentId, Integer loggedInMemberId) {
        log.info("Deleting comment ID: {} by member ID: {}", commentId, loggedInMemberId);

        // 댓글 조회 / Fetch the comment
        ForumPostComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid comment ID: " + commentId));

        // 댓글 소유자 또는 관리자 권한 확인 / Check ownership or admin privileges
        boolean isAdmin = memberService.isAdmin(loggedInMemberId);
        if (!comment.getMember().getId().equals(loggedInMemberId) && !isAdmin) {
            throw new IllegalArgumentException("You are not allowed to delete this comment.");
        }

        // 댓글 삭제 이력 기록 / Log deletion history
        ForumPostCommentHistory history = ForumPostCommentHistory.builder()
                .commentId(comment.getId())
                .content(comment.getContent())
                .authorName(comment.getMember().getName())
                .deletedAt(LocalDateTime.now())
                .build();
        commentHistoryRepository.save(history);

        // 댓글 상태를 삭제됨으로 표시 / Mark the comment as deleted
        comment.setContent("[Removed]");
        comment.setHidden(true);
        if (isAdmin) {
            comment.setRemovedBy("ADMIN"); // 삭제자가 관리자임을 표시
        }
        commentRepository.save(comment);

        log.info("Comment ID: {} deleted and history recorded.", commentId);
    }


    /**
     * 댓글 신고
     *
     * @param commentId 신고 대상 댓글 ID
     * @param reporterId 신고자 ID
     * @param reason 신고 사유
     * @return ForumPostCommentResponseDto 업데이트된 댓글 정보 DTO
     */
    @Transactional
    public ForumPostCommentResponseDto reportComment(Integer commentId, Integer reporterId, String reason) {
        log.info("Reporting comment ID: {} by reporter ID: {}", commentId, reporterId);

        // 댓글 조회
        ForumPostComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid comment ID: " + commentId));

        // 자신의 댓글 신고 방지
        if (comment.getMember().getId().equals(reporterId)) {
            throw new IllegalArgumentException("You cannot report your own comment.");
        }

        // 중복 신고 방지
        boolean alreadyReported = commentReportRepository.existsByCommentIdAndReporterId(commentId, reporterId);
        if (alreadyReported) {
            throw new IllegalArgumentException("You have already reported this comment.");
        }

        // 신고 엔티티 생성 및 저장
        CommentReport report = CommentReport.builder()
                .forumPostComment(comment)
                .member(memberRepository.findById(reporterId)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid reporter ID: " + reporterId)))
                .reason(reason)
                .createdAt(LocalDateTime.now())
                .build();
        commentReportRepository.save(report);

        // 신고 누적 확인
        long reportCount = commentReportRepository.countByCommentId(commentId);

        // 댓글 숨김 처리
        if (reportCount >= REPORT_THRESHOLD) {
            comment.setHidden(true);
            commentRepository.save(comment);
            log.info("Comment ID: {} has been hidden due to exceeding report threshold.", commentId);
        }

        // 업데이트된 댓글 정보 DTO로 반환
        return ForumPostCommentResponseDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .hidden(comment.isHidden())
                .reportCount(Long.valueOf(reportCount))
                .hasReported(commentReportRepository.existsByCommentIdAndReporterId(commentId, reporterId))
                .build();
    }






    /**
     * 댓글 숨김 처리
     * 특정 댓글을 숨김 상태로 설정
     *
     * @param commentId 숨길 댓글 ID
     */
    @Transactional
    public void hideComment(Integer commentId) {
        log.info("Hiding comment ID: {}", commentId);

        ForumPostComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid comment ID: " + commentId));

        comment.setHidden(true); // 숨김 상태로 설정
        commentRepository.save(comment);
        log.info("Comment ID: {} marked as hidden.", commentId);
    }

    // 댓글 복원 로직
    @Transactional
    public ForumPostCommentResponseDto restoreComment(Integer commentId) {
        log.info("Restoring comment ID: {}", commentId);

        ForumPostComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid comment ID: " + commentId));

        ForumPostCommentHistory history = commentHistoryRepository.findTopByCommentIdOrderByDeletedAtDesc(commentId)
                .orElseThrow(() -> new IllegalArgumentException("No history found for comment ID: " + commentId));

        if (history.getContent() != null) {
            comment.setContent(history.getContent());
            comment.setHidden(false);
            comment.setRemovedBy(null);
            commentRepository.save(comment);
            log.info("Comment ID: {} successfully restored.", commentId);
        } else {
            throw new IllegalStateException("No valid history content for restoration.");
        }

        return ForumPostCommentResponseDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .authorName(comment.getMember().getName())
                .memberId(comment.getMember().getId())
                .likesCount(comment.getLikesCount())
                .hidden(comment.getHidden())
                .removedBy(comment.getRemovedBy())
                .editedBy(comment.getEditedBy())
                .locked(comment.getLocked())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .fileUrl(comment.getFileUrl())
                .reportCount(commentReportRepository.countByCommentId(comment.getId())) // Add reportCount
                .build();
    }





//    게시글/포스팅쪽 이랑 동일한 문제. 중복된 기능으로 판단되서 주석처리
    // 추후에 확정되면 삭제 처리
//    /**
//     * 댓글 삭제 취소 (복구)
//     * 삭제된 댓글을 원래 상태로 복원
//     *
//     * @param commentId 복구할 댓글 ID
//     */
//    @Transactional
//    public void undeleteComment(Integer commentId) {
//        log.info("Undeleting comment ID: {}", commentId);
//
//        ForumPostComment comment = commentRepository.findById(commentId)
//                .orElseThrow(() -> new IllegalArgumentException("Invalid comment ID: " + commentId));
//
//        if ("[Removed]".equals(comment.getContent())) {
//            comment.setContent("This comment has been restored."); // 원래 내용 복구 (임시 메시지)
//            comment.setRemovedBy(null); // 삭제자 정보 초기화
//            commentRepository.save(comment);
//            log.info("Comment ID: {} has been undeleted.", commentId);
//        } else {
//            log.warn("Comment ID: {} is not in a deleted state.", commentId);
//        }
//    }

    /**
     * 특정 댓글의 삭제 히스토리 가져오기
     *
     * @param commentId 댓글 ID
     * @return 삭제 히스토리 리스트
     */
    @Transactional(readOnly = true)
    public List<ForumPostCommentHistory> getCommentHistory(Integer commentId) {
        log.info("Fetching history for comment ID: {}", commentId);
        return commentHistoryRepository.findAllByCommentId(commentId);
    }


    // 댓글 좋아요 수 증가
    @Transactional
    public void incrementCommentLikes(Integer commentId) {
        log.info("Incrementing likes for comment ID: {}", commentId); // 댓글 좋아요 증가 로그
        commentRepository.incrementLikes(commentId);
    }
}
