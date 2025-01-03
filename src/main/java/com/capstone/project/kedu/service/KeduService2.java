package com.capstone.project.kedu.service;

import com.capstone.project.kedu.entity.KeduEntity2;
import com.capstone.project.kedu.repository.KeduRepository2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KeduService2 {

    @Autowired
    private KeduRepository2 repository;

    public void saveCourses(List<KeduEntity2> courses) {
        repository.saveAll(courses); // List를 DB에 저장
    }
}
