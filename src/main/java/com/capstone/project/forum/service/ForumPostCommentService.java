package com.capstone.project.forum.service;

import com.capstone.project.forum.dto.request.ForumPostCommentRequestDto;
import com.capstone.project.forum.dto.response.ForumPostCommentResponseDto;
import com.capstone.project.forum.entity.ForumPost;
import com.capstone.project.forum.entity.ForumPostComment;
import com.capstone.project.forum.repository.ForumPostCommentRepository;
import com.capstone.project.member.entity.Member;
import com.capstone.project.member.repository.MemberRepository;
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

        ForumPostComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid comment ID: " + commentId));

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
     * 댓글 삭제
     *
     * @param commentId 삭제할 댓글 ID
     */
    @Transactional
    public void deleteComment(Integer commentId, String removedBy) {
        log.info("Marking comment ID: {} as removed by: {}", commentId, removedBy);

        ForumPostComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid comment ID: " + commentId));

        // Update comment fields to indicate it's removed
        comment.setContent("[Removed]");
        comment.setRemovedBy(removedBy); // Add removedBy field to indicate who removed it
        commentRepository.save(comment);

        // Update associated replies (if any)
        List<ForumPostComment> replies = commentRepository.findRepliesByParentCommentId(commentId);
        for (ForumPostComment reply : replies) {
            reply.setContent("This reply is linked to a removed comment.");
            reply.setRemovedBy("SYSTEM");
            commentRepository.save(reply);
        }
        log.info("All replies for comment ID: {} marked as removed.", commentId);
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
     * 댓글 복구 처리
     * 숨김 처리된 댓글을 복구
     *
     * @param commentId 복구할 댓글 ID
     */
    @Transactional
    public void restoreComment(Integer commentId) {
        log.info("Restoring comment ID: {}", commentId);

        ForumPostComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid comment ID: " + commentId));

        comment.setHidden(false);
        commentRepository.save(comment);
        log.info("Comment ID: {} has been restored.", commentId);
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




    // 댓글 좋아요 수 증가
    @Transactional
    public void incrementCommentLikes(Integer commentId) {
        log.info("Incrementing likes for comment ID: {}", commentId); // 댓글 좋아요 증가 로그
        commentRepository.incrementLikes(commentId);
    }
}
