package com.capstone.project.kedu.repository.edu;

import com.capstone.project.kedu.entity.edu.AcademyEntity2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AcademyRepository2 extends JpaRepository<AcademyEntity2, Long> {
    Optional<AcademyEntity2> findByAcademyNameAndRegion(String academyName, String region);
    List<AcademyEntity2> findByRegion(String region);

    List<AcademyEntity2> findAllByOrderByTotalScoreDesc();

    Optional<Long> findAcademyIdByAcademyNameAndRegion(String academyName, String region);

    @Query("SELECT academyId FROM AcademyEntity2 WHERE academyName = :academyName AND region = :region")
    Optional<Long> findAcademy_IdByAcademyNameAndRegion(@Param("academyName") String academyName, @Param("region") String region);
}
