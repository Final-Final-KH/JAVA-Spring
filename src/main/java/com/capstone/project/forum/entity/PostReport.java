package com.capstone.project.forum.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 게시글 신고 Entity 클래스
 * 게시글에 대한 신고 정보를 저장하는 역할
 */
@Entity
@Table(name = "post_reports", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"forum_post_id", "reporter_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_report_id")
    private Integer id; // 신고 ID

    @ManyToOne
    @JoinColumn(name = "forum_post_id", nullable = false)
    private ForumPost forumPost; // 신고 대상 게시글

    @Column(name = "reporter_id", nullable = false)
    private Integer reporterId; // 신고자 ID (Member 엔티티 대신 단순 ID 사용)

    @Column(nullable = false)
    private String reason; // 신고 사유

    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt; // 신고 생성 시간
}
