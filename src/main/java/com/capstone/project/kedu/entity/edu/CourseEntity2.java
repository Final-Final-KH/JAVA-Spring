package com.capstone.project.kedu.entity.edu;

import com.capstone.project.kedu.entity.comment.CourseCommentEntity2;
import com.capstone.project.kedu.entity.survey.SurveyEntity2;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "course")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class CourseEntity2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long courseId;

    private String academy;

    @Column(nullable = false, name = "course_name")
    private String courseName; // 강의명

    private String region;

    @OneToMany(mappedBy = "courseEntity2", cascade = CascadeType.ALL, orphanRemoval = true )
    private List<MyCourseEntity2> myCourse = new ArrayList<>();

    @OneToMany(mappedBy = "courseEntity2", cascade = CascadeType.ALL, orphanRemoval = true )
    private List<SurveyEntity2> survey = new ArrayList<>();

    @OneToMany(mappedBy = "courseEntity2", cascade = CascadeType.ALL, orphanRemoval = true )
    private List<CourseCommentEntity2> courseReview = new ArrayList<>();

    // 학원의 평점 계산을 위한 필드들
    private double totalScore = 0.0;  // 총점
    private int ratingCount = 0;      // 평점 개수
    private double averageScore = 0.0;  // 평균 점수

    // 평점 추가 메서드 (새로운 평점이 들어오면 totalScore, ratingCount, averageScore 갱신)
    public void addRating(double score) {
        this.totalScore += score;          // 총점 갱신
        this.ratingCount++;                // 평점 개수 갱신
        this.averageScore = totalScore / ratingCount;  // 평균 점수 갱신
    }

    // 평점 계산을 위해 평균 점수 갱신하는 메서드
    public void updateAverageScore() {
        if (ratingCount > 0) {
            this.averageScore = totalScore / ratingCount; // 총점 / 평점 개수
        } else {
            this.averageScore = 0.0; // 평점이 하나도 없으면 0으로 설정
        }
    }
}
