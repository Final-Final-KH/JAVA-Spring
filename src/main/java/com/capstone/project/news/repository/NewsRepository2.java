package com.capstone.project.news.repository;

import com.capstone.project.news.entity.NewsEntity2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsRepository2 extends JpaRepository<NewsEntity2, Long> {
}
