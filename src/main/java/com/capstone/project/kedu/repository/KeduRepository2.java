package com.capstone.project.kedu.repository;

import com.capstone.project.kedu.entity.KeduEntity2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KeduRepository2 extends JpaRepository<KeduEntity2, Long> {
}
