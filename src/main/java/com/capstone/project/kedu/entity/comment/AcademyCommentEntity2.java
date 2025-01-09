package com.capstone.project.kedu.entity.comment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "academy_comments")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class AcademyCommentEntity2 {
    @Id
    private Long academy_comment_id;
}
