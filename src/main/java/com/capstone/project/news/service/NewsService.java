package com.capstone.project.news.service;

import com.capstone.project.news.model.NewsResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class NewsService {

    private final String FLASK_API_URL = "http://localhost:5000/news";

    public NewsResponse getNews(String category, String search, int page) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            // URL 생성
            String url = UriComponentsBuilder.fromHttpUrl(FLASK_API_URL)
                    .queryParam("category", category)
                    .queryParam("search", search)
                    .queryParam("page", page)
                    .toUriString();

            System.out.println("Request URL: " + url);  // URL 출력

            // Flask API 호출 및 응답을 NewsResponse 객체로 변환하여 반환
            return restTemplate.getForObject(url, NewsResponse.class);
        } catch (Exception e) {
            // 예외 처리 (로깅 등)
            throw new RuntimeException("Failed to fetch news from Flask API", e);
        }
    }
}