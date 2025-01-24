package com.capstone.project.myPage.repository;

import com.capstone.project.myPage.dto.MemberProfileResponseDto;
import com.capstone.project.myPage.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface ProfileRepository extends JpaRepository<Profile, Integer> {

    Optional<Profile> findByMemberId(Integer memberId);

    @Query("SELECT new com.capstone.project.myPage.dto.MemberProfileResponseDto(" +
            "m.id, m.email, m.name, m.phoneNumber, m.profilePictureUrl, m.currentCompany, m.showCompany, " +
            "p.location, " +
            "p.bio, " +
            "p.skills, " +
            "p.resumeUrl) " +
            "FROM Member m " +
            "LEFT JOIN Profile p ON m.id = p.memberId " +
            "WHERE m.id = :memberId")
    MemberProfileResponseDto findMemberProfileById(@Param("memberId") Integer memberId);
}