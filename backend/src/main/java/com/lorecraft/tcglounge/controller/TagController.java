package com.lorecraft.tcglounge.controller;

import com.lorecraft.tcglounge.dto.TagDTO;
import com.lorecraft.tcglounge.entity.Tag;
import com.lorecraft.tcglounge.service.TagService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/tags")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class TagController {
    
    @Autowired
    private TagService tagService;
    
    // 모든 태그 조회 (공용)
    @GetMapping
    public ResponseEntity<List<TagDTO>> getAllTags(
            @RequestParam(defaultValue = "name") String sort) {
        
        List<Tag> tags;
        
        if ("usage".equals(sort)) {
            tags = tagService.findAllOrderByUsage();
        } else {
            tags = tagService.findAllOrderByName();
        }
        
        List<TagDTO> tagDTOs = tags.stream()
                .map(TagDTO::new)
                .toList();
        
        return ResponseEntity.ok(tagDTOs);
    }
    
    // 태그 목록 조회 (페이지네이션)
    @GetMapping("/paginated")
    public ResponseEntity<Page<TagDTO>> getTagsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        
        Sort sortObj = Sort.by(Sort.Direction.fromString(direction), sort);
        Pageable pageable = PageRequest.of(page, size, sortObj);
        
        Page<Tag> tagsPage = tagService.findAll(pageable);
        Page<TagDTO> tagDTOPage = tagsPage.map(TagDTO::new);
        
        return ResponseEntity.ok(tagDTOPage);
    }
    
    // 인기 태그 조회
    @GetMapping("/popular")
    public ResponseEntity<List<TagDTO>> getPopularTags(
            @RequestParam(defaultValue = "10") int limit) {
        
        Pageable pageable = PageRequest.of(0, limit);
        List<Tag> popularTags = tagService.findPopularTags(pageable);
        List<TagDTO> popularTagDTOs = popularTags.stream()
                .map(TagDTO::new)
                .toList();
        
        return ResponseEntity.ok(popularTagDTOs);
    }
    
    // 태그 검색
    @GetMapping("/search")
    public ResponseEntity<List<TagDTO>> searchTags(
            @RequestParam String keyword) {
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        List<Tag> tags = tagService.searchTags(keyword.trim());
        List<TagDTO> tagDTOs = tags.stream()
                .map(TagDTO::new)
                .toList();
        
        return ResponseEntity.ok(tagDTOs);
    }
    
    // 태그 검색 (페이지네이션)
    @GetMapping("/search/paginated")
    public ResponseEntity<Page<TagDTO>> searchTagsPaginated(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
        Page<Tag> tagsPage = tagService.searchTags(keyword.trim(), pageable);
        Page<TagDTO> tagDTOPage = tagsPage.map(TagDTO::new);
        
        return ResponseEntity.ok(tagDTOPage);
    }
    
    // 특정 태그 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<TagDTO> getTagById(@PathVariable Long id) {
        Optional<Tag> tag = tagService.findById(id);
        
        if (tag.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        TagDTO tagDTO = new TagDTO(tag.get());
        return ResponseEntity.ok(tagDTO);
    }
    
    // 슬러그로 태그 조회
    @GetMapping("/slug/{slug}")
    public ResponseEntity<TagDTO> getTagBySlug(@PathVariable String slug) {
        Optional<Tag> tag = tagService.findBySlug(slug);
        
        if (tag.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        TagDTO tagDTO = new TagDTO(tag.get());
        return ResponseEntity.ok(tagDTO);
    }
    
    // 태그 통계 조회
    @GetMapping("/statistics")
    public ResponseEntity<List<Object[]>> getTagStatistics() {
        List<Object[]> statistics = tagService.getTagUsageStatistics();
        return ResponseEntity.ok(statistics);
    }
}