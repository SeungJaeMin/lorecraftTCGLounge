package com.lorecraft.tcglounge.controller;

import com.lorecraft.tcglounge.dto.ArticleDTO;
import com.lorecraft.tcglounge.entity.Article;
import com.lorecraft.tcglounge.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/articles")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ArticleController {
    
    @Autowired
    private ArticleService articleService;
    
    // 게시된 게시글 목록 조회 (페이지네이션)
    @GetMapping
    public ResponseEntity<Page<ArticleDTO>> getPublishedArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false, defaultValue = "false") boolean featured) {
        
        Sort sortObj = Sort.by(Sort.Direction.fromString(direction), sort);
        Pageable pageable = PageRequest.of(page, size, sortObj);
        
        Page<Article> articlesPage;
        
        if (search != null && !search.trim().isEmpty()) {
            // 검색어가 있으면 검색
            articlesPage = articleService.searchPublishedArticles(search.trim(), pageable);
        } else if (tag != null && !tag.trim().isEmpty()) {
            // 태그 필터
            articlesPage = articleService.findByTag(tag.trim(), pageable);
        } else if (category != null && !category.trim().isEmpty()) {
            // 카테고리 필터
            try {
                Article.ArticleCategory categoryEnum = Article.ArticleCategory.valueOf(category.toUpperCase());
                articlesPage = articleService.findByCategory(categoryEnum, pageable);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        } else if (featured) {
            // 주요 뉴스 - 별도의 서비스 메소드 사용
            articlesPage = articleService.findFeaturedPublishedArticles(pageable);
        } else {
            // 전체 게시된 게시글
            articlesPage = articleService.findPublishedArticles(pageable);
        }
        
        Page<ArticleDTO> articleDTOPage = articlesPage.map(ArticleDTO::new);
        
        return ResponseEntity.ok(articleDTOPage);
    }
    
    // 주요 뉴스 조회 (리스트 형태)
    @GetMapping("/featured")
    public ResponseEntity<List<ArticleDTO>> getFeaturedArticles() {
        List<Article> featuredArticles = articleService.findFeaturedArticles();
        List<ArticleDTO> featuredDTOs = featuredArticles.stream()
                .map(ArticleDTO::new)
                .toList();
        
        return ResponseEntity.ok(featuredDTOs);
    }
    
    // 인기 게시글 조회
    @GetMapping("/popular")
    public ResponseEntity<Page<ArticleDTO>> getPopularArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Article> popularArticles = articleService.findPopularArticles(pageable);
        Page<ArticleDTO> popularDTOs = popularArticles.map(ArticleDTO::new);
        
        return ResponseEntity.ok(popularDTOs);
    }
    
    // 카테고리별 게시글 조회
    @GetMapping("/category/{category}")
    public ResponseEntity<Page<ArticleDTO>> getArticlesByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        try {
            Article.ArticleCategory categoryEnum = Article.ArticleCategory.valueOf(category.toUpperCase());
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<Article> articlesPage = articleService.findByCategory(categoryEnum, pageable);
            Page<ArticleDTO> articleDTOPage = articlesPage.map(ArticleDTO::new);
            
            return ResponseEntity.ok(articleDTOPage);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 태그별 게시글 조회
    @GetMapping("/tag/{tagSlug}")
    public ResponseEntity<Page<ArticleDTO>> getArticlesByTag(
            @PathVariable String tagSlug,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Article> articlesPage = articleService.findByTag(tagSlug, pageable);
        Page<ArticleDTO> articleDTOPage = articlesPage.map(ArticleDTO::new);
        
        return ResponseEntity.ok(articleDTOPage);
    }
    
    // 게시글 상세 조회 (조회수 증가)
    @GetMapping("/{id}")
    public ResponseEntity<ArticleDTO> getArticleById(@PathVariable Long id) {
        Optional<Article> article = articleService.findPublishedById(id);
        
        if (article.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        ArticleDTO articleDTO = new ArticleDTO(article.get());
        return ResponseEntity.ok(articleDTO);
    }
    
    // 관련 게시글 조회
    @GetMapping("/{id}/related")
    public ResponseEntity<List<ArticleDTO>> getRelatedArticles(
            @PathVariable Long id,
            @RequestParam(defaultValue = "5") int limit) {
        
        Optional<Article> articleOpt = articleService.findPublishedById(id);
        
        if (articleOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Article article = articleOpt.get();
        Pageable pageable = PageRequest.of(0, limit);
        List<Article> relatedArticles = articleService.findRelatedArticles(id, article.getCategory(), pageable);
        List<ArticleDTO> relatedDTOs = relatedArticles.stream()
                .map(ArticleDTO::new)
                .toList();
        
        return ResponseEntity.ok(relatedDTOs);
    }
    
    // 게시글 좋아요
    @PostMapping("/{id}/like")
    public ResponseEntity<ArticleDTO> likeArticle(@PathVariable Long id) {
        try {
            Article likedArticle = articleService.likeArticle(id);
            ArticleDTO articleDTO = new ArticleDTO(likedArticle);
            return ResponseEntity.ok(articleDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // 검색
    @GetMapping("/search")
    public ResponseEntity<Page<ArticleDTO>> searchArticles(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Article> articlesPage = articleService.searchPublishedArticles(keyword.trim(), pageable);
        Page<ArticleDTO> articleDTOPage = articlesPage.map(ArticleDTO::new);
        
        return ResponseEntity.ok(articleDTOPage);
    }
    
    // 카테고리 목록 조회
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        List<String> categories = List.of(
                Article.ArticleCategory.NEWS.name(),
                Article.ArticleCategory.ANNOUNCEMENT.name(),
                Article.ArticleCategory.PRODUCT_INFO.name(),
                Article.ArticleCategory.UPDATE.name(),
                Article.ArticleCategory.EVENT.name()
        );
        
        return ResponseEntity.ok(categories);
    }
    
    // 상태 목록 조회
    @GetMapping("/statuses")
    public ResponseEntity<List<String>> getStatuses() {
        List<String> statuses = List.of(
                Article.ArticleStatus.PRIVATE.name(),
                Article.ArticleStatus.PUBLIC.name(),
                Article.ArticleStatus.PRIVATE.name()
        );
        
        return ResponseEntity.ok(statuses);
    }
}