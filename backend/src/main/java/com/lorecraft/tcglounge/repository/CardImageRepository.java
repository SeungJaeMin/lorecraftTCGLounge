package com.lorecraft.tcglounge.repository;

import com.lorecraft.tcglounge.entity.CardImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardImageRepository extends JpaRepository<CardImage, Long> {
    
    // 특정 카드의 모든 이미지 조회
    List<CardImage> findByCard_CardId(Long cardId);
    
    // 특정 카드의 특정 카테고리 이미지 조회
    List<CardImage> findByCard_CardIdAndImageCategory(Long cardId, String imageCategory);
    
    // 카테고리별 이미지 조회
    List<CardImage> findByImageCategory(String imageCategory);

    // 여러 카드의 이미지를 한번에 조회 (N+1 문제 해결용)
    List<CardImage> findByCard_CardIdIn(List<Long> cardIds);
}