package com.capstone.project.kedu.repository.comment;

import com.capstone.project.kedu.entity.comment.CourseCommentEntity2;
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


}
