package com.capstone.project.forum.service;

import com.capstone.project.forum.dto.request.ForumPostCommentRequestDto;
import com.capstone.project.forum.dto.response.ForumPostCommentResponseDto;
import com.capstone.project.forum.entity.ForumPost;
import com.capstone.project.forum.entity.ForumPostComment;
import com.capstone.project.forum.repository.ForumPostCommentRepository;
import com.capstone.project.member.entity.Member;
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
                        comment.getMember().getName(), // 댓글 작성자 이름 반환
                        comment.getLikesCount(), // 댓글 좋아요 수
                        comment.getCreatedAt() // 댓글 생성 시간
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
                .content(requestDto.getContent()) // 댓글 내용 설정
                .likesCount(0) // 초기 좋아요 수 0으로 설정
                .createdAt(LocalDateTime.now()) // 현재 시간으로 생성 시간 설정
                .updatedAt(LocalDateTime.now()) // 현재 시간으로 수정 시간 초기화
                .build();

        ForumPostComment savedComment = commentRepository.save(newComment); // 데이터베이스에 저장

        // DTO로 변환 후 반환
        return new ForumPostCommentResponseDto(
                savedComment.getId(),
                savedComment.getContent(),
                savedComment.getMember().getName(),
                savedComment.getLikesCount(),
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

        String quotedContent = String.format("%s said: \"%s\"\n\n%s",
                parentComment.getMember().getName(), // 부모 댓글 작성자 이름
                parentComment.getContent(), // 부모 댓글 내용
                requestDto.getContent()); // 답글 내용

        ForumPostComment replyComment = ForumPostComment.builder()
                .forumPost(parentComment.getForumPost()) // 동일한 게시글 참조
                .member(Member.builder().id(requestDto.getMemberId()).build()) // 작성자 ID 매핑
                .content(quotedContent) // 인용된 내용 설정
                .likesCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ForumPostComment savedReply = commentRepository.save(replyComment);

        return new ForumPostCommentResponseDto(
                savedReply.getId(),
                savedReply.getContent(),
                savedReply.getMember().getName(),
                savedReply.getLikesCount(),
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
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ForumPostComment savedReply = commentRepository.save(replyComment);

        return new ForumPostCommentResponseDto(
                savedReply.getId(),
                savedReply.getContent(),
                savedReply.getMember().getName(),
                savedReply.getLikesCount(),
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


    // 댓글 좋아요 수 증가
    @Transactional
    public void incrementCommentLikes(Integer commentId) {
        log.info("Incrementing likes for comment ID: {}", commentId); // 댓글 좋아요 증가 로그
        commentRepository.incrementLikes(commentId);
    }
}
