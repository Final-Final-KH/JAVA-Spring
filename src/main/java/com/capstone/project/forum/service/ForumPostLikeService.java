package com.capstone.project.forum.service;

import com.capstone.project.forum.entity.ForumPost;
import com.capstone.project.forum.entity.ForumPostComment;
import com.capstone.project.forum.entity.ForumPostLike;
import com.capstone.project.forum.repository.ForumPostLikeRepository;
import com.capstone.project.member.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j // 로그 기록을 위한 어노테이션 추가
public class ForumPostLikeService {
    private final ForumPostLikeRepository likeRepository;

    // 특정 게시글에 좋아요를 눌렀는지 확인
    public boolean hasLikedPost(Integer memberId, Integer postId) {
        log.debug("Checking if member ID: {} liked post ID: {}", memberId, postId); // 좋아요 확인 디버그 로그
        return likeRepository.findLikeForPostByMember(memberId, postId).isPresent();
    }

    // 특정 댓글에 좋아요를 눌렀는지 확인
    public boolean hasLikedComment(Integer memberId, Integer commentId) {
        log.debug("Checking if member ID: {} liked comment ID: {}", memberId, commentId); // 댓글 좋아요 확인 디버그 로그
        return likeRepository.findLikeForCommentByMember(memberId, commentId).isPresent();
    }

    // 게시글 좋아요 토글
    @Transactional
    public void togglePostLike(Integer memberId, Integer postId) {
        log.info("Toggling like for post ID: {} by member ID: {}", postId, memberId);

        Optional<ForumPostLike> existingLike = likeRepository.findLikeForPostByMember(memberId, postId);
        if (existingLike.isPresent()) {
            // 좋아요 취소
            log.info("Removing like for post ID: {} by member ID: {}", postId, memberId);
            likeRepository.delete(existingLike.get());
        } else {
            // 좋아요 추가
            log.info("Adding like for post ID: {} by member ID: {}", postId, memberId);
            likeRepository.save(ForumPostLike.builder()
                    .member(Member.builder().id(memberId).build())
                    .forumPost(ForumPost.builder().id(postId).build())
                    .build());
        }
    }

    // 댓글 좋아요 토글
    @Transactional
    public void toggleCommentLike(Integer memberId, Integer commentId) {
        log.info("Toggling like for comment ID: {} by member ID: {}", commentId, memberId);

        Optional<ForumPostLike> existingLike = likeRepository.findLikeForCommentByMember(memberId, commentId);
        if (existingLike.isPresent()) {
            // 좋아요 취소
            log.info("Removing like for comment ID: {} by member ID: {}", commentId, memberId);
            likeRepository.delete(existingLike.get());
        } else {
            // 좋아요 추가
            log.info("Adding like for comment ID: {} by member ID: {}", commentId, memberId);
            likeRepository.save(ForumPostLike.builder()
                    .member(Member.builder().id(memberId).build())
                    .forumPostComment(ForumPostComment.builder().id(commentId).build())
                    .build());
        }
    }

    // 게시글 좋아요 추가 (이전 기능 유지)
    @Transactional
    public void likePost(Integer memberId, Integer postId) {
        log.info("Adding like for post ID: {} by member ID: {}", postId, memberId);
        if (!hasLikedPost(memberId, postId)) {
            likeRepository.save(ForumPostLike.builder()
                    .member(Member.builder().id(memberId).build())
                    .forumPost(ForumPost.builder().id(postId).build())
                    .build());
        }
    }

    // 댓글 좋아요 추가 (이전 기능 유지)
    @Transactional
    public void likeComment(Integer memberId, Integer commentId) {
        log.info("Adding like for comment ID: {} by member ID: {}", commentId, memberId);
        if (!hasLikedComment(memberId, commentId)) {
            likeRepository.save(ForumPostLike.builder()
                    .member(Member.builder().id(memberId).build())
                    .forumPostComment(ForumPostComment.builder().id(commentId).build())
                    .build());
        }
    }
}
