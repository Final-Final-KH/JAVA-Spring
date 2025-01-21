package com.capstone.project.kedu.repository.comment;


import com.capstone.project.kedu.dto.edu.response.CourseDetailResDTO2;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Slf4j
public class CourseCommentRepository2Test2 {
//    @Autowired
//    private CourseCommentRepository2 courseCommentRepository2;

//    @Test
//    @DisplayName("조인으로 강의 상세 출력")
//    public void 조인으로_강의_상세_출력(){
//        List<CourseDetailResDTO2> list = courseCommentRepository2.findReview(1L,1L);
//        for(CourseDetailResDTO2 courseDetailResDTO2 : list){
//            log.info("강의 상세야 출력되어라 : {}", courseDetailResDTO2);
//        }
//    }
}
