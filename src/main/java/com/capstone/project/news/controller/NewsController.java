package com.capstone.project.news.controller;

import com.capstone.project.news.model.NewsResponse;
import com.capstone.project.news.service.NewsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class NewsController {
    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping("/api/news")
    public Object getNews(
            @RequestParam(defaultValue = "IT") String category,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "1") int page) {
        log.error(search);
        return newsService.getNews(category, search, page);
    }
}
