package com.lorecraft.tcglounge.dto;

import com.lorecraft.tcglounge.entity.Article;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public class ArticleCreateUpdateDTO {
    
    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 500, message = "제목은 500자를 초과할 수 없습니다")
    private String title;
    
    @NotBlank(message = "내용은 필수입니다")
    private String content;
    
    @Size(max = 1000, message = "요약은 1000자를 초과할 수 없습니다")
    private String summary;
    
    @NotBlank(message = "작성자는 필수입니다")
    @Size(max = 100, message = "작성자는 100자를 초과할 수 없습니다")
    private String author;
    
    private Long authorId;
    
    private String status; // PUBLIC, PRIVATE
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime publishDate;
    
    @NotNull(message = "카테고리는 필수입니다")
    private String category; // NEWS, ANNOUNCEMENT, PRODUCT_INFO, UPDATE, EVENT
    
    private Boolean featured = false;
    
    private String thumbnailUrl;
    
    @Size(max = 60, message = "메타 제목은 60자를 초과할 수 없습니다")
    private String metaTitle;
    
    @Size(max = 160, message = "메타 설명은 160자를 초과할 수 없습니다")
    private String metaDescription;
    
    private List<String> tagNames;
    
    public ArticleCreateUpdateDTO() {}
    
    // Entity로 변환하는 메서드
    public Article toEntity() {
        Article article = new Article();
        article.setTitle(this.title);
        article.setContent(this.content);
        article.setSummary(this.summary);
        article.setAuthor(this.author);
        article.setAuthorId(this.authorId);
        
        if (this.status != null) {
            try {
                article.setStatus(Article.ArticleStatus.valueOf(this.status));
            } catch (IllegalArgumentException e) {
                article.setStatus(Article.ArticleStatus.PRIVATE);
            }
        }
        
        article.setPublishDate(this.publishDate);
        
        if (this.category != null) {
            try {
                article.setCategory(Article.ArticleCategory.valueOf(this.category));
            } catch (IllegalArgumentException e) {
                // 기본값 설정
                article.setCategory(Article.ArticleCategory.NEWS);
            }
        }
        
        article.setFeatured(this.featured != null ? this.featured : false);
        article.setThumbnailUrl(this.thumbnailUrl);
        article.setMetaTitle(this.metaTitle);
        article.setMetaDescription(this.metaDescription);
        
        return article;
    }
    
    // 기존 Entity를 업데이트하는 메서드
    public void updateEntity(Article article) {
        if (this.title != null) article.setTitle(this.title);
        if (this.content != null) article.setContent(this.content);
        if (this.summary != null) article.setSummary(this.summary);
        if (this.author != null) article.setAuthor(this.author);
        if (this.authorId != null) article.setAuthorId(this.authorId);
        
        if (this.status != null) {
            try {
                article.setStatus(Article.ArticleStatus.valueOf(this.status));
            } catch (IllegalArgumentException e) {
                // 잘못된 상태값이면 무시
            }
        }
        
        if (this.publishDate != null) article.setPublishDate(this.publishDate);
        
        if (this.category != null) {
            try {
                article.setCategory(Article.ArticleCategory.valueOf(this.category));
            } catch (IllegalArgumentException e) {
                // 잘못된 카테고리값이면 무시
            }
        }
        
        if (this.featured != null) article.setFeatured(this.featured);
        if (this.thumbnailUrl != null) article.setThumbnailUrl(this.thumbnailUrl);
        if (this.metaTitle != null) article.setMetaTitle(this.metaTitle);
        if (this.metaDescription != null) article.setMetaDescription(this.metaDescription);
    }
    
    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    
    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getPublishDate() { return publishDate; }
    public void setPublishDate(LocalDateTime publishDate) { this.publishDate = publishDate; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public Boolean getFeatured() { return featured; }
    public void setFeatured(Boolean featured) { this.featured = featured; }
    
    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
    
    public String getMetaTitle() { return metaTitle; }
    public void setMetaTitle(String metaTitle) { this.metaTitle = metaTitle; }
    
    public String getMetaDescription() { return metaDescription; }
    public void setMetaDescription(String metaDescription) { this.metaDescription = metaDescription; }
    
    public List<String> getTagNames() { return tagNames; }
    public void setTagNames(List<String> tagNames) { this.tagNames = tagNames; }
}