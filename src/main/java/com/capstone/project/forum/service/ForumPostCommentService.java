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
import com.capstone.project.member.entity.Member;
import com.capstone.project.member.repository.MemberRepository;
import com.capstone.project.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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

    private static final int REPORT_THRESHOLD = 10; // 신고 누적 기준값

    /**
     * 특정 게시글에 속한 댓글 가져오기
     *
     * @param postId 게시글 ID
     * @return 댓글 리스트
     */
    public List<ForumPostCommentResponseDto> getCommentsForPost(Integer postId) {
        log.info("Fetching comments for post ID: {}", postId); // 로그 기록
        return commentRepository.findCommentsByPostId(postId) // 게시글 ID로 댓글 조회
                .stream()
                .map(comment -> new ForumPostCommentResponseDto(
                        comment.getId(),
                        comment.getContent(),
                        comment.getMember().getName(), // 작성자 이름 반환
                        comment.getLikesCount(), // 좋아요 수
                        comment.getHidden(), // 숨김 여부
                        comment.getRemovedBy(), // 삭제자 정보
                        comment.getCreatedAt() // 생성 시간
                ))
                .collect(Collectors.toList()); // 결과를 리스트로 변환
    }

    /**
     * 새로운 댓글 생성
     *
     * @param requestDto 댓글 생성 요청 데이터
     * @return 생성된 댓글 정보
     */
    @Transactional
    public ForumPostCommentResponseDto createComment(ForumPostCommentRequestDto requestDto) {
        log.info("Creating new comment for post ID: {} by member ID: {}", requestDto.getPostId(), requestDto.getMemberId());

        // 댓글 엔티티 생성
        ForumPostComment newComment = ForumPostComment.builder()
                .forumPost(ForumPost.builder().id(requestDto.getPostId()).build()) // 게시글 ID 매핑
                .member(Member.builder().id(requestDto.getMemberId()).build()) // 작성자 ID 매핑
                .content(requestDto.getContent()) // 댓글 내용
                .likesCount(0) // 좋아요 수 초기화
                .hidden(false) // 숨김 상태 초기화
                .createdAt(LocalDateTime.now()) // 생성 시간 설정
                .updatedAt(LocalDateTime.now()) // 수정 시간 초기화
                .build();

        ForumPostComment savedComment = commentRepository.save(newComment); // 데이터베이스에 저장

        // 작성자의 이름을 가져오기 위해 작성자 정보를 다시 조회
        Member commentAuthor = memberRepository.findById(requestDto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID: " + requestDto.getMemberId()));

        return new ForumPostCommentResponseDto(
                savedComment.getId(),
                savedComment.getContent(),
                commentAuthor.getName(),
                savedComment.getLikesCount(),
                savedComment.getHidden(),
                savedComment.getRemovedBy(),
                savedComment.getCreatedAt()
        );
    }


    /**
     * 댓글에 대한 답글 추가
     *
     * @param parentCommentId 부모 댓글 ID
     * @param requestDto 답글 요청 데이터
     * @return 생성된 답글 정보
     */
    @Transactional
    public ForumPostCommentResponseDto replyToComment(Integer parentCommentId, ForumPostCommentRequestDto requestDto) {
        log.info("Replying to comment ID: {} by member ID: {}", parentCommentId, requestDto.getMemberId());

        ForumPostComment parentComment = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid parent comment ID: " + parentCommentId));

        Member replyAuthor = memberRepository.findById(requestDto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID: " + requestDto.getMemberId()));

        String quotedContent = String.format("%s said: \"%s\"\n\n%s",
                parentComment.getMember().getName(), // 부모 댓글 작성자 이름
                parentComment.getContent(), // 부모 댓글 내용
                requestDto.getContent()); // 답글 내용

        ForumPostComment replyComment = ForumPostComment.builder()
                .forumPost(parentComment.getForumPost())
                .member(replyAuthor)
                .content(quotedContent)
                .likesCount(0)
                .hidden(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ForumPostComment savedReply = commentRepository.save(replyComment);

        return new ForumPostCommentResponseDto(
                savedReply.getId(),
                savedReply.getContent(),
                replyAuthor.getName(),
                savedReply.getLikesCount(),
                savedReply.getHidden(),
                savedReply.getRemovedBy(),
                savedReply.getCreatedAt()
        );
    }


    /**
     * 게시글(OP)에 대한 답글 추가
     *
     * @param postId 게시글 ID
     * @param requestDto 답글 요청 데이터
     * @return 생성된 답글 정보
     */
    @Transactional
    public ForumPostCommentResponseDto replyToPost(Integer postId, ForumPostCommentRequestDto requestDto) {
        log.info("Replying to post ID: {} by member ID: {}", postId, requestDto.getMemberId());

        String quotedContent = String.format("%s (OP) said: \"%s\"\n\n%s",
                requestDto.getOpAuthorName(), // OP 작성자 이름
                requestDto.getOpContent(), // OP 내용
                requestDto.getContent()); // 답글 내용

        ForumPostComment replyComment = ForumPostComment.builder()
                .forumPost(ForumPost.builder().id(postId).build()) // 게시글 ID 매핑
                .member(Member.builder().id(requestDto.getMemberId()).build()) // 작성자 ID 매핑
                .content(quotedContent) // 답글 내용 설정
                .likesCount(0)
                .hidden(false) // 기본 숨김 상태
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ForumPostComment savedReply = commentRepository.save(replyComment);

        return new ForumPostCommentResponseDto(
                savedReply.getId(),
                savedReply.getContent(),
                savedReply.getMember().getName(),
                savedReply.getLikesCount(),
                savedReply.getHidden(),
                savedReply.getRemovedBy(),
                savedReply.getCreatedAt()
        );
    }


    /**
     * 댓글 수정
     *
     * @param commentId 수정할 댓글 ID
     * @param newContent 새로운 댓글 내용
     * @return 수정된 댓글 정보
     */
    @Transactional
    public ForumPostCommentResponseDto updateComment(Integer commentId, String newContent) {
        log.info("Updating comment ID: {}", commentId);

        // 댓글 조회
        ForumPostComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid comment ID: " + commentId));

        // 숨김 또는 삭제된 댓글 검증
        if (comment.getHidden() || "[Removed]".equals(comment.getContent())) {
            throw new IllegalStateException("Cannot edit a hidden or removed comment. Please let ADMIN restore it first.");
        }

        // 댓글 수정
        comment.setContent(newContent); // 새로운 내용으로 업데이트
        comment.setUpdatedAt(LocalDateTime.now()); // 수정 시간 갱신
        ForumPostComment updatedComment = commentRepository.save(comment); // 저장

        return new ForumPostCommentResponseDto(
                updatedComment.getId(),
                updatedComment.getContent(),
                updatedComment.getMember().getName(),
                updatedComment.getLikesCount(),
                updatedComment.getHidden(),
                updatedComment.getRemovedBy(),
                updatedComment.getCreatedAt()
        );
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

        ForumPostComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid comment ID: " + commentId));

        boolean isAdmin = memberService.isAdmin(loggedInMemberId);
        if (!comment.getMember().getId().equals(loggedInMemberId) && !isAdmin) {
            throw new IllegalArgumentException("You are not allowed to delete this comment.");
        }

        // 댓글 히스토리 저장
        ForumPostCommentHistory history = ForumPostCommentHistory.builder()
                .commentId(comment.getId())
                .content(comment.getContent())
                .authorName(comment.getMember().getName())
                .deletedAt(LocalDateTime.now())
                .build();
        commentHistoryRepository.save(history);

        // 댓글 삭제 처리
        comment.setContent("[Removed]");
        comment.setHidden(true); // 숨김 상태로 전환
        commentRepository.save(comment);

        log.info("Comment ID: {} deleted and history recorded.", commentId);
    }

    /**
     * 댓글 신고
     *
     * @param commentId 신고 대상 댓글 ID
     * @param reporterId 신고자 ID
     * @param reason 신고 사유
     */
    @Transactional
    public void reportComment(Integer commentId, Integer reporterId, String reason) {
        log.info("Reporting comment ID: {} by reporter ID: {}", commentId, reporterId);

        // 댓글 조회
        ForumPostComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid comment ID: " + commentId));

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

        // 신고 누적 확인 및 댓글 숨김 처리
        long reportCount = commentReportRepository.countByCommentId(commentId);
        log.info("Comment ID: {} has {} reports.", commentId, reportCount);

        if (reportCount >= REPORT_THRESHOLD) {
            comment.setHidden(true);
            commentRepository.save(comment);
            log.info("Comment ID: {} has been hidden due to exceeding report threshold.", commentId);
        }
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

    /**
     * 댓글 복구
     * 삭제된 댓글을 삭제 이력을 사용하여 복구합니다.
     *
     * @param commentId 복구할 댓글 ID
     */
    @Transactional
    public void restoreComment(Integer commentId) {
        log.info("Restoring comment ID: {}", commentId);

        // 댓글 조회
        ForumPostComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid comment ID: " + commentId));

        // 삭제된 상태 확인
        if (!"[Removed]".equals(comment.getContent())) {
            throw new IllegalStateException("The comment is not in a deleted state.");
        }

        // 삭제 이력 조회 (최신 데이터만 가져오기)
        ForumPostCommentHistory history = commentHistoryRepository.findTopByCommentIdOrderByDeletedAtDesc(commentId)
                .orElseThrow(() -> new IllegalArgumentException("No history found for comment ID: " + commentId));

        // 댓글 복구
        comment.setContent(history.getContent()); // 내용 복구
        comment.setHidden(false); // 숨김 해제
        comment.setRemovedBy(null); // 삭제자 정보 초기화
        commentRepository.save(comment); // 데이터베이스에 저장

        log.info("Comment ID: {} successfully restored.", commentId);
    }


    /**
     * 댓글 삭제 취소 (복구)
     * 삭제된 댓글을 원래 상태로 복원
     *
     * @param commentId 복구할 댓글 ID
     */
    @Transactional
    public void undeleteComment(Integer commentId) {
        log.info("Undeleting comment ID: {}", commentId);

        ForumPostComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid comment ID: " + commentId));

        if ("[Removed]".equals(comment.getContent())) {
            comment.setContent("This comment has been restored."); // 원래 내용 복구 (임시 메시지)
            comment.setRemovedBy(null); // 삭제자 정보 초기화
            commentRepository.save(comment);
            log.info("Comment ID: {} has been undeleted.", commentId);
        } else {
            log.warn("Comment ID: {} is not in a deleted state.", commentId);
        }
    }

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
