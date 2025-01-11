package com.capstone.project.forum.entity;

import com.capstone.project.forum.entity.ForumCategory;
import com.capstone.project.member.entity.Member;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "forum_posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForumPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "forum_post_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private ForumCategory forumCategory;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Boolean sticky = false;

    @Column(nullable = false)
    private Integer viewsCount = 0;

    @Column(nullable = false)
    private Integer likesCount = 0;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ElementCollection
    @CollectionTable(name = "forum_post_files", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "file_url")
    private List<String> fileUrls = new ArrayList<>(); // 첨부 파일 URL 목록

    public void addFileUrl(String fileUrl) {
        this.fileUrls.add(fileUrl); // 파일 URL 추가
    }

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
