package com.lorecraft.tcglounge.domain.card.entity;

import com.lorecraft.tcglounge.domain.user.entity.Gamer;
import com.lorecraft.tcglounge.domain.competition.entity.Enrollment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@Table(name = "card_decks")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CardDeck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "deck_name", nullable = false, length = 100)
    private String deckName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = false;

    @Column(name = "is_valid", nullable = false)
    private Boolean isValid = false;

    @Column(name = "format_type", length = 50)
    private String formatType;

    @Column(name = "total_cards", nullable = false)
    private Integer totalCards = 0;

    @Column(name = "deck_code", unique = true, length = 20)
    private String deckCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "deck_status", nullable = false)
    private DeckStatus deckStatus = DeckStatus.DRAFT;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gamer_id", nullable = false)
    private Gamer gamer;

    @OneToMany(mappedBy = "deck", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<DeckCard> deckCards = new ArrayList<>();

    @OneToMany(mappedBy = "selectedDeck", fetch = FetchType.LAZY)
    private List<Enrollment> enrollments = new ArrayList<>();

    // 비즈니스 메서드
    public void addCard(Card card, int quantity) {
        DeckCard existingDeckCard = findDeckCard(card);
        if (existingDeckCard != null) {
            existingDeckCard.increaseQuantity(quantity);
        } else {
            DeckCard newDeckCard = DeckCard.builder()
                .deck(this)
                .card(card)
                .quantity(quantity)
                .build();
            deckCards.add(newDeckCard);
        }
        updateTotalCards();
        validateDeck();
    }

    public void removeCard(Card card, int quantity) {
        DeckCard deckCard = findDeckCard(card);
        if (deckCard != null) {
            deckCard.decreaseQuantity(quantity);
            if (deckCard.getQuantity() <= 0) {
                deckCards.remove(deckCard);
            }
        }
        updateTotalCards();
        validateDeck();
    }

    public void removeCard(Card card) {
        deckCards.removeIf(deckCard -> deckCard.getCard().equals(card));
        updateTotalCards();
        validateDeck();
    }

    private DeckCard findDeckCard(Card card) {
        return deckCards.stream()
            .filter(deckCard -> deckCard.getCard().equals(card))
            .findFirst()
            .orElse(null);
    }

    public void updateTotalCards() {
        this.totalCards = deckCards.stream()
            .mapToInt(DeckCard::getQuantity)
            .sum();
    }

    public void validateDeck() {
        boolean hasLeader = hasLeaderCard();
        boolean isValidSize = isValidDeckSize();
        boolean hasValidCardLimits = hasValidCardLimits();
        
        this.isValid = hasLeader && isValidSize && hasValidCardLimits;
        
        if (isValid && deckStatus == DeckStatus.DRAFT) {
            this.deckStatus = DeckStatus.COMPLETED;
        } else if (!isValid && deckStatus == DeckStatus.COMPLETED) {
            this.deckStatus = DeckStatus.DRAFT;
        }
    }

    private boolean hasLeaderCard() {
        return deckCards.stream()
            .anyMatch(deckCard -> deckCard.getCard().isLeader());
    }

    private boolean isValidDeckSize() {
        return totalCards >= 40 && totalCards <= 60; // 일반적인 TCG 덱 사이즈
    }

    private boolean hasValidCardLimits() {
        return deckCards.stream()
            .allMatch(deckCard -> deckCard.getQuantity() <= getMaxCopiesPerCard(deckCard.getCard()));
    }

    private int getMaxCopiesPerCard(Card card) {
        if (card.isLeader()) return 1;
        if (card.getRarity() == Card.CardRarity.LEGENDARY) return 1;
        if (card.getRarity() == Card.CardRarity.SECRET_RARE) return 2;
        return 3; // 기본 최대 복사본 수
    }

    public void publish() {
        if (isValid) {
            this.isPublic = true;
            this.deckStatus = DeckStatus.PUBLISHED;
        }
    }

    public void unpublish() {
        this.isPublic = false;
        this.deckStatus = DeckStatus.COMPLETED;
    }

    public void archive() {
        this.deckStatus = DeckStatus.ARCHIVED;
        this.isPublic = false;
    }

    public Map<Card.CardColor, Long> getColorDistribution() {
        return deckCards.stream()
            .collect(Collectors.groupingBy(
                deckCard -> deckCard.getCard().getCardColor(),
                Collectors.summingLong(DeckCard::getQuantity)
            ));
    }

    public Map<String, Long> getTypeDistribution() {
        return deckCards.stream()
            .collect(Collectors.groupingBy(
                deckCard -> deckCard.getCard().getCardType(),
                Collectors.summingLong(DeckCard::getQuantity)
            ));
    }

    public boolean canBeUsedInCompetition() {
        return isValid && (deckStatus == DeckStatus.COMPLETED || deckStatus == DeckStatus.PUBLISHED);
    }

    public int getUniqueCardCount() {
        return deckCards.size();
    }

    public double getAverageCost() {
        return deckCards.stream()
            .filter(deckCard -> deckCard.getCard().getCost() != null)
            .mapToDouble(deckCard -> deckCard.getCard().getCost() * deckCard.getQuantity())
            .average()
            .orElse(0.0);
    }

    // Enum 정의
    public enum DeckStatus {
        DRAFT("작성중"),
        COMPLETED("완성"),
        PUBLISHED("공개"),
        ARCHIVED("보관");

        private final String koreanName;

        DeckStatus(String koreanName) {
            this.koreanName = koreanName;
        }

        public String getKoreanName() {
            return koreanName;
        }

        public boolean isActive() {
            return this == COMPLETED || this == PUBLISHED;
        }
    }
}