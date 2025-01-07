package com.capstone.project.kedu.repository;

import com.capstone.project.kedu.entity.AcademyEntity2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AcademyRepository2 extends JpaRepository<AcademyEntity2, Long> {

    Optional<AcademyEntity2> findByAcademyNameAndRegion(String academyName, String region);

}
