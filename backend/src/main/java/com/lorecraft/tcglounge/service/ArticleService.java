package com.lorecraft.tcglounge.service;

import com.lorecraft.tcglounge.entity.Article;
import com.lorecraft.tcglounge.entity.Tag;
import com.lorecraft.tcglounge.repository.ArticleRepository;
import com.lorecraft.tcglounge.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class ArticleService {
    
    @Autowired
    private ArticleRepository articleRepository;
    
    @Autowired
    private TagRepository tagRepository;
    
    // 게시글 생성
    public Article createArticle(Article article) {
        // 게시 예정일이 없으면 현재 시간으로 설정
        if (article.getPublishDate() == null && article.getStatus() == Article.ArticleStatus.PUBLIC) {
            article.setPublishDate(LocalDateTime.now());
        }
        
        Article savedArticle = articleRepository.save(article);
        
        // 태그 사용 횟수 업데이트
        updateTagUsageCount(savedArticle.getTags(), true);
        
        return savedArticle;
    }
    
    // 게시글 수정 (ID 없이)
    public Article updateArticle(Article updatedArticle) {
        Article savedArticle = articleRepository.save(updatedArticle);
        
        // 태그 사용 횟수 업데이트
        updateTagUsageCount(savedArticle.getTags(), true);
        
        return savedArticle;
    }
    
    // 게시글 수정
    public Article updateArticle(Long id, Article updatedArticle) {
        Optional<Article> existingArticle = articleRepository.findById(id);
        
        if (existingArticle.isEmpty()) {
            throw new RuntimeException("게시글을 찾을 수 없습니다. ID: " + id);
        }
        
        Article article = existingArticle.get();
        Set<Tag> oldTags = article.getTags();
        
        // 기본 정보 업데이트
        article.setTitle(updatedArticle.getTitle());
        article.setContent(updatedArticle.getContent());
        article.setSummary(updatedArticle.getSummary());
        article.setAuthor(updatedArticle.getAuthor());
        article.setAuthorId(updatedArticle.getAuthorId());
        article.setCategory(updatedArticle.getCategory());
        article.setFeatured(updatedArticle.getFeatured());
        article.setThumbnailUrl(updatedArticle.getThumbnailUrl());
        article.setMetaTitle(updatedArticle.getMetaTitle());
        article.setMetaDescription(updatedArticle.getMetaDescription());
        
        // 상태 변경 처리
        if (updatedArticle.getStatus() != null && !updatedArticle.getStatus().equals(article.getStatus())) {
            article.setStatus(updatedArticle.getStatus());
            if (updatedArticle.getStatus() == Article.ArticleStatus.PUBLIC && article.getPublishDate() == null) {
                article.setPublishDate(LocalDateTime.now());
            }
        }
        
        // 태그 업데이트
        if (updatedArticle.getTags() != null) {
            // 기존 태그 사용 횟수 감소
            updateTagUsageCount(oldTags, false);
            
            article.getTags().clear();
            article.getTags().addAll(updatedArticle.getTags());
            
            // 새 태그 사용 횟수 증가
            updateTagUsageCount(article.getTags(), true);
        }
        
        return articleRepository.save(article);
    }
    
    // 게시글 삭제
    public void deleteArticle(Long id) {
        Optional<Article> article = articleRepository.findById(id);
        
        if (article.isEmpty()) {
            throw new RuntimeException("게시글을 찾을 수 없습니다. ID: " + id);
        }
        
        // 태그 사용 횟수 감소
        updateTagUsageCount(article.get().getTags(), false);
        
        articleRepository.deleteById(id);
    }
    
    // 게시글 조회 (관리자용 - 모든 상태)
    @Transactional(readOnly = true)
    public Optional<Article> findById(Long id) {
        return articleRepository.findById(id);
    }
    
    // 게시된 게시글만 조회 (일반 사용자용)
    @Transactional(readOnly = true)
    public Optional<Article> findPublishedById(Long id) {
        Optional<Article> article = articleRepository.findPublishedById(id);
        if (article.isPresent()) {
            // 조회수 증가
            Article foundArticle = article.get();
            foundArticle.incrementViewsCount();
            articleRepository.save(foundArticle);
        }
        return article;
    }
    
    // 게시글 목록 조회 (페이지네이션)
    @Transactional(readOnly = true)
    public Page<Article> findAll(Pageable pageable) {
        return articleRepository.findAll(pageable);
    }
    
    // 게시된 게시글 목록 조회
    @Transactional(readOnly = true)
    public Page<Article> findPublishedArticles(Pageable pageable) {
        Page<Article> articles = articleRepository.findPublishedArticles(LocalDateTime.now(), pageable);
        // tags를 미리 로드하여 LazyInitializationException 방지
        articles.getContent().forEach(article -> article.getTags().size());
        return articles;
    }
    
    // 카테고리별 게시글 조회
    @Transactional(readOnly = true)
    public Page<Article> findByCategory(Article.ArticleCategory category, Pageable pageable) {
        Page<Article> articles = articleRepository.findByStatusAndCategory(Article.ArticleStatus.PUBLIC, category, pageable);
        articles.getContent().forEach(article -> article.getTags().size());
        return articles;
    }
    
    // 주요 뉴스 조회
    @Transactional(readOnly = true)
    public List<Article> findFeaturedArticles() {
        List<Article> articles = articleRepository.findByFeaturedTrueAndStatus(Article.ArticleStatus.PUBLIC);
        articles.forEach(article -> article.getTags().size());
        return articles;
    }
    
    // 주요 뉴스 조회 (페이지네이션)
    @Transactional(readOnly = true)
    public Page<Article> findFeaturedPublishedArticles(Pageable pageable) {
        Page<Article> articles = articleRepository.findByFeaturedTrueAndStatus(Article.ArticleStatus.PUBLIC, pageable);
        articles.getContent().forEach(article -> article.getTags().size());
        return articles;
    }
    
    // 키워드 검색
    @Transactional(readOnly = true)
    public Page<Article> searchPublishedArticles(String keyword, Pageable pageable) {
        Page<Article> articles = articleRepository.findPublishedByKeyword(keyword, pageable);
        articles.getContent().forEach(article -> article.getTags().size());
        return articles;
    }
    
    // 태그별 게시글 조회
    @Transactional(readOnly = true)
    public Page<Article> findByTag(String tagSlug, Pageable pageable) {
        Page<Article> articles = articleRepository.findByTagSlug(tagSlug, pageable);
        articles.getContent().forEach(article -> article.getTags().size());
        return articles;
    }
    
    // 인기 게시글 조회
    @Transactional(readOnly = true)
    public Page<Article> findPopularArticles(Pageable pageable) {
        return articleRepository.findPopularArticles(pageable);
    }
    
    // 관련 게시글 조회
    @Transactional(readOnly = true)
    public List<Article> findRelatedArticles(Long articleId, Article.ArticleCategory category, Pageable pageable) {
        return articleRepository.findRelatedArticles(category, articleId, pageable);
    }
    
    // 게시글 게시
    public Article publishArticle(Long id) {
        Optional<Article> articleOpt = articleRepository.findById(id);
        
        if (articleOpt.isEmpty()) {
            throw new RuntimeException("게시글을 찾을 수 없습니다. ID: " + id);
        }
        
        Article article = articleOpt.get();
        article.setStatus(Article.ArticleStatus.PUBLIC);
        if (article.getPublishDate() == null) {
            article.setPublishDate(LocalDateTime.now());
        }
        
        return articleRepository.save(article);
    }
    
    // 게시글 게시 중단
    public Article unpublishArticle(Long id) {
        Optional<Article> articleOpt = articleRepository.findById(id);
        
        if (articleOpt.isEmpty()) {
            throw new RuntimeException("게시글을 찾을 수 없습니다. ID: " + id);
        }
        
        Article article = articleOpt.get();
        article.setStatus(Article.ArticleStatus.PRIVATE);
        
        return articleRepository.save(article);
    }
    
    // 게시글 보관
    public Article archiveArticle(Long id) {
        Optional<Article> articleOpt = articleRepository.findById(id);
        
        if (articleOpt.isEmpty()) {
            throw new RuntimeException("게시글을 찾을 수 없습니다. ID: " + id);
        }
        
        Article article = articleOpt.get();
        article.setStatus(Article.ArticleStatus.PRIVATE);
        
        return articleRepository.save(article);
    }
    
    // 게시글 좋아요
    public Article likeArticle(Long id) {
        Optional<Article> articleOpt = articleRepository.findById(id);
        
        if (articleOpt.isEmpty()) {
            throw new RuntimeException("게시글을 찾을 수 없습니다. ID: " + id);
        }
        
        Article article = articleOpt.get();
        article.incrementLikesCount();
        
        return articleRepository.save(article);
    }
    
    // 통계 - 카테고리별 게시글 수
    @Transactional(readOnly = true)
    public List<Object[]> getArticleCountByCategory() {
        return articleRepository.countByCategory();
    }
    
    // 통계 - 월별 게시글 수
    @Transactional(readOnly = true)
    public List<Object[]> getArticleCountByMonth() {
        return articleRepository.countByMonth();
    }
    
    // 모든 게시글 조회 (관리자용)
    @Transactional(readOnly = true)
    public Page<Article> findAllArticles(Pageable pageable) {
        return articleRepository.findAll(pageable);
    }
    
    // 모든 게시글 검색 (관리자용)
    @Transactional(readOnly = true)
    public Page<Article> searchAllArticles(String keyword, Pageable pageable) {
        return articleRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(keyword, keyword, pageable);
    }
    
    // 상태별 게시글 조회
    @Transactional(readOnly = true)
    public Page<Article> findByStatus(Article.ArticleStatus status, Pageable pageable) {
        return articleRepository.findByStatus(status, pageable);
    }
    
    // 모든 상태의 카테고리별 게시글 조회 (관리자용)
    @Transactional(readOnly = true)
    public Page<Article> findAllByCategory(Article.ArticleCategory category, Pageable pageable) {
        return articleRepository.findByCategory(category, pageable);
    }
    
    // 작성자별 게시글 조회
    @Transactional(readOnly = true)
    public Page<Article> findByAuthorId(Long authorId, Pageable pageable) {
        return articleRepository.findByAuthorId(authorId, pageable);
    }
    
    // 주요 뉴스 토글
    public Article toggleFeature(Long id) {
        Optional<Article> articleOpt = articleRepository.findById(id);
        
        if (articleOpt.isEmpty()) {
            throw new RuntimeException("게시글을 찾을 수 없습니다. ID: " + id);
        }
        
        Article article = articleOpt.get();
        article.setFeatured(!article.getFeatured());
        
        return articleRepository.save(article);
    }
    
    // 통계 조회 (관리자용)
    @Transactional(readOnly = true)
    public Object getStatistics() {
        // 간단한 통계 반환 (실제로는 복잡한 통계 구현 가능)
        return java.util.Map.of(
            "totalArticles", articleRepository.count(),
            "publishedArticles", articleRepository.countByStatus(Article.ArticleStatus.PUBLIC),
            "draftArticles", articleRepository.countByStatus(Article.ArticleStatus.PRIVATE),
            "archivedArticles", articleRepository.countByStatus(Article.ArticleStatus.PRIVATE)
        );
    }
    
    // 태그 사용 횟수 업데이트 헬퍼 메서드
    private void updateTagUsageCount(Set<Tag> tags, boolean increment) {
        if (tags != null) {
            for (Tag tag : tags) {
                if (increment) {
                    tag.incrementUsageCount();
                } else {
                    tag.decrementUsageCount();
                }
                tagRepository.save(tag);
            }
        }
    }
}