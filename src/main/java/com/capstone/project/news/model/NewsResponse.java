package com.capstone.project.news.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NewsResponse {
    private List<NewsItem> items;
}
