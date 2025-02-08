package com.capstone.project;

import com.capstone.project.job.entity.JobEntity2;
import com.capstone.project.job.repository.JobRepository2;
import com.capstone.project.kedu.entity.edu.KeduEntity2;
import com.capstone.project.kedu.repository.edu.AcademyRepository2;
import com.capstone.project.kedu.repository.edu.KeduRepository2;
import com.capstone.project.kedu.service.CsvReader2;
import com.capstone.project.kedu.service.KeduService2;
import com.capstone.project.news.entity.NewsEntity2;
import com.capstone.project.news.repository.NewsRepository2;
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

	@Autowired
	private JobRepository2 jobRepository2;

	@Autowired
	private NewsRepository2 newsRepository2;

	public static void main(String[] args) {
		SpringApplication.run(ProjectApplication.class, args);
	}

	// CommandLineRunner: 애플리케이션이 시작될 때 실행되는 코드
//	@Bean
//	public CommandLineRunner run() {
//		return args -> {
//			// CSV 파일 경로
//			String filePath = "classpath:/static/csv/course.csv";
//			String jobFilePath = "classpath:/static/csv/job_list.csv";
//			String tech01 = "classpath:/static/csv/tech01.csv";
//			String tech02 = "classpath:/static/csv/tech02.csv";
//			String tech03 = "classpath:/static/csv/tech03.csv";
//			String tech04 = "classpath:/static/csv/tech04.csv";
//			String tech05 = "classpath:/static/csv/tech05.csv";
//
//			// CSV 파일을 읽어 List<KeduEntity2>로 변환
//			List<KeduEntity2> csvData = csvReader2.readCsv(filePath);
//			List<JobEntity2> jobData = csvReader2.jobCsv(jobFilePath);
//			List<NewsEntity2> newsData1 = csvReader2.newsCsv(tech01);
//			List<NewsEntity2> newsData2 = csvReader2.newsCsv(tech02);
//			List<NewsEntity2> newsData3 = csvReader2.newsCsv(tech03);
//			List<NewsEntity2> newsData4 = csvReader2.newsCsv(tech04);
//			List<NewsEntity2> newsData5 = csvReader2.newsCsv(tech05);
//
//			// CSV 데이터를 DB에 저장, 중복 체크 및 저장
//
//			// newsData를 저장하기 위한 중복 체크 로직
//			for (NewsEntity2 newsEntity : newsData1) {
//				Optional<NewsEntity2> existingNews = Optional.empty();
//				if (newsEntity.getNews_id() != null) {
//					existingNews = newsRepository2.findById(newsEntity.getNews_id());
//				}
//				if (!existingNews.isPresent()) {
//					newsRepository2.save(newsEntity);
//				} else {
//					System.out.println("Duplicate news found, skipping");
//				}
//			}
//
//// 다른 newsData들에 대해서도 동일한 방식으로 중복 체크 후 저장
//			for (NewsEntity2 newsEntity : newsData2) {
//				Optional<NewsEntity2> existingNews = Optional.empty();
//				if (newsEntity.getNews_id() != null) {
//					existingNews = newsRepository2.findById(newsEntity.getNews_id());
//				}
//				if (!existingNews.isPresent()) {
//					newsRepository2.save(newsEntity);
//				} else {
//					System.out.println("Duplicate news found, skipping");
//				}
//			}
//
//			for (NewsEntity2 newsEntity : newsData3) {
//				Optional<NewsEntity2> existingNews = Optional.empty();
//				if (newsEntity.getNews_id() != null) {
//					existingNews = newsRepository2.findById(newsEntity.getNews_id());
//				}
//				if (!existingNews.isPresent()) {
//					newsRepository2.save(newsEntity);
//				} else {
//					System.out.println("Duplicate news found, skipping");
//				}
//			}
//
//			for (NewsEntity2 newsEntity : newsData4) {
//				Optional<NewsEntity2> existingNews = Optional.empty();
//				if (newsEntity.getNews_id() != null) {
//					existingNews = newsRepository2.findById(newsEntity.getNews_id());
//				}
//				if (!existingNews.isPresent()) {
//					newsRepository2.save(newsEntity);
//				} else {
//					System.out.println("Duplicate news found, skipping");
//				}
//			}
//
//			for (NewsEntity2 newsEntity : newsData5) {
//				Optional<NewsEntity2> existingNews = Optional.empty();
//				if (newsEntity.getNews_id() != null) {
//					existingNews = newsRepository2.findById(newsEntity.getNews_id());
//				}
//				if (!existingNews.isPresent()) {
//					newsRepository2.save(newsEntity);
//				} else {
//					System.out.println("Duplicate news found, skipping");
//				}
//			}
//
//			for(JobEntity2 jobEntity2 : jobData) {
//				Optional<JobEntity2> existingJob = Optional.empty();
//				if(jobEntity2.getJob_list_id() != null) {
//					existingJob = jobRepository2.findById(jobEntity2.getJob_list_id());
//				}
//				if(!existingJob.isPresent()){
//					jobRepository2.save(jobEntity2);
//				}else {
//					System.out.println("Duplicate course found, skipping");
//				}
//			}
//			for (KeduEntity2 course : csvData) {
//				// 중복 체크: 이미 DB에 해당 강의가 존재하는지 확인 (예: courseId가 유일하다고 가정)
//				Optional<KeduEntity2> existingCourse = Optional.empty();
//
//				// course.getCourseId()가 null이 아닌지 확인
//				if (course.getCourse_id() != null) {
//					existingCourse = keduRepository2.findById(course.getCourse_id());
//				}
//
//				if (!existingCourse.isPresent()) {
//					// 존재하지 않으면 새로운 데이터 추가
//					keduRepository2.save(course);
//				} else {
//					// 존재하면 데이터를 업데이트하지 않고 무시
//					System.out.println("Duplicate course found, skipping: " + course.getCourse_name());
//				}
//			}
//
//			// 데이터를 저장한 후 쿼리 실행 (예시: 학원과 강좌의 중복 없는 목록)
////			List<Object[]> result = keduRepository2.findDistinctAcademyAndCourse();
////			result.forEach(row -> {
////				System.out.println(row[0] + " - " + row[1]);
////			});
//
//
//			keduService2.saveAcademy(); // 학원 데이터 저장
//			keduService2.saveCourse();  // 강좌 데이터 저장
//			keduService2.saveRegion();
//			//keduService2.saveDistrict();
//		};
//	}
}
