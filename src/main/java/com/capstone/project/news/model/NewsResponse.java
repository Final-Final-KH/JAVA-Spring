package com.capstone.project.news.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class NewsResponse {
    private List<Item> items;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public static class Item {
        private String title;
        private String originallink;
        private String link;
        private String description;
        private String pubDate;

        // Getters and Setters
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getOriginallink() {
            return originallink;
        }

        public void setOriginallink(String originallink) {
            this.originallink = originallink;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getPubDate() {
            return pubDate;
        }

        public void setPubDate(String pubDate) {
            this.pubDate = pubDate;
        }
    }
}
