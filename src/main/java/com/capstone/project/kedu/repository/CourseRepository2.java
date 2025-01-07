package com.capstone.project.kedu.repository;

import com.capstone.project.kedu.entity.AcademyEntity2;
import com.capstone.project.kedu.entity.CourseEntity2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRepository2 extends JpaRepository<CourseEntity2, Long> {
//    @Query("SELECT DISTINCT c FROM CourseEntity c WHERE c.academy.academy_name =:academyName AND c.region = :region AND c.course_name =:courseName")
//    Optional<AcademyEntity2> findByCourse(
//            @Param("academyName") String academyName
//            , @Param("region") String region
//            , @Param("courseName")String courseName);
Optional<CourseEntity2> findByCourseNameAndAcademyAndRegion(String courseName, String academy, String region);


}
