package com.capstone.project.forum.service;

import com.capstone.project.forum.dto.request.ForumPostCommentRequestDto;
import com.capstone.project.forum.dto.response.ForumPostCommentResponseDto;
import com.capstone.project.forum.entity.ForumPost;
import com.capstone.project.forum.entity.ForumPostComment;
import com.capstone.project.forum.repository.ForumPostCommentRepository;
import com.capstone.project.forum.repository.ForumPostRepository;
import com.capstone.project.member.entity.Member;
import com.capstone.project.member.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ForumPostCommentServiceTest {

    @Mock
    private ForumPostCommentRepository commentRepository;

    @Mock
    private ForumPostRepository postRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private ForumPostCommentService commentService;

    @Test
    public void testCreateCommentSuccess() {
        // 게시글 엔티티 생성
        ForumPost post = new ForumPost();
        post.setId(1);

        // 회원 엔티티 생성
        Member member = new Member();
        member.setId(1);
        member.setName("User1");

        // 댓글 엔티티 생성
        ForumPostComment comment = new ForumPostComment();
        comment.setId(1);
        comment.setContent("New comment");
        comment.setForumPost(post);
        comment.setMember(member);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());

        // 댓글 요청 DTO 생성
        ForumPostCommentRequestDto requestDto = new ForumPostCommentRequestDto();
        requestDto.setPostId(1);
        requestDto.setMemberId(1);
        requestDto.setContent("New comment");

        // Mock 리포지토리 호출
        when(postRepository.findById(1)).thenReturn(Optional.of(post));
        when(memberRepository.findById(1)).thenReturn(Optional.of(member));
        when(commentRepository.save(any())).thenReturn(comment);

        // Service 메서드 호출
        ForumPostCommentResponseDto responseDto = commentService.createComment(requestDto);

        // 결과 검증
        Assertions.assertEquals("New comment", responseDto.getContent()); // 댓글 내용 확인
        Assertions.assertEquals("User1", responseDto.getAuthorName()); // 작성자 이름 확인
        Assertions.assertNotNull(responseDto.getCreatedAt()); // 생성 시간 확인
        Assertions.assertNotNull(responseDto.getUpdatedAt()); // 수정 시간 확인

        // Mock 호출 검증
        verify(postRepository, times(1)).findById(1);
        verify(memberRepository, times(1)).findById(1);
        verify(commentRepository, times(1)).save(any());
    }
}
