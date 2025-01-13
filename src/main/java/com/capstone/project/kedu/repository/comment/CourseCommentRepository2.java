package com.capstone.project.kedu.repository.comment;

import com.capstone.project.kedu.entity.comment.CourseCommentEntity2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseCommentRepository2 extends JpaRepository<CourseCommentEntity2, Long> {
    List<CourseCommentEntity2> findAllByCourseEntity2_CourseId(long courseId);

    List<CourseCommentEntity2> findByMemberId(int memberId);
}
