package com.capstone.project.news.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "news")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class NewsEntity2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long news_id;

    private String title;

    private String link;
    
    @Lob
    private String content;

    private String category;

    private String reporter;

    private String date;
}
