package com.capstone.project.forum.entity;

import com.capstone.project.member.entity.Member;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 댓글 신고 Entity 클래스
 * 각 댓글에 대한 신고 정보를 저장
 */
@Entity
@Table(name = "comment_reports", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"forum_post_comment_id", "member_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_report_id")
    private Integer id; // 신고 ID

    @ManyToOne
    @JoinColumn(name = "forum_post_comment_id", nullable = false)
    private ForumPostComment forumPostComment; // 신고 대상 댓글

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // 신고자

    @Column(nullable = false)
    private String reason; // 신고 사유

    @Column(nullable = false)
    private LocalDateTime createdAt; // 신고 생성 시간
}
