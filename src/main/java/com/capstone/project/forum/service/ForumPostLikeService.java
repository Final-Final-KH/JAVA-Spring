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

    // 게시글 좋아요 추가
    @Transactional
    public void likePost(Integer memberId, Integer postId) {
        log.info("Adding like for post ID: {} by member ID: {}", postId, memberId); // 게시글 좋아요 추가 로그
        if (!hasLikedPost(memberId, postId)) {
            likeRepository.save(ForumPostLike.builder()
                    .member(Member.builder().id(memberId).build()) // 좋아요를 누른 사용자
                    .forumPost(ForumPost.builder().id(postId).build()) // 좋아요 대상 게시글
                    .build());
        }
    }

    // 댓글 좋아요 추가
    @Transactional
    public void likeComment(Integer memberId, Integer commentId) {
        log.info("Adding like for comment ID: {} by member ID: {}", commentId, memberId); // 댓글 좋아요 추가 로그
        if (!hasLikedComment(memberId, commentId)) {
            likeRepository.save(ForumPostLike.builder()
                    .member(Member.builder().id(memberId).build()) // 좋아요를 누른 사용자
                    .forumPostComment(ForumPostComment.builder().id(commentId).build()) // 좋아요 대상 댓글
                    .build());
        }
    }
}
