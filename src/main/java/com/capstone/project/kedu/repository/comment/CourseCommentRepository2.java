package com.capstone.project.kedu.repository.comment;

import com.capstone.project.kedu.dto.edu.response.CourseDetailResDTO2;
import com.capstone.project.kedu.entity.comment.CourseCommentEntity2;
import com.capstone.project.kedu.entity.edu.CourseEntity2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseCommentRepository2 extends JpaRepository<CourseCommentEntity2, Long> {
    List<CourseCommentEntity2> findAllByCourseEntity2_CourseId(long courseId);

    List<CourseCommentEntity2> findByMemberId(int memberId);

    @Query("select avg(c.job), avg(c.lecture), avg(c.books), avg(c.newTech), avg(c.skillUp), avg(c.teacher) " +
            "from CourseCommentEntity2 c where c.courseEntity2.courseId = :courseId")
    List<Object[]> findAverageScoreForCourse(@Param("courseId") Long courseId);

    @Query("SELECT new com.capstone.project.kedu.dto.edu.response.CourseDetailResDTO2(" +
            "cc.member.id, ac.job, ac.lecture, ac.facilities, ac.teacher, ac.books, ac.service, " +
            "cc.job, cc.lecture, cc.teacher, cc.books, cc.newTech, cc.skillUp, " +
            "kb.id, kb.title, kb.user_id, kb.regDate, kb.content, " +
            "s.survey_id, s.teacher, s.lecture, s.facilities, s.comment) " +
            "FROM CourseCommentEntity2 cc " +
            "LEFT JOIN AcademyCommentEntity2 ac ON ac.academyEntity2.academyId = cc.academyEntity2.academyId " +
            "LEFT JOIN KeduBoardEntity2 kb ON kb.courseEntity2.courseId = cc.courseEntity2.courseId " +
            "LEFT JOIN SurveyEntity2 s ON s.courseEntity2.courseId = cc.courseEntity2.courseId " +
            "WHERE cc.academyEntity2.academyId = :academyId AND cc.courseEntity2.courseId = :courseId " +
            "GROUP BY cc.member.id, ac.job, ac.lecture, ac.facilities, ac.teacher, ac.books, ac.service, " +
            "cc.job, cc.lecture, cc.teacher, cc.books, cc.newTech, cc.skillUp, " +
            "kb.id, kb.title, kb.user_id, kb.regDate, kb.content, " +
            "s.survey_id, s.teacher, s.lecture, s.facilities, s.comment")
    List<CourseDetailResDTO2> findReview(@Param("academyId") Long academyId, @Param("courseId") Long courseId);












}
