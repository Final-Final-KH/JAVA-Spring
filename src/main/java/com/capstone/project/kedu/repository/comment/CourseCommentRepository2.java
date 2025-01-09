package com.capstone.project.kedu.repository.comment;

import com.capstone.project.kedu.entity.comment.CourseCommentEntity2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseCommentRepository2 extends JpaRepository<CourseCommentEntity2, Long> {
}
