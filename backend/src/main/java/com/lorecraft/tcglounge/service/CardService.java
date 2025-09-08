package com.lorecraft.tcglounge.service;

import com.lorecraft.tcglounge.entity.Card;
import com.lorecraft.tcglounge.repository.CardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CardService {
    
    private static final Logger log = LoggerFactory.getLogger(CardService.class);
    
    private final CardRepository cardRepository;
    
    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }
    
    public Card createCard(String cardName, Card.CardColor cardColor, Card.CardRarity rarity, Integer cost) {
        Card card = new Card(cardName, cardColor, rarity, cost);
        Card saved = cardRepository.save(card);
        log.info("Created new card: {}", cardName);
        return saved;
    }
    
    @Transactional(readOnly = true)
    public Optional<Card> findById(Long id) {
        return cardRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public List<Card> findAll() {
        return cardRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public List<Card> searchByName(String cardName) {
        return cardRepository.findByCardNameContaining(cardName);
    }
    
    @Transactional(readOnly = true)
    public List<Card> findByColor(Card.CardColor cardColor) {
        return cardRepository.findByCardColor(cardColor);
    }
    
    @Transactional(readOnly = true)
    public List<Card> findByRarity(Card.CardRarity rarity) {
        return cardRepository.findByRarity(rarity);
    }
    
    @Transactional(readOnly = true)
    public List<Card> findByCostRange(Integer maxCost) {
        return cardRepository.findByCostLessThanEqual(maxCost);
    }
}