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

    private final ForumPostCommentRepository commentRepository;

    // 특정 게시글에 속한 댓글 가져오기
    public List<ForumPostCommentResponseDto> getCommentsForPost(Integer postId) {
        log.info("Fetching comments for post ID: {}", postId); // 게시글 댓글 조회 로그
        return commentRepository.findCommentsByPostId(postId)
                .stream()
                .map(comment -> new ForumPostCommentResponseDto(
                        comment.getId(),
                        comment.getContent(),
                        comment.getMember().getName(), // 댓글 작성자 이름
                        comment.getLikesCount(),
                        comment.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    // 새로운 댓글 생성
    @Transactional
    public ForumPostCommentResponseDto createComment(ForumPostCommentRequestDto requestDto) {
        log.info("Creating new comment for post ID: {} by member ID: {}", requestDto.getPostId(), requestDto.getMemberId()); // 댓글 생성 로그

        // 새로운 댓글 엔티티 생성
        ForumPostComment newComment = ForumPostComment.builder()
                .forumPost(ForumPost.builder().id(requestDto.getPostId()).build()) // 게시글 ID 매핑
                .member(Member.builder().id(requestDto.getMemberId()).build()) // 작성자 ID 매핑
                .content(requestDto.getContent()) // 댓글 내용
                .likesCount(0) // 초기 좋아요 수
                .createdAt(LocalDateTime.now()) // 생성 시간
                .updatedAt(LocalDateTime.now()) // 수정 시간 초기화
                .build();

        // 댓글 저장
        ForumPostComment savedComment = commentRepository.save(newComment);

        // 저장된 댓글 정보를 DTO로 변환하여 반환
        return new ForumPostCommentResponseDto(
                savedComment.getId(),
                savedComment.getContent(),
                savedComment.getMember().getName(), // 작성자 이름
                savedComment.getLikesCount(),
                savedComment.getCreatedAt()
        );
    }

    // 댓글에 대한 답글 추가 (인용)
    @Transactional
    public ForumPostCommentResponseDto replyToComment(Integer parentCommentId, ForumPostCommentRequestDto requestDto) {
        log.info("Replying to comment ID: {} by member ID: {}", parentCommentId, requestDto.getMemberId());

        // 부모 댓글 가져오기
        ForumPostComment parentComment = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid parent comment ID: " + parentCommentId));

        // 새로운 답글 내용 생성 (인용 포함)
        String quotedContent = String.format("%s said: \"%s\"\n\n%s",
                parentComment.getMember().getName(),
                parentComment.getContent(),
                requestDto.getContent());

        // 답글 엔티티 생성
        ForumPostComment replyComment = ForumPostComment.builder()
                .forumPost(parentComment.getForumPost()) // 동일한 게시글 참조
                .member(Member.builder().id(requestDto.getMemberId()).build())
                .content(quotedContent)
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

    // 게시글(OP)에 대한 답글 추가 (인용)
    @Transactional
    public ForumPostCommentResponseDto replyToPost(Integer postId, ForumPostCommentRequestDto requestDto) {
        log.info("Replying to post ID: {} by member ID: {}", postId, requestDto.getMemberId());

        // OP 게시글 가져오기
        ForumPost forumPost = ForumPost.builder().id(postId).build(); // 게시글 ID 매핑 (예: DB 접근 최소화)

        // 새로운 답글 내용 생성 (인용 포함)
        String quotedContent = String.format("%s (OP) said: \"%s\"\n\n%s",
                requestDto.getOpAuthorName(), // OP 작성자 이름 (필요 시 추가)
                requestDto.getOpContent(),   // OP 내용 (클라이언트 요청으로 전달)
                requestDto.getContent());   // 답글 내용

        // 답글 엔티티 생성
        ForumPostComment replyComment = ForumPostComment.builder()
                .forumPost(forumPost)
                .member(Member.builder().id(requestDto.getMemberId()).build())
                .content(quotedContent)
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

    // 댓글 좋아요 수 증가
    @Transactional
    public void incrementCommentLikes(Integer commentId) {
        log.info("Incrementing likes for comment ID: {}", commentId); // 댓글 좋아요 증가 로그
        commentRepository.incrementLikes(commentId);
    }

    // 댓글 수정
    @Transactional
    public ForumPostCommentResponseDto updateComment(Integer commentId, String newContent) {
        log.info("Updating comment ID: {}", commentId);
        ForumPostComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid comment ID: " + commentId));
        comment.setContent(newContent);
        comment.setUpdatedAt(LocalDateTime.now());
        ForumPostComment updatedComment = commentRepository.save(comment);
        return new ForumPostCommentResponseDto(
                updatedComment.getId(),
                updatedComment.getContent(),
                updatedComment.getMember().getName(),
                updatedComment.getLikesCount(),
                updatedComment.getCreatedAt()
        );
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Integer commentId) {
        log.info("Deleting comment ID: {}", commentId);
        if (!commentRepository.existsById(commentId)) {
            throw new IllegalArgumentException("Invalid comment ID: " + commentId);
        }
        commentRepository.deleteById(commentId);
    }
}
