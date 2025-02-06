package com.capstone.project.kedu.repository.edu;

import com.capstone.project.kedu.entity.edu.MyAcademyEntity2;
import com.capstone.project.kedu.entity.edu.MyCourseEntity2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface MyAcademyRepository2 extends JpaRepository<MyAcademyEntity2, Long> {
    List<MyAcademyEntity2> findByMemberId(int memberId);

    boolean existsByMember_IdAndAcademyEntity2_AcademyId(int memberId, Long academyId);
    @Transactional
    @Modifying
    @Query("DELETE FROM MyAcademyEntity2 m WHERE m.member.id = :memberId AND m.list_id = :listId")
    void deleteByMemberIdAndListId(Long listId, Integer memberId);
}
