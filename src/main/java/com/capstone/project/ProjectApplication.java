package com.capstone.project;

import com.capstone.project.kedu.entity.KeduEntity2;
import com.capstone.project.kedu.repository.AcademyRepository2;
import com.capstone.project.kedu.repository.KeduRepository2;
import com.capstone.project.kedu.service.CsvReader2;
import com.capstone.project.kedu.service.KeduService2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class ProjectApplication {

	@Autowired
	private CsvReader2 csvReader2; // CSV 파일을 읽는 서비스

	@Autowired
	private KeduService2 keduService2; // DB에 데이터를 저장하는 서비스

	@Autowired
	private KeduRepository2 keduRepository2;

	@Autowired
	private AcademyRepository2 academyRepository2;

	public static void main(String[] args) {
		SpringApplication.run(ProjectApplication.class, args);
	}

	// CommandLineRunner: 애플리케이션이 시작될 때 실행되는 코드
	@Bean
	public CommandLineRunner run() {
		return args -> {
			// CSV 파일 경로
			String filePath = "classpath:/static/csv/course.csv";

			// CSV 파일을 읽어 List<KeduEntity2>로 변환
			List<KeduEntity2> csvData = csvReader2.readCsv(filePath);

			// CSV 데이터를 DB에 저장, 중복 체크 및 저장
			for (KeduEntity2 course : csvData) {
				// 중복 체크: 이미 DB에 해당 강의가 존재하는지 확인 (예: courseId가 유일하다고 가정)
				Optional<KeduEntity2> existingCourse = Optional.empty();

				// course.getCourseId()가 null이 아닌지 확인
				if (course.getCourse_id() != null) {
					existingCourse = keduRepository2.findById(course.getCourse_id());
				}

				if (!existingCourse.isPresent()) {
					// 존재하지 않으면 새로운 데이터 추가
					keduRepository2.save(course);
				} else {
					// 존재하면 데이터를 업데이트하지 않고 무시
					System.out.println("Duplicate course found, skipping: " + course.getCourse_name());
				}
			}

			// 데이터를 저장한 후 쿼리 실행 (예시: 학원과 강좌의 중복 없는 목록)
			List<Object[]> result = keduRepository2.findDistinctAcademyAndCourse();
			result.forEach(row -> {
				System.out.println(row[0] + " - " + row[1]);
			});


			keduService2.saveAcademy(); // 학원 데이터 저장
			keduService2.saveCourse();  // 강좌 데이터 저장
			keduService2.saveRegion();
			//keduService2.saveDistrict();
		};
	}
}
