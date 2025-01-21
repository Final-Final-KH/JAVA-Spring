package com.capstone.project.forum.entity;

import com.capstone.project.member.entity.Member;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 포럼 댓글 Entity 클래스
 * 댓글 데이터를 관리
 */
@Entity
@Table(name = "forum_post_comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForumPostComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "forum_post_comment_id")
    private Integer id; // 댓글 ID

    @ManyToOne
    @JoinColumn(name = "forum_post_id", nullable = false)
    private ForumPost forumPost; // 댓글이 속한 게시글

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // 댓글 작성자

    @Column(columnDefinition = "TEXT")
    private String content; // 댓글 내용

    @Column(nullable = false)
    @Builder.Default
    private Integer likesCount = 0; // 댓글 좋아요 수

    @Column(nullable = false)
    @Builder.Default
    private Boolean hidden = false; // 숨김 여부 (신고 누적 시 설정)

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now(); // 생성 시간

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now(); // 수정 시간

    @Column(name = "removed_by")
    private String removedBy; // 댓글 삭제자 정보 ("OP", "ADMIN", "SYSTEM")

    @Column(name = "file_url")
    private String fileUrl; // 첨부 파일 URL (단일 파일 지원)

    /**
     * 대댓글(답글) 구현을 위한 부모 댓글 참조
     */
    @ManyToOne
    @JoinColumn(name = "parent_comment_id")
    private ForumPostComment parentComment; // 부모 댓글

    /**
     * 대댓글(답글) 관리
     */
    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ForumPostComment> childComments = new ArrayList<>(); // 자식 댓글 리스트

    /**
     * 엔티티 생성 이벤트 처리 메서드
     * 생성 시간과 수정 시간을 현재 시간으로 설정
     */
    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 엔티티 업데이트 이벤트 처리 메서드
     * 수정 시간을 현재 시간으로 설정
     */
    @PreUpdate
    private void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Column(name = "edited_by")
    private String editedBy; // 댓글 수정자 정보 ("OP", "ADMIN")

    @Column(nullable = false)
    @Builder.Default
    private Boolean locked = false; // 댓글 수정 잠금 여부 (ADMIN이 수정 시 잠김)

    // Add methods for setting edit metadata
    public void lockForEditing(String editor) {
        this.editedBy = editor;
        this.locked = true;
        this.onUpdate(); // Update the timestamp
    }

    public void unlock() {
        this.editedBy = null;
        this.locked = false;
    }
}
