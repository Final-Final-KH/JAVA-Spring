package com.capstone.project.kedu.repository.survey;

import com.capstone.project.kedu.entity.survey.SurveyEntity2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveyRepository2 extends JpaRepository<SurveyEntity2, Long> {

    List<SurveyEntity2> findByAcademyEntity2AcademyIdAndCourseEntity2CourseId(Long academyId, Long courseId);
}
