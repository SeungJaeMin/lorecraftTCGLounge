package com.lorecraft.tcglounge.repository;

import com.lorecraft.tcglounge.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    
    // 상태별 조회
    List<Article> findByStatus(Article.ArticleStatus status);
    Page<Article> findByStatus(Article.ArticleStatus status, Pageable pageable);
    long countByStatus(Article.ArticleStatus status);
    
    // 카테고리별 조회
    List<Article> findByCategory(Article.ArticleCategory category);
    Page<Article> findByCategory(Article.ArticleCategory category, Pageable pageable);
    
    // 상태와 카테고리별 조회
    Page<Article> findByStatusAndCategory(Article.ArticleStatus status, Article.ArticleCategory category, Pageable pageable);
    
    // 주요 뉴스 조회
    List<Article> findByFeaturedTrueAndStatus(Article.ArticleStatus status);
    Page<Article> findByFeaturedTrueAndStatus(Article.ArticleStatus status, Pageable pageable);
    
    // 제목이나 내용으로 검색 (게시된 것만)
    @Query("SELECT a FROM Article a WHERE a.status = 'PUBLIC' AND (LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(a.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Article> findPublishedByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    // 제목과 내용으로 검색 (모든 상태)
    Page<Article> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String title, String content, Pageable pageable);
    
    // 작성자별 조회
    List<Article> findByAuthorId(Long authorId);
    Page<Article> findByAuthorId(Long authorId, Pageable pageable);
    
    // 게시일 기준 조회
    @Query("SELECT a FROM Article a WHERE a.status = 'PUBLIC' AND a.publishDate <= :now ORDER BY a.publishDate DESC")
    Page<Article> findPublishedArticles(@Param("now") LocalDateTime now, Pageable pageable);
    
    // 최신 게시글 조회 (게시된 것만)
    @Query("SELECT a FROM Article a WHERE a.status = 'PUBLIC' ORDER BY a.createdAt DESC")
    Page<Article> findLatestPublished(Pageable pageable);
    
    // 인기 게시글 조회 (조회수 기준)
    @Query("SELECT a FROM Article a WHERE a.status = 'PUBLIC' ORDER BY a.viewsCount DESC, a.createdAt DESC")
    Page<Article> findPopularArticles(Pageable pageable);
    
    // 태그별 게시글 조회
    @Query("SELECT DISTINCT a FROM Article a JOIN a.tags t WHERE t.slug = :tagSlug AND a.status = 'PUBLIC'")
    Page<Article> findByTagSlug(@Param("tagSlug") String tagSlug, Pageable pageable);
    
    // 카테고리별 게시글 수 조회
    @Query("SELECT a.category, COUNT(a) FROM Article a WHERE a.status = 'PUBLIC' GROUP BY a.category")
    List<Object[]> countByCategory();
    
    // 월별 게시글 수 조회
    @Query("SELECT YEAR(a.createdAt), MONTH(a.createdAt), COUNT(a) FROM Article a WHERE a.status = 'PUBLIC' GROUP BY YEAR(a.createdAt), MONTH(a.createdAt) ORDER BY YEAR(a.createdAt) DESC, MONTH(a.createdAt) DESC")
    List<Object[]> countByMonth();
    
    // 관련 게시글 조회 (같은 카테고리, 다른 ID)
    @Query("SELECT a FROM Article a WHERE a.category = :category AND a.id != :excludeId AND a.status = 'PUBLIC' ORDER BY a.createdAt DESC")
    List<Article> findRelatedArticles(@Param("category") Article.ArticleCategory category, @Param("excludeId") Long excludeId, Pageable pageable);
    
    // ID로 게시된 게시글만 조회
    @Query("SELECT a FROM Article a WHERE a.id = :id AND a.status = 'PUBLIC'")
    Optional<Article> findPublishedById(@Param("id") Long id);
}