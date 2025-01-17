package com.capstone.project.kedu.repository.comment;

import com.capstone.project.kedu.entity.comment.AcademyCommentEntity2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AcademyCommentRepository2 extends JpaRepository<AcademyCommentEntity2, Long> {
    List<AcademyCommentEntity2> findByAcademyEntity2AcademyId(Long id);

    List<AcademyCommentEntity2> findByMemberId(int memberId);

    @Query("SELECT (COUNT(c) * 100) / (SELECT COUNT(c2) FROM AcademyCommentEntity2 c2) FROM AcademyCommentEntity2 c WHERE c.employee_outcome = true")
    Optional<Integer> findEmploymentOutcomePercentage(Long academyId);

    @Query("SELECT AVG(c.job), AVG(c.lecture), AVG(c.facilities), AVG(c.teacher), AVG(c.books), AVG(c.service) " +
            "FROM AcademyCommentEntity2 c WHERE c.academyEntity2.academyId = :academyId")
    List<Object[]> findAverageScoresForAcademy(Long academyId);
}
