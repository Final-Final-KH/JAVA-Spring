package com.capstone.project.kedu.service;

import com.capstone.project.kedu.entity.KeduEntity2;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import java.io.FileReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class CsvReader2 {

    @Autowired
    private ResourceLoader resourceLoader;

    // CSV 파일을 읽어서 KeduEntity2 리스트로 변환
    public List<KeduEntity2> readCsv(String filePath) throws Exception {
        List<KeduEntity2> courseList = new ArrayList<>();

        // ResourceLoader를 통해 CSV 파일 로딩
        Resource resource = resourceLoader.getResource(filePath);  // filePath는 classpath:/csv/kedu.csv
        Reader in = new FileReader(resource.getFile());  // File 객체로 변환
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withHeader().parse(in);

        // 날짜 포맷 설정 (CSV 파일에서 읽은 날짜 포맷이 맞지 않을 경우 포맷을 설정)
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

        // CSV 파일의 각 레코드에 대해 KeduEntity2 객체로 변환
        for (CSVRecord record : records) {
            KeduEntity2 course = new KeduEntity2();

            // 각 컬럼 데이터를 KeduEntity2 객체에 매핑
            course.setAcademy_name(record.get("academy_name"));
            course.setCourse_name(record.get("course_name"));

            // 날짜를 변환하여 설정
            course.setStart_date(parseDate(record.get("start_date"), dateFormat));
            course.setEnd_date(parseDate(record.get("end_date"), dateFormat));
            course.setRegion(record.get("region"));
            course.setAuth(record.get("auth"));
            course.setTr_date(parseInt(record.get("tr_date")));

            // 숫자 데이터 매핑
            course.setTotal_hour(parseInt(record.get("total_hour")));
            course.setEmployment_rate(parseInt(record.get("employment_rate")));
            course.setPrice_total(parseInt(record.get("price_total")));
            course.setSelf_payment(parseInt(record.get("self_payment")));

            // 리스트에 추가
            courseList.add(course);
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
