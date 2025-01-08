package com.capstone.project.kedu.entity.survey;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "answer")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class SurveyAnswerEntity2 {
    @Id
    private Long answer_id;
}
