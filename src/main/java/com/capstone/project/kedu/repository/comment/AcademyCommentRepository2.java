package com.capstone.project.kedu.repository.comment;

import com.capstone.project.kedu.entity.comment.AcademyCommentEntity2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AcademyCommentRepository2 extends JpaRepository<AcademyCommentEntity2, Long> {
    List<AcademyCommentEntity2> findByAcademyEntity2AcademyId(Long id);

    List<AcademyCommentEntity2> findByMemberId(int memberId);
}
