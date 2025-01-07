package com.capstone.project.job.repository;

import com.capstone.project.job.entity.JobEntity2;
import com.capstone.project.kedu.entity.AcademyEntity2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository2 extends JpaRepository<JobEntity2, Long> {
}
