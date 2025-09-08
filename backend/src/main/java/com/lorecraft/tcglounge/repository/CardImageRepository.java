package com.lorecraft.tcglounge.repository;

import com.lorecraft.tcglounge.entity.CardImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardImageRepository extends JpaRepository<CardImage, Long> {
    
    List<CardImage> findByCardCardId(Long cardId);
    
    List<CardImage> findByCardCardIdAndImageCategory(Long cardId, String imageCategory);
}