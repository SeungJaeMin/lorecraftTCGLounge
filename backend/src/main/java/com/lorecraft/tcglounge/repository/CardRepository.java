package com.lorecraft.tcglounge.repository;

import com.lorecraft.tcglounge.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long>, JpaSpecificationExecutor<Card> {
    List<Card> findByCardNameContaining(String cardName);
    List<Card> findByCardColor(Card.CardColor cardColor);
    List<Card> findByRarity(Card.CardRarity rarity);
    List<Card> findByCostLessThanEqual(Integer cost);
    Long countByCardColor(Card.CardColor cardColor);
    Long countByRarity(Card.CardRarity rarity);
}