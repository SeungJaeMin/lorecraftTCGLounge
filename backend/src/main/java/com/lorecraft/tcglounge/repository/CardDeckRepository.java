package com.lorecraft.tcglounge.repository;

import com.lorecraft.tcglounge.entity.CardDeck;
import com.lorecraft.tcglounge.entity.Gamer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardDeckRepository extends JpaRepository<CardDeck, Long> {
    
    List<CardDeck> findByGamerOrderByUpdatedAtDesc(Gamer gamer);
    
    Page<CardDeck> findByGamerOrderByUpdatedAtDesc(Gamer gamer, Pageable pageable);
    
    List<CardDeck> findByGamerAndDeckNameContaining(Gamer gamer, String deckName);
    
    Optional<CardDeck> findByIdAndGamer(Long id, Gamer gamer);
    
    Optional<CardDeck> findByDeckCode(String deckCode);
    
    List<CardDeck> findByIsPublicTrue();
    
    Page<CardDeck> findByIsPublicTrueOrderByLikesCountDescViewsCountDesc(Pageable pageable);
    
    @Query("SELECT d FROM CardDeck d WHERE d.gamer = :gamer AND d.totalCards >= 40")
    List<CardDeck> findCompleteDecksByGamer(@Param("gamer") Gamer gamer);
    
    @Query("SELECT d FROM CardDeck d WHERE d.gamer = :gamer AND d.totalCards < 40")
    List<CardDeck> findIncompleteDecksByGamer(@Param("gamer") Gamer gamer);
    
    @Query("SELECT COUNT(d) FROM CardDeck d WHERE d.gamer = :gamer")
    Integer countByGamer(@Param("gamer") Gamer gamer);
    
    @Query("SELECT COUNT(d) FROM CardDeck d WHERE d.gamer = :gamer AND d.totalCards >= 40")
    Integer countCompleteDecksByGamer(@Param("gamer") Gamer gamer);
}