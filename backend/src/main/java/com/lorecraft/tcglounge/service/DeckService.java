package com.lorecraft.tcglounge.service;

import com.lorecraft.tcglounge.entity.*;
import com.lorecraft.tcglounge.repository.CardDeckRepository;
import com.lorecraft.tcglounge.repository.DeckDetailRepository;
import com.lorecraft.tcglounge.repository.CardRepository;
import com.lorecraft.tcglounge.repository.GamerRepository;
import com.lorecraft.tcglounge.repository.UserRepository;
import java.util.ArrayList;
import com.lorecraft.tcglounge.dto.deck.DeckSaveRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@Service
@Transactional
public class DeckService {

    private final Random random = new Random();

    private final CardDeckRepository deckRepository;
    private final DeckDetailRepository deckDetailRepository;
    private final CardRepository cardRepository;
    private final GamerRepository gamerRepository;
    private final UserRepository userRepository;

    public DeckService(CardDeckRepository deckRepository,
                      DeckDetailRepository deckDetailRepository,
                      CardRepository cardRepository,
                      GamerRepository gamerRepository,
                      UserRepository userRepository) {
        this.deckRepository = deckRepository;
        this.deckDetailRepository = deckDetailRepository;
        this.cardRepository = cardRepository;
        this.gamerRepository = gamerRepository;
        this.userRepository = userRepository;
    }

    // ========== DECK CRUD ==========

    @Transactional(readOnly = true)
    public List<CardDeck> getUserDecks(String userid) {
        // userid로 User 조회 후 Gamer 확인
        User user = userRepository.findByUserid(userid)
            .orElseThrow(() -> new RuntimeException("User not found: " + userid));

        // User가 Gamer 타입인 경우에만 덱 조회
        Gamer gamer = gamerRepository.findById(user.getUid())
            .orElse(null);

        if (gamer == null) {
            return new ArrayList<>(); // Gamer가 아닌 경우 빈 리스트
        }

        List<CardDeck> decks = deckRepository.findByGamerOrderByUpdatedAtDesc(gamer);

        // Eager fetch leader cards to avoid lazy loading issues
        for (CardDeck deck : decks) {
            if (deck.getLeaderCard() != null) {
                deck.getLeaderCard().getCardName(); // Force initialization
            }
        }

        return decks;
    }

    @Transactional(readOnly = true)
    public Optional<CardDeck> getDeckById(Long deckId, String userid) {
        // userid로 User 조회 후 Gamer 확인
        User user = userRepository.findByUserid(userid)
            .orElseThrow(() -> new RuntimeException("User not found: " + userid));

        Gamer gamer = gamerRepository.findById(user.getUid())
            .orElseThrow(() -> new RuntimeException("User is not a gamer: " + userid));

        return deckRepository.findByDeckIdAndGamer(deckId, gamer);
    }

    @Transactional(readOnly = true)
    public List<DeckDetail> getDeckCards(Long deckId, String userid) {
        CardDeck deck = validateDeckAccess(deckId, userid);
        List<DeckDetail> deckDetails = deckDetailRepository.findByDeckAndIsSideboardFalseOrderByOrderIndexAsc(deck);

        // Eager fetch card information to avoid lazy loading issues
        for (DeckDetail detail : deckDetails) {
            if (detail.getCard() != null) {
                detail.getCard().getCardName(); // Force initialization
                detail.getCard().getCardType();
                detail.getCard().getCardColor();
            }
        }

        return deckDetails;
    }

    public CardDeck saveDeck(String userid, DeckSaveRequestDTO request) {
        // userid로 User 조회 후 Gamer 확인
        User user = userRepository.findByUserid(userid)
            .orElseThrow(() -> new RuntimeException("User not found: " + userid));

        Gamer gamer = gamerRepository.findById(user.getUid())
            .orElseThrow(() -> new RuntimeException("User is not a gamer: " + userid));
        CardDeck deck;

        if (request.getDeckId() == null) {
            // Create new deck
            deck = createNewDeck(gamer, request);
        } else {
            // Update existing deck
            deck = updateExistingDeck(userid, request);
        }

        deck = deckRepository.save(deck);

        // Handle card list
        if (request.getCards() != null && !request.getCards().isEmpty()) {
            saveCards(deck, request.getCards());
        }

        // Update total cards count
        deck.updateTotalCards();
        return deckRepository.save(deck);
    }

