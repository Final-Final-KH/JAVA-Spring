package com.capstone.project.forum.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 포럼 카테고리 Entity 클래스
 * 각 카테고리 정보와 관련된 데이터를 저장
 */
@Entity
@Table(name = "forum_categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForumCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "forum_category_id")
    private Integer id; // 카테고리 고유 ID

    @Column(nullable = false, length = 255)
    private String title; // 카테고리 제목

    @Column(columnDefinition = "TEXT")
    private String description; // 카테고리 설명

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now(); // 생성 시간

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now(); // 수정 시간

    @OneToMany(mappedBy = "forumCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ForumPost> posts = new ArrayList<>(); // 카테고리에 속한 포스트 리스트

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
