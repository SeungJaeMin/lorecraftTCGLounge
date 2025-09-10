package com.lorecraft.tcglounge.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "articles")
@EntityListeners(AuditingEntityListener.class)
public class Article {
    
    public enum ArticleStatus {
        PUBLIC, PRIVATE
    }
    
    public enum ArticleCategory {
        NEWS, ANNOUNCEMENT, PRODUCT_INFO, UPDATE, EVENT
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "title", nullable = false, length = 500)
    private String title;
    
    @Column(name = "content", nullable = false, columnDefinition = "LONGTEXT")
    private String content;
    
    @Column(name = "summary", length = 1000)
    private String summary;
    
    @Column(name = "author", nullable = false, length = 100)
    private String author;
    
    @Column(name = "author_id")
    private Long authorId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ArticleStatus status = ArticleStatus.PRIVATE;
    
    @Column(name = "publish_date")
    private LocalDateTime publishDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private ArticleCategory category;
    
    @Column(name = "featured")
    private Boolean featured = false;
    
    @Column(name = "views_count")
    private Integer viewsCount = 0;
    
    @Column(name = "likes_count")
    private Integer likesCount = 0;
    
    @Column(name = "thumbnail_url")
    private String thumbnailUrl;
    
    @Column(name = "meta_title", length = 60)
    private String metaTitle;
    
    @Column(name = "meta_description", length = 160)
    private String metaDescription;
    
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Many-to-Many relationship with Tags
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "article_tags",
        joinColumns = @JoinColumn(name = "article_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();
    
    // Many-to-One relationship with Admin (author)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", insertable = false, updatable = false)
    private Admin adminAuthor;
    
    public Article() {}
    
    public Article(String title, String content, String author, ArticleCategory category) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.category = category;
    }
    
    // Getters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getSummary() { return summary; }
    public String getAuthor() { return author; }
    public Long getAuthorId() { return authorId; }
    public ArticleStatus getStatus() { return status; }
    public LocalDateTime getPublishDate() { return publishDate; }
    public ArticleCategory getCategory() { return category; }
    public Boolean getFeatured() { return featured; }
    public Integer getViewsCount() { return viewsCount; }
    public Integer getLikesCount() { return likesCount; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public String getMetaTitle() { return metaTitle; }
    public String getMetaDescription() { return metaDescription; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public Set<Tag> getTags() { return tags; }
    public Admin getAdminAuthor() { return adminAuthor; }
    
    // Setters
    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setSummary(String summary) { this.summary = summary; }
    public void setAuthor(String author) { this.author = author; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
    public void setStatus(ArticleStatus status) { this.status = status; }
    public void setPublishDate(LocalDateTime publishDate) { this.publishDate = publishDate; }
    public void setCategory(ArticleCategory category) { this.category = category; }
    public void setFeatured(Boolean featured) { this.featured = featured; }
    public void setViewsCount(Integer viewsCount) { this.viewsCount = viewsCount; }
    public void setLikesCount(Integer likesCount) { this.likesCount = likesCount; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
    public void setMetaTitle(String metaTitle) { this.metaTitle = metaTitle; }
    public void setMetaDescription(String metaDescription) { this.metaDescription = metaDescription; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setTags(Set<Tag> tags) { this.tags = tags; }
    public void setAdminAuthor(Admin adminAuthor) { this.adminAuthor = adminAuthor; }
    
    // Helper methods
    public void addTag(Tag tag) {
        this.tags.add(tag);
        tag.getArticles().add(this);
    }
    
    public void removeTag(Tag tag) {
        this.tags.remove(tag);
        tag.getArticles().remove(this);
    }
    
    public void incrementViewsCount() {
        this.viewsCount = (this.viewsCount == null) ? 1 : this.viewsCount + 1;
    }
    
    public void incrementLikesCount() {
        this.likesCount = (this.likesCount == null) ? 1 : this.likesCount + 1;
    }
    
    public boolean isPublic() {
        return ArticleStatus.PUBLIC.equals(this.status);
    }
    
    public boolean isPrivate() {
        return ArticleStatus.PRIVATE.equals(this.status);
    }
}