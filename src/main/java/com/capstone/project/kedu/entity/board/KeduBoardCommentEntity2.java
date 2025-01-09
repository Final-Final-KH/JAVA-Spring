package com.capstone.project.kedu.entity.board;

import com.capstone.project.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "kedu_board_comment")
public class KeduBoardCommentEntity2 {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "board_comment_id")
    private Long board_comment_id; // comment_id

    @ManyToOne
    @JoinColumn(name="board_id")
    private KeduBoardEntity2 kedu_board;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;


    @Column(length = 1000)
    private String content;

    private LocalDateTime regDate;

    @PrePersist
    public void prePersist() {
        regDate = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "kedu_board_comment", cascade = CascadeType.ALL, orphanRemoval = true) // 주인이 아님을 의미, 즉 객체를 참조만 함
    private List<KeduBoardCommentsCommentsEntity2> comments = new ArrayList<>();

    public void addComment(KeduBoardCommentsCommentsEntity2 comment){
        comments.add(comment);
        comment.setKedu_board_comment(this);
    }

    public void removeComment(KeduBoardCommentsCommentsEntity2 comment){
        comments.remove(comment);
        comment.setKedu_board_comment(null);
    }
}