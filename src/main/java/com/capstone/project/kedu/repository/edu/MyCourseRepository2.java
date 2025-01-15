package com.capstone.project.kedu.repository.edu;

import com.capstone.project.kedu.entity.edu.MyCourseEntity2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface MyCourseRepository2 extends JpaRepository<MyCourseEntity2, Long> {
    @Transactional
    @Modifying
    @Query("DELETE FROM MyCourseEntity2 m WHERE m.member.id = :memberId AND m.list_id = :listId")
    void deleteByMemberIdAndListId(Long listId, Integer memberId);  // memberId는 Integer로 변경

    List<MyCourseEntity2> findByMemberId(int memberId);

    MyCourseEntity2 findByMember_IdAndAcademyEntity2_AcademyIdAndCourseEntity2_CourseId(int memberId, Long academyId, Long courseId);

    boolean existsByMember_IdAndAcademyEntity2_AcademyId(int memberId, Long academyId);
}
