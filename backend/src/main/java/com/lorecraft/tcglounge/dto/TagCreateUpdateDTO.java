package com.lorecraft.tcglounge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

public class TagCreateUpdateDTO {
    
    @NotBlank(message = "태그명은 필수입니다")
    @Size(max = 50, message = "태그명은 50자를 초과할 수 없습니다")
    private String name;
    
    @Size(max = 100, message = "슬러그는 100자를 초과할 수 없습니다")
    @Pattern(regexp = "^[a-z0-9가-힣\\-]*$", message = "슬러그는 소문자, 숫자, 한글, 하이픈만 허용됩니다")
    private String slug;
    
    @Size(max = 500, message = "설명은 500자를 초과할 수 없습니다")
    private String description;
    
    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "색상은 #RRGGBB 형식이어야 합니다")
    private String color;
    
    public TagCreateUpdateDTO() {}
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
}