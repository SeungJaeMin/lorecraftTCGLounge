package com.lorecraft.tcglounge.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "deck_details")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeckDetail {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detail_id")
    private Long detailId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deck_id", nullable = false)
    private CardDeck deck;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;
    
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    
    @Column(name = "is_sideboard")
    private Boolean isSideboard = false;
    
    @Column(name = "order_index")
    private Integer orderIndex;
    
    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Builder
    private DeckDetail(CardDeck deck, Card card, Integer quantity,
                      Boolean isSideboard, Integer orderIndex) {
        this.deck = deck;
        this.card = card;
        this.quantity = quantity != null ? quantity : 1;
        this.isSideboard = isSideboard != null ? isSideboard : false;
        this.orderIndex = orderIndex;
    }

    // JPA용 간단 생성자 (하위 호환성)
    public DeckDetail(CardDeck deck, Card card, Integer quantity) {
        this.deck = deck;
        this.card = card;
        this.quantity = quantity != null ? quantity : 1;
        this.isSideboard = false;
    }
    
    // Getters는 @Getter 어노테이션으로 자동 생성

    // Business methods (Setter 대체)
    public void updateQuantity(Integer quantity) {
        if (quantity != null && quantity > 0) {
            this.quantity = quantity;
        }
    }

    public void increaseQuantity(Integer amount) {
        if (amount != null && amount > 0) {
            this.quantity += amount;
        }
    }

    public void decreaseQuantity(Integer amount) {
        if (amount != null && amount > 0 && this.quantity > amount) {
            this.quantity -= amount;
        }
    }

    public void moveToSideboard() {
        this.isSideboard = true;
    }

    public void moveToMainDeck() {
        this.isSideboard = false;
    }

    public void updateOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

}