    public void deleteDeck(Long deckId, String userid) {
        CardDeck deck = validateDeckAccess(deckId, userid);

        deckDetailRepository.deleteByDeck(deck);
        deckRepository.delete(deck);
        log.info("Deleted deck: {} for user: {}", deckId, userid);
    }

    // ========== DECK CARDS MANAGEMENT ==========

    public DeckDetail addCardToDeck(Long deckId, String userid, Long cardId, Integer quantity) {
        CardDeck deck = validateDeckAccess(deckId, userid);
        Card card = cardRepository.findById(cardId)
            .orElseThrow(() -> new RuntimeException("Card not found: " + cardId));

        validateCardAddition(deck, card, quantity);

        Optional<DeckDetail> existingDetail = deckDetailRepository.findByDeckAndCard(deck, card);

        DeckDetail deckDetail;
        if (existingDetail.isPresent()) {
            // Update existing card quantity
            deckDetail = existingDetail.get();
            int newQuantity = deckDetail.getQuantity() + quantity;

            validateCardQuantity(card, newQuantity);
            deckDetail.updateQuantity(newQuantity);
        } else {
            // Add new card
            deckDetail = DeckDetail.builder()
                .deck(deck)
                .card(card)
                .quantity(quantity)
                .orderIndex(getNextOrderIndex(deck))
                .build();
        }

        DeckDetail saved = deckDetailRepository.save(deckDetail);

        // Update deck total cards
        deck.updateTotalCards();
        deckRepository.save(deck);

        return saved;
    }

    public void removeCardFromDeck(Long deckId, String userid, Long cardId, Integer quantity) {
        CardDeck deck = validateDeckAccess(deckId, userid);
        Card card = cardRepository.findById(cardId)
            .orElseThrow(() -> new RuntimeException("Card not found: " + cardId));

        DeckDetail deckDetail = deckDetailRepository.findByDeckAndCard(deck, card)
            .orElseThrow(() -> new RuntimeException("Card not in deck"));

        int newQuantity = deckDetail.getQuantity() - quantity;

        if (newQuantity <= 0) {
            deckDetailRepository.delete(deckDetail);
        } else {
            deckDetail.updateQuantity(newQuantity);
            deckDetailRepository.save(deckDetail);
        }

        // Update deck total cards
        deck.updateTotalCards();
        deckRepository.save(deck);
    }

    // ========== DECK UTILITIES ==========

    @Transactional(readOnly = true)
    public Map<String, Object> getDeckStats(Long deckId, String userid) {
        CardDeck deck = validateDeckAccess(deckId, userid);
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

    @Transactional(readOnly = true)
    public Optional<CardDeck> getDeckByCode(String deckCode) {
        return deckRepository.findByDeckCode(deckCode);
    }

    @Transactional(readOnly = true)
    public List<DeckDetail> getDeckCardsByDeckId(Long deckId) {
        CardDeck deck = deckRepository.findById(deckId)
            .orElseThrow(() -> new RuntimeException("Deck not found: " + deckId));

        return deckDetailRepository.findByDeckAndIsSideboardFalseOrderByOrderIndexAsc(deck);
    }

    public CardDeck generateRandomDeck(String userid, String deckName) {
        // userid로 User 조회 후 Gamer 확인
        User user = userRepository.findByUserid(userid)
            .orElseThrow(() -> new RuntimeException("User not found: " + userid));

        Gamer gamer = gamerRepository.findById(user.getUid())
            .orElseThrow(() -> new RuntimeException("User is not a gamer: " + userid));

        // Create new deck
        CardDeck deck = CardDeck.builder()
            .gamer(gamer)
            .deckName(deckName)
            .description("Randomly generated deck")
            .build();

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
        addCardToDeck(deck.getDeckId(), userid, randomLeader.getCardId(), 1);
        deck.changeLeaderCard(randomLeader);

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
                addCardToDeck(deck.getDeckId(), userid, randomCard.getCardId(), 1);
                cardCounts.put(randomCard.getCardId(), currentCount + 1);
                cardsToAdd--;
            }
        }

