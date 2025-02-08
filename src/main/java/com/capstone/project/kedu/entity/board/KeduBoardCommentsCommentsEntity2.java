package com.capstone.project.kedu.entity.board;

import com.capstone.project.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "kedu_board_comment_comment")
public class KeduBoardCommentsCommentsEntity2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId; // comment_id

    @ManyToOne
    @JoinColumn(name="board_id")
    private KeduBoardEntity2 board;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "board_comment_id")
    private KeduBoardCommentEntity2 kedu_board_comment;

    @Column(length = 1000)
    private String content;

    private LocalDateTime regDate;

    @PrePersist
    public void prePersist() {
        regDate = LocalDateTime.now();
    }
}
