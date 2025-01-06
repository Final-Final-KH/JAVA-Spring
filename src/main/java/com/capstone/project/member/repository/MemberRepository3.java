package com.capstone.project.member.repository;

import com.capstone.project.member.entity.Member3;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository3 extends JpaRepository<Member3, Long> {
    Optional<Member3> findByEmail(String email);
    Optional<Member3> findById(String id);
    boolean existsByEmail(String email);
}