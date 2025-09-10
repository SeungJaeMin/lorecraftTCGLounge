package com.lorecraft.tcglounge.controller;

import com.lorecraft.tcglounge.dto.TagDTO;
import com.lorecraft.tcglounge.entity.Tag;
import com.lorecraft.tcglounge.service.TagService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/v1/admin/tags")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AdminTagController {
    
    @Autowired
    private TagService tagService;
    
    // 관리자용 태그 목록 조회 (페이지네이션)
    @GetMapping
    public ResponseEntity<Page<TagDTO>> getAllTags(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "updatedAt") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String search) {
        
        Sort sortObj = Sort.by(Sort.Direction.fromString(direction), sort);
        Pageable pageable = PageRequest.of(page, size, sortObj);
        
        Page<Tag> tagsPage;
        
        if (search != null && !search.trim().isEmpty()) {
            tagsPage = tagService.searchTags(search.trim(), pageable);
        } else {
            tagsPage = tagService.findAll(pageable);
        }
        
        Page<TagDTO> tagDTOPage = tagsPage.map(TagDTO::new);
        
        return ResponseEntity.ok(tagDTOPage);
    }
    
    // 새 태그 생성
    @PostMapping
    public ResponseEntity<TagDTO> createTag(@Valid @RequestBody TagCreateUpdateDTO createDTO) {
        try {
            Tag tag = new Tag();
            tag.setName(createDTO.getName());
            tag.setSlug(createDTO.getSlug());
            tag.setDescription(createDTO.getDescription());
            tag.setColor(createDTO.getColor());
            
            Tag createdTag = tagService.createTag(tag);
            TagDTO tagDTO = new TagDTO(createdTag);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(tagDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // 태그 수정
    @PutMapping("/{id}")
    public ResponseEntity<TagDTO> updateTag(
            @PathVariable Long id,
            @Valid @RequestBody TagCreateUpdateDTO updateDTO) {
        
        try {
            Tag updatedTag = new Tag();
            updatedTag.setName(updateDTO.getName());
            updatedTag.setSlug(updateDTO.getSlug());
            updatedTag.setDescription(updateDTO.getDescription());
            updatedTag.setColor(updateDTO.getColor());
            
            Tag tag = tagService.updateTag(id, updatedTag);
            TagDTO tagDTO = new TagDTO(tag);
            
            return ResponseEntity.ok(tagDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // 태그 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        try {
            tagService.deleteTag(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // 사용하지 않는 태그 조회
    @GetMapping("/unused")
    public ResponseEntity<List<TagDTO>> getUnusedTags() {
        List<Tag> unusedTags = tagService.findUnusedTags();
        List<TagDTO> unusedTagDTOs = unusedTags.stream()
                .map(TagDTO::new)
                .toList();
        
        return ResponseEntity.ok(unusedTagDTOs);
    }
    
    // 사용하지 않는 태그 일괄 삭제
    @DeleteMapping("/unused")
    public ResponseEntity<Map<String, Integer>> cleanupUnusedTags() {
        try {
            int deletedCount = tagService.cleanupUnusedTags();
            return ResponseEntity.ok(Map.of("deletedCount", deletedCount));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // 태그 사용 횟수 재계산 (특정 태그)
    @PostMapping("/{id}/recalculate-usage")
    public ResponseEntity<TagDTO> recalculateUsageCount(@PathVariable Long id) {
        try {
            tagService.recalculateUsageCount(id);
            Optional<Tag> tag = tagService.findById(id);
            
            if (tag.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            TagDTO tagDTO = new TagDTO(tag.get());
            return ResponseEntity.ok(tagDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // 모든 태그 사용 횟수 재계산
    @PostMapping("/recalculate-usage")
    public ResponseEntity<Map<String, String>> recalculateAllUsageCounts() {
        try {
            tagService.recalculateAllUsageCounts();
            return ResponseEntity.ok(Map.of("message", "모든 태그의 사용 횟수가 재계산되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // 태그 이름 중복 검사
    @GetMapping("/check-name")
    public ResponseEntity<Map<String, Boolean>> checkTagName(@RequestParam String name) {
        boolean exists = tagService.existsByName(name);
        return ResponseEntity.ok(Map.of("exists", exists));
    }
    
    // 태그 슬러그 중복 검사
    @GetMapping("/check-slug")
    public ResponseEntity<Map<String, Boolean>> checkTagSlug(@RequestParam String slug) {
        boolean exists = tagService.existsBySlug(slug);
        return ResponseEntity.ok(Map.of("exists", exists));
    }
    
    // 태그 생성/수정용 DTO 클래스
    public static class TagCreateUpdateDTO {
        
        @NotBlank(message = "태그명은 필수입니다")
        @Size(max = 100, message = "태그명은 100자를 초과할 수 없습니다")
        private String name;
        
        @Size(max = 150, message = "슬러그는 150자를 초과할 수 없습니다")
        private String slug;
        
        @Size(max = 500, message = "설명은 500자를 초과할 수 없습니다")
        private String description;
        
        @Size(max = 7, message = "색상은 7자를 초과할 수 없습니다")
        private String color;
        
        // 기본 생성자
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
}