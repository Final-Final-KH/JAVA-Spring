package com.capstone.project.news.service;

import com.capstone.project.news.model.NewsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NewsService {
    @Autowired
    private RestTemplate restTemplate;

    private static final String FLASK_API_URL = "http://localhost:5000/news"; // Flask API URL

    public NewsResponse getNewsFromFlaskApi(String searchQuery) {
        String url = FLASK_API_URL;
        if (searchQuery != null && !searchQuery.isEmpty()) {
            url += "?search=" + searchQuery;
        }
        return restTemplate.getForObject(url, NewsResponse.class);
    }
}
