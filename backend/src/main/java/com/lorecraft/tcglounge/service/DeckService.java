package com.lorecraft.tcglounge.service;

import com.lorecraft.tcglounge.entity.*;
import com.lorecraft.tcglounge.repository.CardDeckRepository;
import com.lorecraft.tcglounge.repository.DeckDetailRepository;
import com.lorecraft.tcglounge.repository.CardRepository;
import com.lorecraft.tcglounge.repository.GamerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;

@Service
@Transactional
public class DeckService {
    
    private static final Logger log = LoggerFactory.getLogger(DeckService.class);
    private final Random random = new Random();
    
    private final CardDeckRepository deckRepository;
    private final DeckDetailRepository deckDetailRepository;
    private final CardRepository cardRepository;
    private final GamerRepository gamerRepository;
    
    public DeckService(CardDeckRepository deckRepository, 
                      DeckDetailRepository deckDetailRepository,
                      CardRepository cardRepository,
                      GamerRepository gamerRepository) {
        this.deckRepository = deckRepository;
        this.deckDetailRepository = deckDetailRepository;
        this.cardRepository = cardRepository;
        this.gamerRepository = gamerRepository;
    }
    
    @Transactional(readOnly = true)
    public List<CardDeck> getGamerDecks(Long gamerId) {
        Gamer gamer = gamerRepository.findById(gamerId)
            .orElseThrow(() -> new RuntimeException("Gamer not found: " + gamerId));
        List<CardDeck> decks = deckRepository.findByGamerOrderByUpdatedAtDesc(gamer);

        // Eager fetch leader cards to avoid lazy loading issues
        for (CardDeck deck : decks) {
            if (deck.getLeaderCard() != null) {
                // Force initialization of leader card
                deck.getLeaderCard().getCardName();
            }
        }

        return decks;
    }
    
    @Transactional(readOnly = true)
    public Optional<CardDeck> getDeckById(Long deckId, Long gamerId) {
        Gamer gamer = gamerRepository.findById(gamerId)
            .orElseThrow(() -> new RuntimeException("Gamer not found: " + gamerId));
        return deckRepository.findByDeckIdAndGamer(deckId, gamer);
    }
    
    @Transactional(readOnly = true)
    public List<DeckDetail> getDeckCards(Long deckId, Long gamerId) {
        CardDeck deck = getDeckById(deckId, gamerId)
            .orElseThrow(() -> new RuntimeException("Deck not found: " + deckId));
        List<DeckDetail> deckDetails = deckDetailRepository.findByDeckAndIsSideboardFalseOrderByOrderIndexAsc(deck);

        // Eager fetch card information to avoid lazy loading issues
        for (DeckDetail detail : deckDetails) {
            if (detail.getCard() != null) {
                // Force initialization of card entity
                detail.getCard().getCardName();
                detail.getCard().getCardType();
                detail.getCard().getCardColor();
            }
        }

        return deckDetails;
    }
    
    public CardDeck createDeck(Long gamerId, String deckName, String description) {
        Gamer gamer = gamerRepository.findById(gamerId)
            .orElseThrow(() -> new RuntimeException("Gamer not found: " + gamerId));
        
        CardDeck deck = new CardDeck(gamer, deckName);
        deck.setDescription(description);
        
        CardDeck savedDeck = deckRepository.save(deck);
        log.info("Created new deck: {} for gamer: {}", deckName, gamerId);
        return savedDeck;
    }
    
    public CardDeck updateDeck(Long deckId, Long gamerId, String deckName, String description, Boolean isPublic) {
        CardDeck deck = getDeckById(deckId, gamerId)
            .orElseThrow(() -> new RuntimeException("Deck not found: " + deckId));
        
        if (deckName != null) deck.setDeckName(deckName);
        if (description != null) deck.setDescription(description);
        if (isPublic != null) deck.setIsPublic(isPublic);
        
        return deckRepository.save(deck);
    }
    
