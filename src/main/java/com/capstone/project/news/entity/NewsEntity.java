package com.capstone.project.news.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "news")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class NewsEntity {
    @Id
    private Long news_id;
}
