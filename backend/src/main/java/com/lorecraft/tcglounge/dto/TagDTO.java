package com.lorecraft.tcglounge.dto;

import com.lorecraft.tcglounge.entity.Tag;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class TagDTO {
    
    private Long id;
    private String name;
    private String slug;
    private String description;
    private String color;
    private Integer usageCount;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    public TagDTO() {}
    
    public TagDTO(Tag tag) {
        this.id = tag.getId();
        this.name = tag.getName();
        this.slug = tag.getSlug();
        this.description = tag.getDescription();
        this.color = tag.getColor();
        this.usageCount = tag.getUsageCount();
        this.createdAt = tag.getCreatedAt();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    
    public Integer getUsageCount() { return usageCount; }
    public void setUsageCount(Integer usageCount) { this.usageCount = usageCount; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}