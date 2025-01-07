package com.capstone.project.kedu.repository;

import com.capstone.project.kedu.entity.AcademyEntity2;
import com.capstone.project.kedu.entity.CourseEntity2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRepository2 extends JpaRepository<CourseEntity2, Long> {

}
