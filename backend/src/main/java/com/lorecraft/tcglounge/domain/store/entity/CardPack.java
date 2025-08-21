package com.lorecraft.tcglounge.domain.store.entity;

import com.lorecraft.tcglounge.domain.card.entity.Card;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "card_packs")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CardPack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(name = "pack_name", nullable = false, length = 100)
    private String packName;

    @Column(name = "set_code", nullable = false, length = 20)
    private String setCode;

    @Column(name = "cards_per_pack", nullable = false)
    private Integer cardsPerPack;

    @Column(name = "rare_card_guaranteed", nullable = false)
    private Boolean rareCardGuaranteed = false;

    @Column(name = "pack_description", columnDefinition = "TEXT")
    private String packDescription;

    // 연관관계 - 카드팩에 포함될 수 있는 카드들
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "card_pack_contents",
        joinColumns = @JoinColumn(name = "card_pack_id"),
        inverseJoinColumns = @JoinColumn(name = "card_id")
    )
    private List<Card> availableCards = new ArrayList<>();

    // 비즈니스 메서드
    public void addAvailableCard(Card card) {
        if (!availableCards.contains(card)) {
            availableCards.add(card);
        }
    }

    public void removeAvailableCard(Card card) {
        availableCards.remove(card);
    }

    public boolean containsCard(Card card) {
        return availableCards.contains(card);
    }

    public int getTotalAvailableCards() {
        return availableCards.size();
    }

    public boolean hasGuaranteedRare() {
        return rareCardGuaranteed;
    }

    public List<Card> getRareCards() {
        return availableCards.stream()
            .filter(card -> card.getRarity() == Card.CardRarity.RARE ||
                           card.getRarity() == Card.CardRarity.SUPER_RARE ||
                           card.getRarity() == Card.CardRarity.ULTRA_RARE ||
                           card.getRarity() == Card.CardRarity.SECRET_RARE ||
                           card.getRarity() == Card.CardRarity.LEGENDARY)
            .toList();
    }

    public List<Card> getCommonCards() {
        return availableCards.stream()
            .filter(card -> card.getRarity() == Card.CardRarity.COMMON)
            .toList();
    }

    public boolean isFromSet(String setCode) {
        return this.setCode.equals(setCode);
    }

    public String getFullPackName() {
        return setCode + " - " + packName;
    }

    public boolean isValidPack() {
        return cardsPerPack > 0 && !availableCards.isEmpty();
    }
}