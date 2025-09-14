package com.lorecraft.tcglounge.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "deck_details")
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
    
    public DeckDetail() {}
    
    public DeckDetail(CardDeck deck, Card card, Integer quantity) {
        this.deck = deck;
        this.card = card;
        this.quantity = quantity;
    }
    
    // Getters
    public Long getDetailId() { return detailId; }
    public CardDeck getDeck() { return deck; }
    public Card getCard() { return card; }
    public Integer getQuantity() { return quantity; }
    public Boolean getIsSideboard() { return isSideboard; }
    public Integer getOrderIndex() { return orderIndex; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    
    // Setters
    public void setDetailId(Long detailId) { this.detailId = detailId; }
    public void setDeck(CardDeck deck) { this.deck = deck; }
    public void setCard(Card card) { this.card = card; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setIsSideboard(Boolean isSideboard) { this.isSideboard = isSideboard; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}