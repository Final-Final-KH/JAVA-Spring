package com.capstone.project.kedu.dto.comment;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class AcademyCommentResDTOTest2 {

    @Test
    public void 롬복_기능_테스트() {
        // Given: DTO의 필드 값을 준비합니다.
        Long academyCommentId = 1L;
        Long academyId = 100L;
        int memberId = 123;
        double avgJob = 90.5;
        double avgLecture = 85.0;
        double avgFacilities = 88.0;
        double avgTeacher = 92.0;
        double avgBooks = 86.5;
        double avgService = 87.0;
        double totalAvg = 88.0;

        // When: AcademyCommentResDTO2 객체를 생성합니다.
        AcademyCommentResDTO2 dto = new AcademyCommentResDTO2(
                academyCommentId, academyId, memberId, avgJob, avgLecture,
                avgFacilities, avgTeacher, avgBooks, avgService, totalAvg
        );

        // Then: 필드 값이 제대로 설정되었는지 확인합니다.
        assertThat(dto.getAcademy_comment_id()).isEqualTo(academyCommentId);
        assertThat(dto.getAcademy_id()).isEqualTo(academyId);
        assertThat(dto.getMember_id()).isEqualTo(memberId);
        assertThat(dto.getAvgJob()).isEqualTo(avgJob);
        assertThat(dto.getAvgLecture()).isEqualTo(avgLecture);
        assertThat(dto.getAvgFacilities()).isEqualTo(avgFacilities);
        assertThat(dto.getAvgTeacher()).isEqualTo(avgTeacher);
        assertThat(dto.getAvgBooks()).isEqualTo(avgBooks);
        assertThat(dto.getAvgService()).isEqualTo(avgService);
        assertThat(dto.getTotalAvg()).isEqualTo(totalAvg);

        // 또한 Lombok의 toString 메서드를 테스트할 수도 있습니다.
        assertThat(dto.toString()).contains("academy_comment_id=" + academyCommentId);
        assertThat(dto.toString()).contains("avgJob=" + avgJob);
    }
}
