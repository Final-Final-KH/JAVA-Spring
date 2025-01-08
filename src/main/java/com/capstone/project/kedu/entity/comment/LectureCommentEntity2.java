package com.capstone.project.kedu.entity.comment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "lecture_comments")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class LectureCommentEntity2 {
    @Id
    private Long course_comment_id;
}
