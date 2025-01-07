package com.capstone.project.kedu.repository;

import com.capstone.project.kedu.entity.AcademyEntity2;
import com.capstone.project.kedu.entity.CityEntity2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityRepository2 extends JpaRepository<CityEntity2, Long> {
    List<CityEntity2> findByRegionName(String regionName);
}

