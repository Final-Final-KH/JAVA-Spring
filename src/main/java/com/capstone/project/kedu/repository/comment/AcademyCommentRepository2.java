package com.capstone.project.kedu.repository.comment;

import com.capstone.project.kedu.entity.comment.AcademyCommentEntity2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AcademyCommentRepository2 extends JpaRepository<AcademyCommentEntity2, Long> {
}
