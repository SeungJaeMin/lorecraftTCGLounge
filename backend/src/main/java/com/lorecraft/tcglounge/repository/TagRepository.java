package com.lorecraft.tcglounge.repository;

import com.lorecraft.tcglounge.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    
    // 이름으로 태그 조회
    Optional<Tag> findByName(String name);
    
    // 슬러그로 태그 조회
    Optional<Tag> findBySlug(String slug);
    
    // 이름으로 태그 검색
    List<Tag> findByNameContainingIgnoreCase(String name);
    Page<Tag> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    // 사용 빈도 기준 정렬
    List<Tag> findAllByOrderByUsageCountDesc();
    Page<Tag> findAllByOrderByUsageCountDesc(Pageable pageable);
    
    // 이름 기준 정렬
    List<Tag> findAllByOrderByNameAsc();
    Page<Tag> findAllByOrderByNameAsc(Pageable pageable);
    
    // 최신 생성순 정렬
    List<Tag> findAllByOrderByCreatedAtDesc();
    Page<Tag> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    // 인기 태그 조회 (사용 횟수 기준 상위 N개)
    @Query("SELECT t FROM Tag t WHERE t.usageCount > 0 ORDER BY t.usageCount DESC")
    List<Tag> findPopularTags(Pageable pageable);
    
    // 특정 사용 횟수 이상의 태그 조회
    @Query("SELECT t FROM Tag t WHERE t.usageCount >= :minUsage ORDER BY t.usageCount DESC")
    List<Tag> findByUsageCountGreaterThanEqual(@Param("minUsage") Integer minUsage);
    
    // 태그명 존재 여부 확인
    boolean existsByName(String name);
    
    // 슬러그 존재 여부 확인
    boolean existsBySlug(String slug);
    
    // 사용되지 않은 태그 조회 (사용 횟수가 0인 태그)
    @Query("SELECT t FROM Tag t WHERE t.usageCount = 0 OR t.usageCount IS NULL")
    List<Tag> findUnusedTags();
    
    // 특정 기사와 연관된 태그 조회
    @Query("SELECT t FROM Tag t JOIN t.articles a WHERE a.id = :articleId")
    List<Tag> findByArticleId(@Param("articleId") Long articleId);
    
    // 태그별 기사 수 조회
    @Query("SELECT t.name, t.usageCount FROM Tag t ORDER BY t.usageCount DESC")
    List<Object[]> getTagUsageStatistics();
}