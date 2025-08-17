package com.lorecraft.tcglounge.domain.card.entity;

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

@Entity
@Table(name = "cards")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "card_type")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    private Long cardId;

    @Column(name = "card_name", nullable = false, length = 100)
    private String cardName;

    @Column(name = "card_img", length = 255)
    private String cardImg;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "card_color", nullable = false)
    private CardColor cardColor;

    @Column(name = "burst_number")
    private Integer burstNumber;

    @Column(name = "rarity", nullable = false)
    @Enumerated(EnumType.STRING)
    private CardRarity rarity;

    @Column(name = "cost")
    private Integer cost;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "release_set", length = 50)
    private String releaseSet;

    @Column(name = "card_number", length = 20)
    private String cardNumber;

    @Column(name = "card_type", insertable = false, updatable = false)
    private String cardType;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 연관관계
    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DeckCard> deckCards = new ArrayList<>();

    // 비즈니스 메서드
    public void deactivate() {
        this.isActive = false;
    }

    public void activate() {
        this.isActive = true;
    }

    public boolean isLeader() {
        return this instanceof Leader;
    }

    public boolean isUnit() {
        return this instanceof Unit;
    }

    public boolean isItem() {
        return this instanceof Item;
    }

    public boolean isField() {
        return this instanceof Field;
    }

    public boolean isSpell() {
        return this instanceof Spell;
    }

    public String getFullCardNumber() {
        return releaseSet + "-" + cardNumber;
    }

    public boolean canBeUsedInDeck() {
        return isActive;
    }

    public void addToDeck(DeckCard deckCard) {
        deckCards.add(deckCard);
        deckCard.setCard(this);
    }

    public void removeFromDeck(DeckCard deckCard) {
        deckCards.remove(deckCard);
        deckCard.setCard(null);
    }

    // Enum 정의
    public enum CardColor {
        RED("적색"),
        BLUE("청색"), 
        GREEN("녹색"),
        YELLOW("황색"),
        WHITE("백색"),
        BLACK("흑색"),
        COLORLESS("무색");

        private final String koreanName;

        CardColor(String koreanName) {
            this.koreanName = koreanName;
        }

        public String getKoreanName() {
            return koreanName;
        }
    }

    public enum CardRarity {
        COMMON("커먼"),
        RARE("레어"),
        SUPER_RARE("슈퍼레어"),
        ULTRA_RARE("울트라레어"),
        SECRET_RARE("시크릿레어"),
        LEGENDARY("레전더리");

        private final String koreanName;

        CardRarity(String koreanName) {
            this.koreanName = koreanName;
        }

        public String getKoreanName() {
            return koreanName;
        }
    }
}