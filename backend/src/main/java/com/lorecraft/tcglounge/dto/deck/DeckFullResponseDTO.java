package com.lorecraft.tcglounge.dto.deck;

import lombok.Builder;
import lombok.Getter;
import com.lorecraft.tcglounge.entity.CardDeck;
import com.lorecraft.tcglounge.entity.DeckDetail;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class DeckFullResponseDTO {

    // 덱 기본 정보
    private final Long deckId;
    private final String deckName;
    private final String description;
    private final String deckType;
    private final Boolean isPublic;
    private final Boolean isTournamentLegal;
    private final Integer totalCards;
    private final String deckCode;
    private final Integer likesCount;
    private final Integer viewsCount;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    // 리더 카드 정보
    private final LeaderCardInfo leaderCard;

    // 덱에 포함된 카드들
    private final List<DeckCardInfo> cards;

    // 덱 통계 (완성도 등)
    private final DeckStats stats;

    @Builder
    private DeckFullResponseDTO(Long deckId, String deckName, String description, String deckType,
                               Boolean isPublic, Boolean isTournamentLegal, Integer totalCards,
                               String deckCode, Integer likesCount, Integer viewsCount,
                               LocalDateTime createdAt, LocalDateTime updatedAt,
                               LeaderCardInfo leaderCard, List<DeckCardInfo> cards, DeckStats stats) {
        this.deckId = deckId;
        this.deckName = deckName;
        this.description = description;
        this.deckType = deckType;
        this.isPublic = isPublic;
        this.isTournamentLegal = isTournamentLegal;
        this.totalCards = totalCards;
        this.deckCode = deckCode;
        this.likesCount = likesCount;
        this.viewsCount = viewsCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.leaderCard = leaderCard;
        this.cards = cards;
        this.stats = stats;
    }

    // Entity에서 DTO로 변환하는 정적 메서드
    public static DeckFullResponseDTO from(CardDeck deck, List<DeckDetail> deckDetails) {
        // 리더 카드 정보
        LeaderCardInfo leaderCardInfo = null;
        if (deck.getLeaderCard() != null) {
            leaderCardInfo = LeaderCardInfo.builder()
                .cardId(deck.getLeaderCard().getCardId())
                .cardName(deck.getLeaderCard().getCardName())
                .cardColor(deck.getLeaderCard().getCardColor().toString())
                .cardType(deck.getLeaderCard().getCardType())
                .build();
        }

        // 카드 리스트 변환
        List<DeckCardInfo> cards = deckDetails.stream()
            .map(detail -> DeckCardInfo.builder()
                .cardId(detail.getCard().getCardId())
                .cardName(detail.getCard().getCardName())
                .cardColor(detail.getCard().getCardColor().toString())
                .cardType(detail.getCard().getCardType())
                .rarity(detail.getCard().getRarity().toString())
                .cost(detail.getCard().getCost())
                .cardImg(detail.getCard().getCardImg())
                .quantity(detail.getQuantity())
                .isSideboard(detail.getIsSideboard())
                .orderIndex(detail.getOrderIndex())
                .build())
            .collect(Collectors.toList());

        // 덱 통계 계산
        DeckStats stats = DeckStats.builder()
            .totalCards(deck.getTotalCards())
            .isComplete(deck.getTotalCards() >= 40)
            .mainDeckCount((int) cards.stream().filter(c -> !c.isSideboard).count())
            .sideboardCount((int) cards.stream().filter(c -> c.isSideboard).count())
            .build();

        return DeckFullResponseDTO.builder()
            .deckId(deck.getDeckId())
            .deckName(deck.getDeckName())
            .description(deck.getDescription())
            .deckType(deck.getDeckType().toString())
            .isPublic(deck.getIsPublic())
            .isTournamentLegal(deck.getIsTournamentLegal())
            .totalCards(deck.getTotalCards())
            .deckCode(deck.getDeckCode())
            .likesCount(deck.getLikesCount())
            .viewsCount(deck.getViewsCount())
            .createdAt(deck.getCreatedAt())
            .updatedAt(deck.getUpdatedAt())
            .leaderCard(leaderCardInfo)
            .cards(cards)
            .stats(stats)
            .build();
    }

    @Getter
    public static class LeaderCardInfo {
        private final Long cardId;
        private final String cardName;
        private final String cardColor;
        private final String cardType;

        @Builder
        private LeaderCardInfo(Long cardId, String cardName, String cardColor, String cardType) {
            this.cardId = cardId;
            this.cardName = cardName;
            this.cardColor = cardColor;
            this.cardType = cardType;
        }
    }

    @Getter
    public static class DeckCardInfo {
        private final Long cardId;
        private final String cardName;
        private final String cardColor;
        private final String cardType;
        private final String rarity;
        private final Integer cost;
        private final String cardImg;
        private final Integer quantity;
        private final Boolean isSideboard;
        private final Integer orderIndex;

        @Builder
        private DeckCardInfo(Long cardId, String cardName, String cardColor, String cardType,
                           String rarity, Integer cost, String cardImg, Integer quantity,
                           Boolean isSideboard, Integer orderIndex) {
            this.cardId = cardId;
            this.cardName = cardName;
            this.cardColor = cardColor;
            this.cardType = cardType;
            this.rarity = rarity;
            this.cost = cost;
            this.cardImg = cardImg;
            this.quantity = quantity;
            this.isSideboard = isSideboard;
            this.orderIndex = orderIndex;
        }
    }

    @Getter
    public static class DeckStats {
        private final Integer totalCards;
        private final Boolean isComplete;
        private final Integer mainDeckCount;
        private final Integer sideboardCount;

        @Builder
        private DeckStats(Integer totalCards, Boolean isComplete, Integer mainDeckCount, Integer sideboardCount) {
            this.totalCards = totalCards;
            this.isComplete = isComplete;
            this.mainDeckCount = mainDeckCount;
            this.sideboardCount = sideboardCount;
        }
    }
}