    public CardDeck saveOrUpdateDeck(Long deckId, Long gamerId, String deckName, String description, Boolean isPublic) {
        if (deckId == null || deckId == 0) {
            // Create new deck
            return createDeck(gamerId, deckName, description);
        } else {
            // Try to update existing deck
            Optional<CardDeck> existingDeck = getDeckById(deckId, gamerId);
            if (existingDeck.isPresent()) {
                // Update existing deck
                CardDeck deck = existingDeck.get();
                if (deckName != null) deck.setDeckName(deckName);
                if (description != null) deck.setDescription(description);
                if (isPublic != null) deck.setIsPublic(isPublic);
                return deckRepository.save(deck);
            } else {
                // Deck doesn't exist or doesn't belong to user, create new one
                return createDeck(gamerId, deckName, description);
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    public void updateDeckCards(Long deckId, Long gamerId, List<?> cardsList) {
        CardDeck deck = getDeckById(deckId, gamerId)
            .orElseThrow(() -> new RuntimeException("Deck not found: " + deckId));
        
        // 기존 덱 카드들 모두 삭제
        deckDetailRepository.deleteByDeck(deck);
        
        // 새로운 카드들 추가
        int orderIndex = 1;
        for (Object cardObj : cardsList) {
            if (cardObj instanceof Map) {
                Map<String, Object> cardData = (Map<String, Object>) cardObj;
                Map<String, Object> cardInfo = (Map<String, Object>) cardData.get("card");
                
                if (cardInfo != null) {
                    Long cardId = Long.valueOf(cardInfo.get("cardId").toString());
                    Integer quantity = Integer.valueOf(cardData.get("quantity").toString());
                    
                    Card card = cardRepository.findById(cardId)
                        .orElseThrow(() -> new RuntimeException("Card not found: " + cardId));
                    
                    // DeckDetail 생성
                    DeckDetail deckDetail = new DeckDetail(deck, card, quantity);
                    deckDetail.setOrderIndex(orderIndex++);
                    deckDetailRepository.save(deckDetail);
                    
                    // 리더 카드 설정
                    if ("LEADER".equals(card.getCardType()) && deck.getLeaderCard() == null) {
                        deck.setLeaderCard(card);
                    }
                }
            }
        }
        
        // 덱 총 카드 수 업데이트
        deck.updateTotalCards();
        deckRepository.save(deck);
        
        log.info("Updated deck cards for deck: {} with {} card types", deckId, cardsList.size());
    }
    
    public void deleteDeck(Long deckId, Long gamerId) {
        CardDeck deck = getDeckById(deckId, gamerId)
            .orElseThrow(() -> new RuntimeException("Deck not found: " + deckId));
        
        deckDetailRepository.deleteByDeck(deck);
        deckRepository.delete(deck);
        log.info("Deleted deck: {} for gamer: {}", deckId, gamerId);
    }
    
    public DeckDetail addCardToDeck(Long deckId, Long gamerId, Long cardId, Integer quantity) {
        CardDeck deck = getDeckById(deckId, gamerId)
            .orElseThrow(() -> new RuntimeException("Deck not found: " + deckId));
        
        Card card = cardRepository.findById(cardId)
            .orElseThrow(() -> new RuntimeException("Card not found: " + cardId));
        
        // Validate deck rules
        validateCardAddition(deck, card, quantity);
        
        Optional<DeckDetail> existingDetail = deckDetailRepository.findByDeckAndCard(deck, card);
        
        DeckDetail deckDetail;
        if (existingDetail.isPresent()) {
            deckDetail = existingDetail.get();
            int newQuantity = deckDetail.getQuantity() + quantity;
            
            // Check max 3 cards per type (except leaders)
            if (!card.getCardType().equals("LEADER") && newQuantity > 3) {
                throw new RuntimeException("Cannot add more than 3 copies of the same card");
            }
            if (card.getCardType().equals("LEADER") && newQuantity > 1) {
                throw new RuntimeException("Cannot add more than 1 leader card");
            }
            
            deckDetail.setQuantity(newQuantity);
        } else {
            deckDetail = new DeckDetail(deck, card, quantity);
            deckDetail.setOrderIndex(getNextOrderIndex(deck));
        }
        
        DeckDetail saved = deckDetailRepository.save(deckDetail);
        
        // Update deck total cards
        deck.updateTotalCards();
        deckRepository.save(deck);
        
        return saved;
    }
    
    public void removeCardFromDeck(Long deckId, Long gamerId, Long cardId, Integer quantity) {
        CardDeck deck = getDeckById(deckId, gamerId)
            .orElseThrow(() -> new RuntimeException("Deck not found: " + deckId));
        
        Card card = cardRepository.findById(cardId)
            .orElseThrow(() -> new RuntimeException("Card not found: " + cardId));
        
        DeckDetail deckDetail = deckDetailRepository.findByDeckAndCard(deck, card)
            .orElseThrow(() -> new RuntimeException("Card not in deck"));
        
        int newQuantity = deckDetail.getQuantity() - quantity;
        
        if (newQuantity <= 0) {
            deckDetailRepository.delete(deckDetail);
        } else {
            deckDetail.setQuantity(newQuantity);
            deckDetailRepository.save(deckDetail);
        }
        
        // Update deck total cards
        deck.updateTotalCards();
        deckRepository.save(deck);
    }
    
    public CardDeck generateRandomDeck(Long gamerId, String deckName) {
        Gamer gamer = gamerRepository.findById(gamerId)
            .orElseThrow(() -> new RuntimeException("Gamer not found: " + gamerId));
        
        // Create new deck
        CardDeck deck = new CardDeck(gamer, deckName);
        deck.setDescription("Randomly generated deck");
        deck = deckRepository.save(deck);
        
        // Get all available cards
        List<Card> allCards = cardRepository.findAll();
        List<Card> leaders = allCards.stream()
            .filter(card -> "LEADER".equals(card.getCardType()))
            .toList();
        List<Card> nonLeaders = allCards.stream()
            .filter(card -> !"LEADER".equals(card.getCardType()))
            .toList();
        
        if (leaders.isEmpty() || nonLeaders.isEmpty()) {
            throw new RuntimeException("Not enough cards to generate random deck");
        }
        
        // Add 1 random leader
        Card randomLeader = leaders.get(random.nextInt(leaders.size()));
        addCardToDeck(deck.getDeckId(), gamerId, randomLeader.getCardId(), 1);
        deck.setLeaderCard(randomLeader);
        
        // Get leader color for deck constraint
        Card.CardColor leaderColor = randomLeader.getCardColor();
        
        // Filter cards by color (include COLORLESS)
        List<Card> compatibleCards = nonLeaders.stream()
            .filter(card -> leaderColor.equals(card.getCardColor()) || Card.CardColor.COLORLESS.equals(card.getCardColor()))
            .toList();
        
        // Add random cards to reach 40 total
        int cardsToAdd = 39; // We already added 1 leader
        Map<Long, Integer> cardCounts = new HashMap<>();
        
        while (cardsToAdd > 0 && !compatibleCards.isEmpty()) {
            Card randomCard = compatibleCards.get(random.nextInt(compatibleCards.size()));
            int currentCount = cardCounts.getOrDefault(randomCard.getCardId(), 0);
            
            if (currentCount < 3) { // Max 3 per card
                addCardToDeck(deck.getDeckId(), gamerId, randomCard.getCardId(), 1);
                cardCounts.put(randomCard.getCardId(), currentCount + 1);
                cardsToAdd--;
            }
        }
        
        // Update and save deck
        deck.updateTotalCards();
        return deckRepository.save(deck);
    }
    
    @Transactional(readOnly = true)
    public Map<String, Object> getDeckStats(Long deckId, Long gamerId) {
        CardDeck deck = getDeckById(deckId, gamerId)
            .orElseThrow(() -> new RuntimeException("Deck not found: " + deckId));
        
        List<DeckDetail> deckCards = deckDetailRepository.findByDeckAndIsSideboardFalseOrderByOrderIndexAsc(deck);
        
        Map<String, Integer> typeCount = new HashMap<>();
        Map<String, Integer> colorCount = new HashMap<>();
        
        for (DeckDetail detail : deckCards) {
            Card card = detail.getCard();
            String cardType = card.getCardType();
            String cardColor = card.getCardColor().toString();
            int quantity = detail.getQuantity();
            
            typeCount.put(cardType, typeCount.getOrDefault(cardType, 0) + quantity);
            colorCount.put(cardColor, colorCount.getOrDefault(cardColor, 0) + quantity);
        }
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCards", deck.getTotalCards());
        stats.put("isComplete", deck.getTotalCards() >= 40);
        stats.put("typeDistribution", typeCount);
        stats.put("colorDistribution", colorCount);
        stats.put("leaderCard", deck.getLeaderCard());
        
        return stats;
    }
    
    private void validateCardAddition(CardDeck deck, Card card, Integer quantity) {
        // Check deck size limit
        Integer currentTotal = deckDetailRepository.sumMainDeckCardQuantities(deck);
        if (currentTotal == null) currentTotal = 0;
        
        if (currentTotal + quantity > 45) {
            throw new RuntimeException("Deck cannot exceed 45 cards");
        }
        
        // Check color compatibility (unless COLORLESS)
        if (deck.getLeaderCard() != null && !card.getCardColor().equals(Card.CardColor.COLORLESS)) {
            Card.CardColor leaderColor = deck.getLeaderCard().getCardColor();
            if (!card.getCardColor().equals(leaderColor)) {
                throw new RuntimeException("Card color must match leader color or be COLORLESS");
            }
        }
    }
    
    private Integer getNextOrderIndex(CardDeck deck) {
        List<DeckDetail> existingCards = deckDetailRepository.findByDeckOrderByOrderIndexAsc(deck);
        if (existingCards.isEmpty()) {
            return 1;
        }
        return existingCards.get(existingCards.size() - 1).getOrderIndex() + 1;
    }
}