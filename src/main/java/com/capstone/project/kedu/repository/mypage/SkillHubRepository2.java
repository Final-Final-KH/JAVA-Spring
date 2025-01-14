package com.capstone.project.kedu.repository.mypage;

import com.capstone.project.kedu.entity.mypage.SkillHubEntity2;
import com.capstone.project.kedu.entity.mypage.SkillType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillHubRepository2 extends JpaRepository<SkillHubEntity2, Long> {
    List<SkillHubEntity2> findByMemberId(int memberId);

//    List<SkillHubEntity2> findByMemberIdAndDate(int memberId);
}
