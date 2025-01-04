package com.capstone.project.kedu.service;

import com.capstone.project.kedu.dto.KeduResDTO2;
import com.capstone.project.kedu.entity.KeduEntity2;
import com.capstone.project.kedu.repository.KeduRepository2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class KeduService2 {

    @Autowired
    private KeduRepository2 repository;

    public void saveCourses(List<KeduEntity2> courses) {
        repository.saveAll(courses); // List를 DB에 저장
    }
    public List<KeduResDTO2>findAllCourse(){
        List<KeduEntity2> course = repository.findAll();
        List<KeduResDTO2> keduResDTO2List = new ArrayList<>();

        for(KeduEntity2 keduEntity2 : course) {
            keduResDTO2List.add(convertEntityToDtoWithoutComments(keduEntity2));
        }
        return keduResDTO2List;
    }
    // 댓글 제외 DTO 변환 메서드
    private KeduResDTO2 convertEntityToDtoWithoutComments(KeduEntity2 kedu) {
        KeduResDTO2 keduResDTO2 = new KeduResDTO2();

        // KeduEntity2에서 KeduResDTO2로 데이터 매핑
        keduResDTO2.setCourseId(kedu.getCourse_id());
        keduResDTO2.setAcademyName(kedu.getAcademy_name());
        keduResDTO2.setCourseName(kedu.getCourse_name());
        keduResDTO2.setStartDate(kedu.getStart_date());
        keduResDTO2.setEndDate(kedu.getEnd_date());
        keduResDTO2.setRegion(kedu.getRegion());
        keduResDTO2.setAuth(kedu.getAuth());
        keduResDTO2.setTrDate(kedu.getTr_date());
        keduResDTO2.setTotalHour(kedu.getTotal_hour());
        keduResDTO2.setEmploymentRate(kedu.getEmployment_rate());
        keduResDTO2.setPriceTotal(kedu.getPrice_total());
        keduResDTO2.setSelfPayment(kedu.getSelf_payment());

        return keduResDTO2;
    }

}
