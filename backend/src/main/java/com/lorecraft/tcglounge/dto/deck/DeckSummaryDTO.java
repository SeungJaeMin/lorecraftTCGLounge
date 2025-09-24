package com.lorecraft.tcglounge.dto.deck;

import lombok.Builder;
import lombok.Getter;
import com.lorecraft.tcglounge.entity.CardDeck;
import java.time.LocalDateTime;

@Getter
public class DeckSummaryDTO {

    private final Long deckId;
    private final String deckName;
    private final String description;
    private final String deckType;
    private final Boolean isPublic;
    private final Integer totalCards;
    private final String deckCode;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    // 리더 카드 간단 정보
    private final LeaderCardSummary leaderCard;

    @Builder
    private DeckSummaryDTO(Long deckId, String deckName, String description, String deckType,
                          Boolean isPublic, Integer totalCards, String deckCode,
                          LocalDateTime createdAt, LocalDateTime updatedAt,
                          LeaderCardSummary leaderCard) {
        this.deckId = deckId;
        this.deckName = deckName;
        this.description = description;
        this.deckType = deckType;
        this.isPublic = isPublic;
        this.totalCards = totalCards;
        this.deckCode = deckCode;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.leaderCard = leaderCard;
    }

    // Entity에서 DTO로 변환
    public static DeckSummaryDTO from(CardDeck deck) {
        LeaderCardSummary leaderCardSummary = null;
        if (deck.getLeaderCard() != null) {
            leaderCardSummary = LeaderCardSummary.builder()
                .cardId(deck.getLeaderCard().getCardId())
                .cardName(deck.getLeaderCard().getCardName())
                .cardColor(deck.getLeaderCard().getCardColor().toString())
                .cardType(deck.getLeaderCard().getCardType())
                .build();
        }

        return DeckSummaryDTO.builder()
            .deckId(deck.getDeckId())
            .deckName(deck.getDeckName())
            .description(deck.getDescription())
            .deckType(deck.getDeckType().toString())
            .isPublic(deck.getIsPublic())
            .totalCards(deck.getTotalCards())
            .deckCode(deck.getDeckCode())
            .createdAt(deck.getCreatedAt())
            .updatedAt(deck.getUpdatedAt())
            .leaderCard(leaderCardSummary)
            .build();
    }

    @Getter
    public static class LeaderCardSummary {
        private final Long cardId;
        private final String cardName;
        private final String cardColor;
        private final String cardType;

        @Builder
        private LeaderCardSummary(Long cardId, String cardName, String cardColor, String cardType) {
            this.cardId = cardId;
            this.cardName = cardName;
            this.cardColor = cardColor;
            this.cardType = cardType;
        }
    }
}