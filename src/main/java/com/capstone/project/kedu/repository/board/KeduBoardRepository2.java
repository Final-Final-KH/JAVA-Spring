package com.capstone.project.kedu.repository.board;

import com.capstone.project.kedu.dto.board.KeduBoardReqDTO2;
import com.capstone.project.kedu.entity.board.KeduBoardEntity2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KeduBoardRepository2 extends JpaRepository<KeduBoardEntity2, Long> {

    @Query("SELECT k FROM KeduBoardEntity2 k WHERE k.academyEntity2.academyId = :academyId AND k.courseEntity2.courseId = :courseId")
    List<KeduBoardEntity2> findByAcademyIdAndCourseId(@Param("academyId") Long academyId, @Param("courseId") Long courseId);
    @Modifying
    @Query("DELETE FROM KeduBoardEntity2 k WHERE k.id = :id AND k.member.id = :memberId")
    void deleteByIdAndMemberId(@Param("id") Long id, @Param("memberId") int memberId);

    List<KeduBoardEntity2> findByMemberId(int memberId);
}


