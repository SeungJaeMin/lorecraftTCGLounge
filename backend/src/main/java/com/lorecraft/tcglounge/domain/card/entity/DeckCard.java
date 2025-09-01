package com.lorecraft.tcglounge.domain.card.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "deck_detail",
    uniqueConstraints = @UniqueConstraint(columnNames = {"deck_id", "card_id"}))
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DeckCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deck_id", nullable = false)
    private CardDeck deck;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "order_index")
    private Integer orderIndex;

    @Column(name = "notes", length = 255)
    private String notes;

    @CreatedDate
    @Column(name = "added_at", nullable = false)
    private LocalDateTime addedAt;

    // 비즈니스 메서드
    public void increaseQuantity(int amount) {
        if (amount > 0) {
            this.quantity += amount;
            validateQuantity();
        }
    }

    public void decreaseQuantity(int amount) {
        if (amount > 0) {
            this.quantity = Math.max(0, this.quantity - amount);
        }
    }

    public void setQuantity(int newQuantity) {
        if (newQuantity >= 0) {
            this.quantity = newQuantity;
            validateQuantity();
        }
    }

    private void validateQuantity() {
        int maxAllowed = getMaxAllowedQuantity();
        if (this.quantity > maxAllowed) {
            this.quantity = maxAllowed;
        }
    }

    private int getMaxAllowedQuantity() {
        if (card.isLeader()) return 1;
        if (card.getRarity() == Card.CardRarity.LEGENDARY) return 1;
        if (card.getRarity() == Card.CardRarity.SECRET_RARE) return 2;
        return 3; // 기본 최대 복사본 수
    }

    public boolean isAtMaxQuantity() {
        return quantity >= getMaxAllowedQuantity();
    }

    public boolean canIncreaseQuantity() {
        return quantity < getMaxAllowedQuantity();
    }

    public int getTotalCost() {
        if (card.getCost() == null) return 0;
        return card.getCost() * quantity;
    }

    public boolean hasNotes() {
        return notes != null && !notes.trim().isEmpty();
    }

    public void addNote(String note) {
        if (this.notes == null || this.notes.trim().isEmpty()) {
            this.notes = note;
        } else {
            this.notes += "; " + note;
        }
    }

    public void clearNotes() {
        this.notes = null;
    }

    public boolean isValidQuantity() {
        return quantity > 0 && quantity <= getMaxAllowedQuantity();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        DeckCard deckCard = (DeckCard) obj;
        return deck.getId().equals(deckCard.deck.getId()) && 
               card.getCardId().equals(deckCard.card.getCardId());
    }

    @Override
    public int hashCode() {
        return deck.getId().hashCode() + card.getCardId().hashCode();
    }
}