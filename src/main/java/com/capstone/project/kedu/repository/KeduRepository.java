package com.capstone.project.kedu.repository;

import com.capstone.project.kedu.entity.KeduEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KeduRepository extends JpaRepository<KeduEntity, Long> {
}
