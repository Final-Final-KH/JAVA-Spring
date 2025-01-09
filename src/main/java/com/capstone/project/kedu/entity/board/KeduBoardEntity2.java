package com.capstone.project.kedu.entity.board;

import com.capstone.project.kedu.dto.board.KeduBoardCommentReqDTO2;
import com.capstone.project.kedu.entity.edu.AcademyEntity2;
import com.capstone.project.kedu.entity.edu.CourseEntity2;
import com.capstone.project.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "kedu_board")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class KeduBoardEntity2 {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "board_id") // 참조키는 해당 객체의 기본키여야 함
    private Long id;

    @Column(nullable = false)
    private String title; // 글 제목

    @Lob
    @Column(length = 1000)
    private String content; // 글 내용

    private String user_id;

    @Column(nullable = false, updatable = false)
    private LocalDateTime regDate; // 게시글 등록 일자

    @PrePersist
    public void prePersist() {
        regDate = LocalDateTime.now();
    }

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "academy_id")
    private AcademyEntity2 academyEntity2;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private CourseEntity2 courseEntity2;

    @OneToMany(mappedBy = "kedu_board", cascade = CascadeType.ALL, orphanRemoval = true) // 주인이 아님을 의미, 즉 객체를 참조만 함
    private List<KeduBoardCommentEntity2> comments = new ArrayList<>();

    public void addComment(KeduBoardCommentEntity2 keduBoardCommentEntity2){
        comments.add(keduBoardCommentEntity2);
        keduBoardCommentEntity2.setKedu_board(this);
    }

    public void removeComment(KeduBoardCommentEntity2 keduBoardCommentEntity2){
        comments.remove(keduBoardCommentEntity2);
        keduBoardCommentEntity2.setKedu_board(null);
    }

}
