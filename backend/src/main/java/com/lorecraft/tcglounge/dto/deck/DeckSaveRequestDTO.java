package com.lorecraft.tcglounge.dto.deck;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Getter
@NoArgsConstructor
@ToString
public class DeckSaveRequestDTO {

    // 덱 ID (수정시에만 필요, 생성시에는 null)
    private Long deckId;

    @NotBlank(message = "덱 이름은 필수입니다")
    @Size(min = 1, max = 50, message = "덱 이름은 1-50자여야 합니다")
    private String deckName;

    @Size(max = 500, message = "설명은 500자 이하여야 합니다")
    private String description;

    private String deckType = "STANDARD";

    private Boolean isPublic = false;

    private Boolean isTournamentLegal = false;

    private Long leaderCardId;

    @Valid
    private List<DeckCardDTO> cards;

    @Getter
    @NoArgsConstructor
    @ToString
    public static class DeckCardDTO {
        @NotNull(message = "카드 ID는 필수입니다")
        private Long cardId;

        @Min(value = 1, message = "수량은 1 이상이어야 합니다")
        private Integer quantity = 1;

        private Boolean isSideboard = false;

        private Integer orderIndex;

        // Setters for JSON deserialization
        public void setCardId(Long cardId) {
            this.cardId = cardId;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public void setIsSideboard(Boolean isSideboard) {
            this.isSideboard = isSideboard;
        }

        public void setOrderIndex(Integer orderIndex) {
            this.orderIndex = orderIndex;
        }
    }

    // Setters for JSON deserialization
    public void setDeckId(Long deckId) {
        this.deckId = deckId;
    }

    public void setDeckName(String deckName) {
        this.deckName = deckName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDeckType(String deckType) {
        this.deckType = deckType;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public void setIsTournamentLegal(Boolean isTournamentLegal) {
        this.isTournamentLegal = isTournamentLegal;
    }

    public void setLeaderCardId(Long leaderCardId) {
        this.leaderCardId = leaderCardId;
    }

    public void setCards(List<DeckCardDTO> cards) {
        this.cards = cards;
    }
}