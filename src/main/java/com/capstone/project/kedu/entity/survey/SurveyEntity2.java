package com.capstone.project.kedu.entity.survey;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "survey")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class SurveyEntity2 {
    @Id
    private Long survey_id;
}