        // Update and save deck
        deck.updateTotalCards();
        return deckRepository.save(deck);
    }

    // ========== PRIVATE HELPER METHODS ==========

    // 더 이상 사용하지 않는 메서드 - 삭제 예정
    @Deprecated
    private Gamer validateGamerAccess(Long userId) {
        return gamerRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Gamer not found: " + userId));
    }

    private CardDeck validateDeckAccess(Long deckId, String userid) {
        // userid로 User 조회 후 Gamer 확인
        User user = userRepository.findByUserid(userid)
            .orElseThrow(() -> new RuntimeException("User not found: " + userid));

        Gamer gamer = gamerRepository.findById(user.getUid())
            .orElseThrow(() -> new RuntimeException("User is not a gamer: " + userid));

        return deckRepository.findByDeckIdAndGamer(deckId, gamer)
            .orElseThrow(() -> new RuntimeException("Deck not found: " + deckId));
    }

    private CardDeck createNewDeck(Gamer gamer, DeckSaveRequestDTO request) {
        Card leaderCard = null;
        if (request.getLeaderCardId() != null) {
            leaderCard = cardRepository.findById(request.getLeaderCardId())
                .orElseThrow(() -> new RuntimeException("Leader card not found"));
        }

        return CardDeck.builder()
            .gamer(gamer)
            .deckName(request.getDeckName())
            .description(request.getDescription())
            .deckType(DeckType.valueOf(request.getDeckType()))
            .isPublic(request.getIsPublic())
            .isTournamentLegal(request.getIsTournamentLegal())
            .leaderCard(leaderCard)
            .build();
    }

    private CardDeck updateExistingDeck(String userid, DeckSaveRequestDTO request) {
        CardDeck deck = validateDeckAccess(request.getDeckId(), userid);

        deck.updateDeckInfo(request.getDeckName(), request.getDescription());
        deck.updateSettings(
            DeckType.valueOf(request.getDeckType()),
            request.getIsPublic(),
            request.getIsTournamentLegal()
        );

        if (request.getLeaderCardId() != null) {
            Card leaderCard = cardRepository.findById(request.getLeaderCardId())
                .orElseThrow(() -> new RuntimeException("Leader card not found"));
            deck.changeLeaderCard(leaderCard);
        }

        // Clear existing cards (will be re-added)
        deckDetailRepository.deleteByDeck(deck);

        return deck;
    }

    private void saveCards(CardDeck deck, List<DeckSaveRequestDTO.DeckCardDTO> cards) {
        for (DeckSaveRequestDTO.DeckCardDTO cardDTO : cards) {
            Card card = cardRepository.findById(cardDTO.getCardId())
                .orElseThrow(() -> new RuntimeException("Card not found: " + cardDTO.getCardId()));

            DeckDetail deckDetail = DeckDetail.builder()
                .deck(deck)
                .card(card)
                .quantity(cardDTO.getQuantity())
                .isSideboard(cardDTO.getIsSideboard())
                .orderIndex(cardDTO.getOrderIndex())
                .build();

            deckDetailRepository.save(deckDetail);
        }
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

    private void validateCardQuantity(Card card, int newQuantity) {
        if (!card.getCardType().equals("LEADER") && newQuantity > 3) {
            throw new RuntimeException("Cannot add more than 3 copies of the same card");
        }
        if (card.getCardType().equals("LEADER") && newQuantity > 1) {
            throw new RuntimeException("Cannot add more than 1 leader card");
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