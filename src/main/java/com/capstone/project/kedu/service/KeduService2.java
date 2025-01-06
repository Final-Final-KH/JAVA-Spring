package com.capstone.project.kedu.service;

import com.capstone.project.kedu.dto.KeduResDTO2;
import com.capstone.project.kedu.entity.AcademyEntity2;
import com.capstone.project.kedu.entity.CourseEntity2;
import com.capstone.project.kedu.entity.KeduEntity2;
import com.capstone.project.kedu.repository.AcademyRepository2;
import com.capstone.project.kedu.repository.CourseRepository2;
import com.capstone.project.kedu.repository.KeduRepository2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class KeduService2 {

    @Autowired
    private KeduRepository2 repository;
    @Autowired
    private AcademyRepository2 academyRepository;

    @Autowired
    private CourseRepository2 courseRepository;

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

    @Transactional
    public void saveAcademy() {

        List<Object[]> distinctAcademies = repository.findDistinctAcademyAndCourse();

        // 반환된 값에서 데이터를 추출하여 AcademyEntity2 객체로 저장
        for (Object[] row : distinctAcademies) {
            String academyName = (String) row[0]; // academy_name
            String region = (String) row[1]; // region

            AcademyEntity2 academyEntity2 = new AcademyEntity2();
            academyEntity2.setAcademyName(academyName);
            academyEntity2.setRegion(region);

            academyRepository.save(academyEntity2); // DB에 저장

        }
    }

    @Transactional
    public void saveCourse() {
        List<KeduEntity2> keduList = repository.findAll();

        for (KeduEntity2 keduEntity : keduList) {
            // 교육기관이 DB에 존재하는지 확인
            AcademyEntity2 academyEntity = academyRepository.findByAcademyNameAndRegion(keduEntity.getAcademy_name(), keduEntity.getRegion())
                    .orElseThrow(() -> new RuntimeException("Academy not found"));

            // 새로운 강좌를 만들어서 저장
            CourseEntity2 courseEntity = new CourseEntity2();
            courseEntity.setCourseName(keduEntity.getCourse_name());
            courseEntity.setAuth(keduEntity.getAuth());
            courseEntity.setStartDate(keduEntity.getStart_date());
            courseEntity.setEndDate(keduEntity.getEnd_date());
            courseEntity.setTotalHour(keduEntity.getTotal_hour());
            courseEntity.setPriceTotal(keduEntity.getPrice_total());
            courseEntity.setSelfPayment(keduEntity.getSelf_payment());

            // AcademyEntity2와 관계 맺기
            courseEntity.setAcademy(academyEntity);  // AcademyEntity2와 연결

            courseRepository.save(courseEntity);  // 강좌 저장
        }
    }
}
