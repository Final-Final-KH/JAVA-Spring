package com.capstone.project.kedu.service;

import com.capstone.project.kedu.dto.edu.response.*;
import com.capstone.project.kedu.entity.board.KeduBoardEntity2;
import com.capstone.project.kedu.entity.comment.AcademyCommentEntity2;
import com.capstone.project.kedu.entity.comment.CourseCommentEntity2;
import com.capstone.project.kedu.entity.edu.AcademyEntity2;
import com.capstone.project.kedu.entity.edu.CityEntity2;
import com.capstone.project.kedu.entity.edu.CourseEntity2;
import com.capstone.project.kedu.entity.edu.KeduEntity2;
import com.capstone.project.kedu.entity.survey.SurveyEntity2;
import com.capstone.project.kedu.repository.comment.AcademyCommentRepository2;
import com.capstone.project.kedu.repository.comment.CourseCommentRepository2;
import com.capstone.project.kedu.repository.edu.AcademyRepository2;
import com.capstone.project.kedu.repository.edu.CityRepository2;
import com.capstone.project.kedu.repository.edu.CourseRepository2;
import com.capstone.project.kedu.repository.edu.KeduRepository2;
import com.capstone.project.kedu.repository.survey.SurveyRepository2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class KeduService2 {

    @Autowired
    private KeduRepository2 repository;

    @Autowired
    private AcademyRepository2 academyRepository;

    @Autowired
    private CourseRepository2 courseRepository;

    @Autowired
    private CityRepository2 cityRepository2;

    @Autowired
    private AcademyCommentRepository2 academyCommentRepository2;

    @Autowired
    private CourseCommentRepository2 courseCommentRepository2;

    @Autowired
    private SurveyRepository2 surveyRepository2;


    public List<CourseDetailResDTO2> detail(Long academyId, Long courseId) {
        // courseCommentRepository2에서 데이터를 가져옴 (리스트로)
        return courseCommentRepository2.findReview(academyId, courseId);
    }
    public List<KeduResDTO2> getCourseList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<KeduEntity2> keduEntity2s = repository.findAll(pageable).getContent();
        List<KeduResDTO2> keduResDTO2s = new ArrayList<>();
        for (KeduEntity2 keduEntity2 : keduEntity2s){
            keduResDTO2s.add(convertEntityToDtoWithoutComments(keduEntity2));
        }
        return keduResDTO2s;
    }
    public List<KeduResDTO2>findAllCourse(){
        List<KeduEntity2> course = repository.findAll();
        List<KeduResDTO2> keduResDTO2List = new ArrayList<>();

        for(KeduEntity2 keduEntity2 : course) {
            keduResDTO2List.add(convertEntityToDtoWithoutComments(keduEntity2));
        }
        return keduResDTO2List;
    }

    public void saveCourse() {
        List<Object[]>distinctCourse = repository.findDistinctCourse();
        for(Object[] row : distinctCourse){
            String academyName = (String) row[0]; // academy_name
            String region = (String) row[1]; // region
            String course = (String) row[2];

            // 기존 학원이 존재하는지 확인
            Optional<CourseEntity2> existAcademy = courseRepository
                    .findByCourseNameAndAcademyAndRegion(course,academyName, region);
            if(!existAcademy.isPresent()){
                CourseEntity2 courseEntity2 = new CourseEntity2();
                courseEntity2.setCourseName(course);
                courseEntity2.setAcademy(academyName);
                courseEntity2.setRegion(region);

                courseRepository.save(courseEntity2);

            }else {
                // 존재하면 데이터를 업데이트하지 않고 무시
                System.out.println("Duplicate academy found, skipping: ");
            }
        }
    }
    @Transactional
    public void saveAcademy() {
        List<Object[]> distinctAcademies = repository.findDistinctAcademyAndCourse();

        for (Object[] row : distinctAcademies) {
            String academyName = (String) row[0]; // academy_name
            String region = (String) row[1]; // region

            // 기존 학원이 존재하는지 확인
            Optional<AcademyEntity2> existAcademy = academyRepository.findByAcademyNameAndRegion(academyName, region);

            // 만약 학원이 존재하지 않으면 새로 추가
            if (!existAcademy.isPresent()) {
                AcademyEntity2 academyEntity = new AcademyEntity2();
                academyEntity.setAcademyName(academyName);
                academyEntity.setRegion(region);

                academyRepository.save(academyEntity); // DB에 저장
            } else {
                // 존재하면 데이터를 업데이트하지 않고 무시
                System.out.println("Duplicate academy found, skipping: " + academyName);
            }
        }
    }



    @Transactional
    public void saveRegion() {
        List<String> regions = repository.findDistinctCities();  // 지역 목록 가져오기

        for (String regionName : regions) {
            // 해당 지역이 이미 존재하는지 확인
            List<CityEntity2> existRegion = cityRepository2.findByRegionName(regionName);  // 메소드 이름 수정

            // 존재하지 않으면 새로운 데이터 추가
            if (existRegion.isEmpty()) {
                CityEntity2 cityEntity2 = new CityEntity2();
                cityEntity2.setRegionName(regionName);  // 지역 이름을 설정

                // 데이터 저장
                cityRepository2.save(cityEntity2);
            } else {
                // 이미 존재하면 데이터를 추가하지 않음
                System.out.println("Duplicate region found, skipping: " + regionName);
            }
        }
    }



    public List<AcademyResDTO2> findAllAcademy() {
        List<AcademyEntity2> academy = academyRepository.findAll();
        List<AcademyResDTO2> academyResDTO2List = new ArrayList<>();
        for(AcademyEntity2 academyEntity2 : academy){
            academyResDTO2List.add(convertEntityToAcademyResDto(academyEntity2));
        }
        return academyResDTO2List;
    }

    public List<RegionResDTO2> findAllRegion() {
        List<String> region = repository.findDistinctCities();
        List<RegionResDTO2> regionResDTO2List = new ArrayList<>();

        for (String city : region){
            RegionResDTO2 regionResDTO2 = new RegionResDTO2();
            regionResDTO2.setCity(city);
            regionResDTO2List.add(regionResDTO2);
        }
        return regionResDTO2List;
    }

    public List<DistrictResDTO2> findByRegionDistrict(String region) {
        List<String> district = repository.findByRegionDistrict(region);
        List<DistrictResDTO2> districtResDTO2List = new ArrayList<>();

        for (String gu : district){
            DistrictResDTO2 districtResDTO2 = new DistrictResDTO2();
            districtResDTO2.setDistrict_name(gu);
            districtResDTO2List.add(districtResDTO2);
        }
        return districtResDTO2List;
    }
    public List<AcademyResDTO2> findAcadey(String region) {
        List<AcademyEntity2> academyEntity = academyRepository.findByRegion(region);
        List<AcademyResDTO2> academyResDTO2List = new ArrayList<>();
        for(AcademyEntity2 academyEntity2 : academyEntity){
            academyResDTO2List.add(convertEntityToAcademyResDto(academyEntity2));
        }
        return academyResDTO2List;
    }
    public Long getAcademyId(String region, String academyName) {
        return academyRepository.findAcademyIdByAcademyNameAndRegion(academyName, region);
    }
    public List<LectureResDTO2> findLecture(String region, String academy) {
        List<CourseEntity2> courseEntity = courseRepository.findByAcademyAndRegion(academy, region);
        List<LectureResDTO2> lectureResDTO2List = new ArrayList<>();
        for(CourseEntity2 courseEntity2 : courseEntity) {
            lectureResDTO2List.add(convertEntityToLectureResDto(courseEntity2));
        }
        return lectureResDTO2List;
    }

    public Integer getBoards(PageRequest pageRequest) {
        return repository.findAll(pageRequest).getTotalPages();
    }

    public LectureResDTO2 convertEntityToLectureResDto(CourseEntity2 courseEntity){
        LectureResDTO2 lectureResDTO2 = new LectureResDTO2();
        lectureResDTO2.setCourse_id(courseEntity.getCourseId());
        lectureResDTO2.setAcademy(courseEntity.getAcademy());
        lectureResDTO2.setCourse_name(courseEntity.getCourseName());
        lectureResDTO2.setRegion(courseEntity.getRegion());
        return lectureResDTO2;
    }

    public AcademyResDTO2 convertEntityToAcademyResDto(AcademyEntity2 academy) {
        AcademyResDTO2 academyResDTO2 = new AcademyResDTO2();

        academyResDTO2.setAcademy_id(academy.getAcademyId());
        academyResDTO2.setAcademy_name(academy.getAcademyName());
        academyResDTO2.setRegion(academy.getRegion());

        return academyResDTO2;
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