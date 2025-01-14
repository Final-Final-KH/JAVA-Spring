package com.capstone.project.forum.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 삭제된 게시글의 이력을 저장하는 엔티티
 * 게시글의 ID, 제목, 내용, 작성자, 삭제 시간 등을 저장
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "forum_post_history")
public class ForumPostHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 고유 ID

    @Column(nullable = false)
    private Integer postId; // 삭제된 게시글 ID

    @Column(nullable = false)
    private String title; // 삭제된 게시글 제목

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content; // 삭제된 게시글 내용

    @Column(nullable = false)
    private String authorName; // 삭제된 게시글 작성자 이름

    @Column(nullable = false)
    private LocalDateTime deletedAt; // 게시글 삭제 시간

    @ElementCollection
    @CollectionTable(name = "forum_post_history_files", joinColumns = @JoinColumn(name = "history_id"))
    @Column(name = "file_url")
    private List<String> fileUrls; // 삭제된 게시글 첨부 파일 목록
}
