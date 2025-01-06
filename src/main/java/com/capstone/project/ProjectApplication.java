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
import java.util.Map;

@SpringBootApplication
public class ProjectApplication {

	@Autowired
	private CsvReader2 csvReader2; // CSV 파일을 읽는 서비스

	@Autowired
	private KeduService2 keduService2; // DB에 데이터를 저장하는 서비스

	@Autowired
	private KeduRepository2 keduRepository2;

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

			// CSV 데이터를 DB에 저장
			keduService2.saveCourses(csvData);

			// 데이터를 저장한 후 쿼리 실행
			List<Object[]> result = keduRepository2.findDistinctAcademyAndCourse();
			result.forEach(row -> {
				System.out.println(row[0] + " - " + row[1]);
			});

			keduService2.saveAcademy();
			keduService2.saveCourse();
		};
	}
}