package com.capstone.project.news;

import com.capstone.project.config.PaymentConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class NewsController {

    @Autowired
    private PaymentConfig paymentConfig;

    // Flask API에서 뉴스 가져오기
    @GetMapping("/news")
    public String getNews(@RequestParam(required = false) String search) {
        // PaymentConfig 클래스에서 제공하는 메서드를 사용하여 뉴스 데이터를 가져옵니다.
        return paymentConfig.getNewsFromFlaskApi(search);
    }
}
