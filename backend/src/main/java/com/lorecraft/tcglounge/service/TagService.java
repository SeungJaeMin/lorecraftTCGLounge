package com.lorecraft.tcglounge.service;

import com.lorecraft.tcglounge.entity.Tag;
import com.lorecraft.tcglounge.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TagService {
    
    @Autowired
    private TagRepository tagRepository;
    
    // 태그 생성
    public Tag createTag(Tag tag) {
        // 슬러그가 없으면 이름으로부터 생성
        if (tag.getSlug() == null || tag.getSlug().trim().isEmpty()) {
            tag.setSlug(Tag.generateSlug(tag.getName()));
        }
        
        // 중복 검사
        if (tagRepository.existsByName(tag.getName())) {
            throw new RuntimeException("이미 존재하는 태그명입니다: " + tag.getName());
        }
        
        if (tagRepository.existsBySlug(tag.getSlug())) {
            throw new RuntimeException("이미 존재하는 태그 슬러그입니다: " + tag.getSlug());
        }
        
        return tagRepository.save(tag);
    }
    
    // 태그 수정
    public Tag updateTag(Long id, Tag updatedTag) {
        Optional<Tag> existingTag = tagRepository.findById(id);
        
        if (existingTag.isEmpty()) {
            throw new RuntimeException("태그를 찾을 수 없습니다. ID: " + id);
        }
        
        Tag tag = existingTag.get();
        
        // 이름 변경 시 중복 검사
        if (!tag.getName().equals(updatedTag.getName())) {
            if (tagRepository.existsByName(updatedTag.getName())) {
                throw new RuntimeException("이미 존재하는 태그명입니다: " + updatedTag.getName());
            }
            tag.setName(updatedTag.getName());
        }
        
        // 슬러그 변경 시 중복 검사
        if (updatedTag.getSlug() != null && !tag.getSlug().equals(updatedTag.getSlug())) {
            if (tagRepository.existsBySlug(updatedTag.getSlug())) {
                throw new RuntimeException("이미 존재하는 태그 슬러그입니다: " + updatedTag.getSlug());
            }
            tag.setSlug(updatedTag.getSlug());
        } else if (updatedTag.getSlug() == null || updatedTag.getSlug().trim().isEmpty()) {
            // 슬러그가 없으면 이름으로부터 새로 생성
            tag.setSlug(Tag.generateSlug(tag.getName()));
        }
        
        // 기타 속성 업데이트
        if (updatedTag.getDescription() != null) {
            tag.setDescription(updatedTag.getDescription());
        }
        
        if (updatedTag.getColor() != null) {
            tag.setColor(updatedTag.getColor());
        }
        
        return tagRepository.save(tag);
    }
    
    // 태그 삭제
    public void deleteTag(Long id) {
        Optional<Tag> tag = tagRepository.findById(id);
        
        if (tag.isEmpty()) {
            throw new RuntimeException("태그를 찾을 수 없습니다. ID: " + id);
        }
        
        // 연관된 게시글이 있으면 삭제 불가 (실제로는 관계만 제거하고 태그는 삭제)
        tagRepository.deleteById(id);
    }
    
    // 태그 조회
    @Transactional(readOnly = true)
    public Optional<Tag> findById(Long id) {
        return tagRepository.findById(id);
    }
    
    // 이름으로 태그 조회
    @Transactional(readOnly = true)
    public Optional<Tag> findByName(String name) {
        return tagRepository.findByName(name);
    }
    
    // 슬러그로 태그 조회
    @Transactional(readOnly = true)
    public Optional<Tag> findBySlug(String slug) {
        return tagRepository.findBySlug(slug);
    }
    
    // 모든 태그 조회
    @Transactional(readOnly = true)
    public List<Tag> findAll() {
        return tagRepository.findAll();
    }
    
    // 태그 목록 조회 (페이지네이션)
    @Transactional(readOnly = true)
    public Page<Tag> findAll(Pageable pageable) {
        return tagRepository.findAll(pageable);
    }
    
    // 이름 기준 정렬 조회
    @Transactional(readOnly = true)
    public List<Tag> findAllOrderByName() {
        return tagRepository.findAllByOrderByNameAsc();
    }
    
    // 사용 빈도 기준 정렬 조회
    @Transactional(readOnly = true)
    public List<Tag> findAllOrderByUsage() {
        return tagRepository.findAllByOrderByUsageCountDesc();
    }
    
    // 인기 태그 조회
    @Transactional(readOnly = true)
    public List<Tag> findPopularTags(Pageable pageable) {
        return tagRepository.findPopularTags(pageable);
    }
    
    // 태그 검색
    @Transactional(readOnly = true)
    public List<Tag> searchTags(String keyword) {
        return tagRepository.findByNameContainingIgnoreCase(keyword);
    }
    
    // 태그 검색 (페이지네이션)
    @Transactional(readOnly = true)
    public Page<Tag> searchTags(String keyword, Pageable pageable) {
        return tagRepository.findByNameContainingIgnoreCase(keyword, pageable);
    }
    
    // 사용되지 않은 태그 조회
    @Transactional(readOnly = true)
    public List<Tag> findUnusedTags() {
        return tagRepository.findUnusedTags();
    }
    
    // 특정 게시글의 태그 조회
    @Transactional(readOnly = true)
    public List<Tag> findTagsByArticleId(Long articleId) {
        return tagRepository.findByArticleId(articleId);
    }
    
    // 태그 사용 통계 조회
    @Transactional(readOnly = true)
    public List<Object[]> getTagUsageStatistics() {
        return tagRepository.getTagUsageStatistics();
    }
    
    // 태그 이름으로 존재 여부 확인
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return tagRepository.existsByName(name);
    }
    
    // 태그 슬러그로 존재 여부 확인
    @Transactional(readOnly = true)
    public boolean existsBySlug(String slug) {
        return tagRepository.existsBySlug(slug);
    }
    
    // 태그명들로부터 태그 엔티티 목록 생성 또는 조회
    public List<Tag> findOrCreateTagsByNames(List<String> tagNames) {
        return tagNames.stream()
                .map(this::findOrCreateTag)
                .toList();
    }
    
    // 태그명으로 태그 조회 또는 생성
    public Tag findOrCreateTag(String tagName) {
        Optional<Tag> existingTag = tagRepository.findByName(tagName.trim());
        
        if (existingTag.isPresent()) {
            return existingTag.get();
        }
        
        // 새 태그 생성
        Tag newTag = new Tag();
        newTag.setName(tagName.trim());
        newTag.setSlug(Tag.generateSlug(tagName.trim()));
        
        return tagRepository.save(newTag);
    }
    
    // 사용하지 않는 태그들 정리
    @Transactional
    public int cleanupUnusedTags() {
        List<Tag> unusedTags = findUnusedTags();
        tagRepository.deleteAll(unusedTags);
        return unusedTags.size();
    }
    
    // 태그 사용 횟수 재계산
    @Transactional
    public void recalculateUsageCount(Long tagId) {
        Optional<Tag> tagOpt = tagRepository.findById(tagId);
        
        if (tagOpt.isPresent()) {
            Tag tag = tagOpt.get();
            int actualUsageCount = tag.getArticles().size();
            tag.setUsageCount(actualUsageCount);
            tagRepository.save(tag);
        }
    }
    
    // 모든 태그의 사용 횟수 재계산
    @Transactional
    public void recalculateAllUsageCounts() {
        List<Tag> allTags = tagRepository.findAll();
        
        for (Tag tag : allTags) {
            int actualUsageCount = tag.getArticles().size();
            tag.setUsageCount(actualUsageCount);
        }
        
        tagRepository.saveAll(allTags);
    }
}