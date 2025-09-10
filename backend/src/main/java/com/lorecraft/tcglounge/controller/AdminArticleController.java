package com.lorecraft.tcglounge.controller;

import com.lorecraft.tcglounge.dto.ArticleDTO;
import com.lorecraft.tcglounge.dto.ArticleCreateUpdateDTO;
import com.lorecraft.tcglounge.entity.Article;
import com.lorecraft.tcglounge.entity.Tag;
import com.lorecraft.tcglounge.service.ArticleService;
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
@RequestMapping("/v1/admin/articles")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AdminArticleController {
    
    @Autowired
    private ArticleService articleService;
    
    @Autowired
    private TagService tagService;
    
    // 관리자용 전체 게시글 조회 (모든 상태 포함)
    @GetMapping
    public ResponseEntity<Page<ArticleDTO>> getAllArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "updatedAt") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long authorId) {
        
        Sort sortObj = Sort.by(Sort.Direction.fromString(direction), sort);
        Pageable pageable = PageRequest.of(page, size, sortObj);
        
        Page<Article> articlesPage;
        
        if (search != null && !search.trim().isEmpty()) {
            // 검색어가 있으면 검색 (모든 상태 포함)
            articlesPage = articleService.searchAllArticles(search.trim(), pageable);
        } else if (status != null && !status.trim().isEmpty()) {
            // 상태별 필터
            try {
                Article.ArticleStatus statusEnum = Article.ArticleStatus.valueOf(status.toUpperCase());
                articlesPage = articleService.findByStatus(statusEnum, pageable);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        } else if (category != null && !category.trim().isEmpty()) {
            // 카테고리별 필터 (모든 상태 포함)
            try {
                Article.ArticleCategory categoryEnum = Article.ArticleCategory.valueOf(category.toUpperCase());
                articlesPage = articleService.findAllByCategory(categoryEnum, pageable);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        } else if (authorId != null) {
            // 작성자별 필터
            articlesPage = articleService.findByAuthorId(authorId, pageable);
        } else {
            // 전체 게시글 (모든 상태)
            articlesPage = articleService.findAllArticles(pageable);
        }
        
        Page<ArticleDTO> articleDTOPage = articlesPage.map(ArticleDTO::new);
        
        return ResponseEntity.ok(articleDTOPage);
    }
    
    // 게시글 상세 조회 (관리자용 - 조회수 증가 없음)
    @GetMapping("/{id}")
    public ResponseEntity<ArticleDTO> getArticleById(@PathVariable Long id) {
        Optional<Article> article = articleService.findById(id);
        
        if (article.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        ArticleDTO articleDTO = new ArticleDTO(article.get());
        return ResponseEntity.ok(articleDTO);
    }
    
    // 새 게시글 작성
    @PostMapping
    public ResponseEntity<ArticleDTO> createArticle(@Valid @RequestBody ArticleCreateUpdateDTO createDTO) {
        try {
            Article article = createDTO.toEntity();
            
            // 태그 처리
            if (createDTO.getTagNames() != null && !createDTO.getTagNames().isEmpty()) {
                List<Tag> tags = tagService.findOrCreateTagsByNames(createDTO.getTagNames());
                article.getTags().addAll(tags);
            }
            
            Article savedArticle = articleService.createArticle(article);
            ArticleDTO articleDTO = new ArticleDTO(savedArticle);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(articleDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // 게시글 수정
    @PutMapping("/{id}")
    public ResponseEntity<ArticleDTO> updateArticle(
            @PathVariable Long id, 
            @Valid @RequestBody ArticleCreateUpdateDTO updateDTO) {
        
        try {
            Optional<Article> existingArticle = articleService.findById(id);
            
            if (existingArticle.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Article article = existingArticle.get();
            
            // 기존 태그들 제거
            article.getTags().clear();
            
            // DTO의 내용으로 엔티티 업데이트
            updateDTO.updateEntity(article);
            
            // 새로운 태그 처리
            if (updateDTO.getTagNames() != null && !updateDTO.getTagNames().isEmpty()) {
                List<Tag> tags = tagService.findOrCreateTagsByNames(updateDTO.getTagNames());
                article.getTags().addAll(tags);
            }
            
            Article updatedArticle = articleService.updateArticle(article);
            ArticleDTO articleDTO = new ArticleDTO(updatedArticle);
            
            return ResponseEntity.ok(articleDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        try {
            Optional<Article> article = articleService.findById(id);
            
            if (article.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            articleService.deleteArticle(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // 게시글 발행
    @PostMapping("/{id}/publish")
    public ResponseEntity<ArticleDTO> publishArticle(@PathVariable Long id) {
        try {
            Article publishedArticle = articleService.publishArticle(id);
            ArticleDTO articleDTO = new ArticleDTO(publishedArticle);
            return ResponseEntity.ok(articleDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // 게시글 보관
    @PostMapping("/{id}/archive")
    public ResponseEntity<ArticleDTO> archiveArticle(@PathVariable Long id) {
        try {
            Article archivedArticle = articleService.archiveArticle(id);
            ArticleDTO articleDTO = new ArticleDTO(archivedArticle);
            return ResponseEntity.ok(articleDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // 게시글 주요 뉴스 설정/해제
    @PostMapping("/{id}/feature")
    public ResponseEntity<ArticleDTO> toggleFeature(@PathVariable Long id) {
        try {
            Article article = articleService.toggleFeature(id);
            ArticleDTO articleDTO = new ArticleDTO(article);
            return ResponseEntity.ok(articleDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // 게시글 통계 조회
    @GetMapping("/statistics")
    public ResponseEntity<Object> getArticleStatistics() {
        try {
            Object statistics = articleService.getStatistics();
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // 초안 게시글 조회
    @GetMapping("/drafts")
    public ResponseEntity<Page<ArticleDTO>> getDraftArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        Page<Article> draftArticles = articleService.findByStatus(Article.ArticleStatus.PRIVATE, pageable);
        Page<ArticleDTO> draftDTOs = draftArticles.map(ArticleDTO::new);
        
        return ResponseEntity.ok(draftDTOs);
    }
    
    // 보관된 게시글 조회
    @GetMapping("/archived")
    public ResponseEntity<Page<ArticleDTO>> getArchivedArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        Page<Article> archivedArticles = articleService.findByStatus(Article.ArticleStatus.PRIVATE, pageable);
        Page<ArticleDTO> archivedDTOs = archivedArticles.map(ArticleDTO::new);
        
        return ResponseEntity.ok(archivedDTOs);
    }
}