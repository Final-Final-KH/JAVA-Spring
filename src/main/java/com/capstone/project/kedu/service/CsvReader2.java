package com.capstone.project.kedu.service;

import com.capstone.project.job.entity.JobEntity2;
import com.capstone.project.kedu.entity.edu.KeduEntity2;
import com.capstone.project.news.entity.NewsEntity2;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CsvReader2 {

    @Autowired
    private ResourceLoader resourceLoader;

    public List<NewsEntity2> newsCsv(String tech) throws Exception {
        List<NewsEntity2> newsList = new ArrayList<>();

        Resource resource = resourceLoader.getResource(tech);  // tech: classpath에서 리소스를 찾기 위한 경로
        try (InputStream inputStream = resource.getInputStream();
             InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withHeader().parse(reader);
            for (CSVRecord record : records) {
                NewsEntity2 newsEntity2 = new NewsEntity2();
                newsEntity2.setTitle(record.get("Title"));
                newsEntity2.setLink(record.get("Link"));
                newsEntity2.setContent(record.get("Content"));
                newsEntity2.setCategory(record.get("Category"));
                newsEntity2.setReporter(record.get("Reporter"));
                newsEntity2.setDate(record.get("Date"));
                newsList.add(newsEntity2);
            }
        }
        return newsList;
    }

    public List<JobEntity2> jobCsv(String jobData) throws Exception {
        List<JobEntity2> jobList = new ArrayList<>();

        Resource resource = resourceLoader.getResource(jobData);  // jobData: classpath에서 리소스를 찾기 위한 경로
        try (InputStream inputStream = resource.getInputStream();
             InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withHeader().parse(reader);
            for (CSVRecord record : records) {
                JobEntity2 jobEntity2 = new JobEntity2();
                jobEntity2.setCompany(record.get("Company"));
                jobEntity2.setTitle(record.get("Title"));
                jobEntity2.setLink(record.get("Link"));
                jobEntity2.setJob(record.get("Job"));
                jobEntity2.setLocation(record.get("Location"));
                jobList.add(jobEntity2);
            }
        }
        return jobList;
    }

    public List<KeduEntity2> readCsv(String filePath) throws Exception {
        List<KeduEntity2> courseList = new ArrayList<>();
        Resource resource = resourceLoader.getResource(filePath);  // filePath: classpath:/csv/kedu.csv
        try (InputStream inputStream = resource.getInputStream();
             InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withHeader().parse(reader);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            for (CSVRecord record : records) {
                KeduEntity2 course = new KeduEntity2();
                course.setAcademy_name(record.get("academy_name"));
                course.setCourse_name(record.get("course_name"));
                course.setStart_date(parseDate(record.get("start_date"), dateFormat));
                course.setEnd_date(parseDate(record.get("end_date"), dateFormat));
                course.setRegion(record.get("region"));
                course.setAuth(record.get("auth"));
                course.setTr_date(parseInt(record.get("tr_date")));
                course.setTotal_hour(parseInt(record.get("total_hour")));
                course.setEmployment_rate(parseInt(record.get("employment_rate")));
                course.setPrice_total(parseInt(record.get("price_total")));
                course.setSelf_payment(parseInt(record.get("self_payment")));
                courseList.add(course);
            }
        }
        return courseList;
    }

    // 문자열을 Date로 변환하는 메서드
    private Date parseDate(String dateString, SimpleDateFormat dateFormat) {
        try {
            return dateFormat.parse(dateString);
        } catch (Exception e) {
            return null; // 날짜 형식 오류 시 null 반환
        }
    }

    // 문자열을 정수로 변환하는 메서드
    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0; // 숫자 형식 오류 시 0 반환
        }
    }
}
