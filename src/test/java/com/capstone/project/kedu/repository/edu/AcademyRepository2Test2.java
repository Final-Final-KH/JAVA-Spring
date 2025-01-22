package com.capstone.project.kedu.repository.edu;

import com.capstone.project.kedu.entity.edu.AcademyEntity2;
import com.capstone.project.kedu.service.KeduService2;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static com.google.common.base.Verify.verify;
import static javax.management.Query.times;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Slf4j
public class AcademyRepository2Test2 {
    @Mock
    private AcademyRepository2 academyRepository2; // academyRepository를 모의 객체로 생성

    @InjectMocks
    private KeduService2 keduService2; // 실제 서비스 클래스

    @Test
    void testGetAcademyId() {
        // given
        String region = "서울 강남구";
        String academyName = "KH정보교육원 강남지원";

       Optional<Long> id = academyRepository2.findAcademy_IdByAcademyNameAndRegion(academyName, region);
        log.info("너의 id는? : {}", id.get());

    }
}
