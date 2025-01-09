package com.capstone.project.kedu.entity.board;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "kedu_board_comment")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class KeduBoardCommentEntity2 {
    @Id
    private Long board_comment_id;

}
