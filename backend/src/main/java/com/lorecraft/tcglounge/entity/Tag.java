package com.lorecraft.tcglounge.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tags")
@EntityListeners(AuditingEntityListener.class)
public class Tag {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;
    
    @Column(name = "slug", nullable = false, unique = true, length = 50)
    private String slug;
    
    @Column(name = "description", length = 200)
    private String description;
    
    @Column(name = "color", length = 7)
    private String color = "#007bff";
    
    @Column(name = "usage_count")
    private Integer usageCount = 0;
    
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    // Many-to-Many relationship with Articles
    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private Set<Article> articles = new HashSet<>();
    
    public Tag() {}
    
    public Tag(String name, String slug) {
        this.name = name;
        this.slug = slug;
    }
    
    public Tag(String name, String slug, String description, String color) {
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.color = color;
    }
    
    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getSlug() { return slug; }
    public String getDescription() { return description; }
    public String getColor() { return color; }
    public Integer getUsageCount() { return usageCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Set<Article> getArticles() { return articles; }
    
    // Setters
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setSlug(String slug) { this.slug = slug; }
    public void setDescription(String description) { this.description = description; }
    public void setColor(String color) { this.color = color; }
    public void setUsageCount(Integer usageCount) { this.usageCount = usageCount; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setArticles(Set<Article> articles) { this.articles = articles; }
    
    // Helper methods
    public void incrementUsageCount() {
        this.usageCount = (this.usageCount == null) ? 1 : this.usageCount + 1;
    }
    
    public void decrementUsageCount() {
        if (this.usageCount != null && this.usageCount > 0) {
            this.usageCount--;
        }
    }
    
    // Generate slug from name
    public static String generateSlug(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "";
        }
        return name.toLowerCase()
                   .replaceAll("[^a-z0-9가-힣\\s-]", "")
                   .replaceAll("\\s+", "-")
                   .replaceAll("-+", "-")
                   .replaceAll("^-|-$", "");
    }
    
    @PrePersist
    @PreUpdate
    private void generateSlugFromName() {
        if (this.slug == null || this.slug.trim().isEmpty()) {
            this.slug = generateSlug(this.name);
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tag)) return false;
        Tag tag = (Tag) o;
        return id != null && id.equals(tag.getId());
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    
    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", slug='" + slug + '\'' +
                ", usageCount=" + usageCount +
                '}';
    }
}