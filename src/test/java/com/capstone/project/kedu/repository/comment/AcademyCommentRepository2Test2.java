package com.capstone.project.kedu.repository.comment;

import com.capstone.project.kedu.entity.comment.AcademyCommentEntity2;
import com.capstone.project.kedu.entity.edu.AcademyEntity2;
import com.capstone.project.kedu.repository.edu.AcademyRepository2;  // AcademyEntity2 저장을 위한 리포지토리 추가
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Slf4j
public class AcademyCommentRepository2Test2 {

    @Autowired
    private AcademyCommentRepository2 academyCommentRepository2;

    @Autowired
    private AcademyRepository2 academyRepository2;  // AcademyEntity2 저장용 리포지토리

    // @AfterEach는 JUnit 5에서 @After를 대신하여 사용됩니다.
    @AfterEach
    public void cleanup() {
        academyCommentRepository2.deleteAll();  // 데이터베이스 정리
    }

    @Test
    public void 평점_저장_불러오기() {
        // Given: 테스트 데이터 준비
        AcademyEntity2 academy = new AcademyEntity2();
        academy.setAcademyName("한국IT");
        academy.setRegion("서울시 강남구");

        // Academy 저장
        academy = academyRepository2.save(academy);  // academyRepository2를 사용하여 저장

        AcademyCommentEntity2 comment = AcademyCommentEntity2.builder()
                .job(90)
                .lecture(85)
                .teacher(100)
                .facilities(100)
                .books(99)
                .service(100)
                .academyEntity2(academy)  // academyEntity2를 연결
                .build();

        // When: 데이터 저장
        academyCommentRepository2.save(comment);

        // Then: 저장된 데이터 검증
        AcademyCommentEntity2 savedComment = academyCommentRepository2.findById(comment.getAcademy_comment_id()).orElse(null);

        assertThat(savedComment).isNotNull();
        assertThat(savedComment.getJob()).isEqualTo(90);
        assertThat(savedComment.getLecture()).isEqualTo(85);
        assertThat(savedComment.getTeacher()).isEqualTo(100);
        assertThat(savedComment.getFacilities()).isEqualTo(100);
        assertThat(savedComment.getBooks()).isEqualTo(99);
        assertThat(savedComment.getService()).isEqualTo(100);
        assertThat(savedComment.getAcademyEntity2().getAcademyName()).isEqualTo("한국IT");  // "한국IT"으로 검증
    }

    @Test
    @DisplayName("아카데미 평점 순위 메기기")
    public void findAllOrderByTotalScoreDesc(){
        List<AcademyEntity2> number1Academy = academyRepository2.findAllByOrderByTotalScoreDesc();
        for(AcademyEntity2 academyEntity2 : number1Academy){
            log.info("학원 평점에 대한 순위 메기기 : {}", academyEntity2);
        }
    }
}
