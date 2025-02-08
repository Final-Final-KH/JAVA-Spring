package com.capstone.project.kedu.repository.edu;

import com.capstone.project.kedu.dto.edu.response.CourseDetailResDTO2;
import com.capstone.project.kedu.entity.edu.CourseEntity2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository2 extends JpaRepository<CourseEntity2, Long> {

    Optional<CourseEntity2> findByCourseNameAndAcademyAndRegion(String courseName, String academy, String region);

    List<CourseEntity2> findByAcademyAndRegion(String academy, String region);

}
