package com.capstone.project.kedu.repository;

import com.capstone.project.kedu.entity.AcademyEntity2;
import com.capstone.project.kedu.entity.KeduEntity2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KeduRepository2 extends JpaRepository<KeduEntity2, Long> {
    // 추후 지역들 선택 후에 조회 하는 걸로 변경
    @Query("SELECT DISTINCT k.academy_name, k.region FROM KeduEntity2 k")
    List<Object[]> findDistinctAcademyAndCourse();

    // Native SQL query 사용하여 대도시만 추출
    @Query(value = "SELECT DISTINCT SUBSTRING_INDEX(region, ' ', 1) FROM kedu", nativeQuery = true)
    List<String> findDistinctCities();

}