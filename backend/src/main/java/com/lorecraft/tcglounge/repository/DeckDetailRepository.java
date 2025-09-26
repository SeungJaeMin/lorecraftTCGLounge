package com.lorecraft.tcglounge.repository;

import com.lorecraft.tcglounge.entity.DeckDetail;
import com.lorecraft.tcglounge.entity.DeckDetailId;
import com.lorecraft.tcglounge.entity.CardDeck;
import com.lorecraft.tcglounge.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeckDetailRepository extends JpaRepository<DeckDetail, DeckDetailId> {
    
    List<DeckDetail> findByDeckOrderByOrderIndexAsc(CardDeck deck);
    
    List<DeckDetail> findByDeckAndIsSideboardFalseOrderByOrderIndexAsc(CardDeck deck);

    // Card 정보를 함께 조회 (LazyInitializationException 방지)
    @Query("SELECT dd FROM DeckDetail dd JOIN FETCH dd.card WHERE dd.deck = :deck AND dd.isSideboard = false ORDER BY dd.orderIndex ASC")
    List<DeckDetail> findMainDeckCardsWithCard(@Param("deck") CardDeck deck);
    
    Optional<DeckDetail> findByDeckAndCard(CardDeck deck, Card card);
    
    @Query("SELECT dd FROM DeckDetail dd WHERE dd.deck = :deck AND dd.card.cardType = :cardType")
    List<DeckDetail> findByDeckAndCardType(@Param("deck") CardDeck deck, @Param("cardType") String cardType);
    
    @Query("SELECT COUNT(dd) FROM DeckDetail dd WHERE dd.deck = :deck AND dd.isSideboard = false")
    Integer countMainDeckCards(@Param("deck") CardDeck deck);
    
    @Query("SELECT SUM(dd.quantity) FROM DeckDetail dd WHERE dd.deck = :deck AND dd.isSideboard = false")
    Integer sumMainDeckCardQuantities(@Param("deck") CardDeck deck);
    
    @Query("SELECT dd FROM DeckDetail dd WHERE dd.deck = :deck AND dd.card.cardColor = :color")
    List<DeckDetail> findByDeckAndCardColor(@Param("deck") CardDeck deck, @Param("color") String color);
    
    void deleteByDeck(CardDeck deck);
}