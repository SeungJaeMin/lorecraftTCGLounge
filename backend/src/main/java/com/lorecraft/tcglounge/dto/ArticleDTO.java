package com.lorecraft.tcglounge.dto;

import com.lorecraft.tcglounge.entity.Article;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

public class ArticleDTO {
    
    private Long id;
    private String title;
    private String content;
    private String summary;
    private String author;
    private Long authorId;
    private String status;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime publishDate;
    
    private String category;
    private Boolean featured;
    private Integer viewsCount;
    private Integer likesCount;
    private String thumbnailUrl;
    private String metaTitle;
    private String metaDescription;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
    
    private List<TagDTO> tags;
    private String adminAuthorNickname;
    
    public ArticleDTO() {}
    
    public ArticleDTO(Article article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.summary = article.getSummary();
        this.author = article.getAuthor();
        this.authorId = article.getAuthorId();
        this.status = article.getStatus() != null ? article.getStatus().name() : null;
        this.publishDate = article.getPublishDate();
        this.category = article.getCategory() != null ? article.getCategory().name() : null;
        this.featured = article.getFeatured();
        this.viewsCount = article.getViewsCount();
        this.likesCount = article.getLikesCount();
        this.thumbnailUrl = article.getThumbnailUrl();
        this.metaTitle = article.getMetaTitle();
        this.metaDescription = article.getMetaDescription();
        this.createdAt = article.getCreatedAt();
        this.updatedAt = article.getUpdatedAt();
        
        if (article.getTags() != null) {
            this.tags = article.getTags().stream()
                    .map(TagDTO::new)
                    .toList();
        }
        
        // adminAuthor 로딩 제거 - 필요시에만 별도 조회
        // if (article.getAdminAuthor() != null) {
        //     this.adminAuthorNickname = article.getAdminAuthor().getNickname();
        // }
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
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
    
    public Integer getViewsCount() { return viewsCount; }
    public void setViewsCount(Integer viewsCount) { this.viewsCount = viewsCount; }
    
    public Integer getLikesCount() { return likesCount; }
    public void setLikesCount(Integer likesCount) { this.likesCount = likesCount; }
    
    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
    
    public String getMetaTitle() { return metaTitle; }
    public void setMetaTitle(String metaTitle) { this.metaTitle = metaTitle; }
    
    public String getMetaDescription() { return metaDescription; }
    public void setMetaDescription(String metaDescription) { this.metaDescription = metaDescription; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public List<TagDTO> getTags() { return tags; }
    public void setTags(List<TagDTO> tags) { this.tags = tags; }
    
    public String getAdminAuthorNickname() { return adminAuthorNickname; }
    public void setAdminAuthorNickname(String adminAuthorNickname) { this.adminAuthorNickname = adminAuthorNickname; }
}