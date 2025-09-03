package com.lorecraft.tcglounge.domain.card.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "deck_details")
public class DeckDetail {
    
    @EmbeddedId
    private DeckDetailId id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("deckId")
    @JoinColumn(name = "deck_id")
    private CardDeck deck;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("cardId") 
    @JoinColumn(name = "card_id")
    private Card card;
    
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    
    @Column(name = "is_sideboard")
    private Boolean isSideboard = false;
    
    @Column(name = "order_index")
    private Integer orderIndex;
    
    public DeckDetail() {}
    
    public DeckDetail(CardDeck deck, Card card, Integer quantity) {
        this.id = new DeckDetailId(deck.getId(), card.getCardId());
        this.deck = deck;
        this.card = card;
        this.quantity = quantity;
    }
    
    // Getters
    public DeckDetailId getId() { return id; }
    public CardDeck getDeck() { return deck; }
    public Card getCard() { return card; }
    public Integer getQuantity() { return quantity; }
    public Boolean getIsSideboard() { return isSideboard; }
    public Integer getOrderIndex() { return orderIndex; }
    
    // Setters
    public void setId(DeckDetailId id) { this.id = id; }
    public void setDeck(CardDeck deck) { this.deck = deck; }
    public void setCard(Card card) { this.card = card; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setIsSideboard(Boolean isSideboard) { this.isSideboard = isSideboard; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }
}

@Embeddable
class DeckDetailId {
    
    @Column(name = "deck_id")
    private Long deckId;
    
    @Column(name = "card_id")
    private Long cardId;
    
    public DeckDetailId() {}
    
    public DeckDetailId(Long deckId, Long cardId) {
        this.deckId = deckId;
        this.cardId = cardId;
    }
    
    // Getters
    public Long getDeckId() { return deckId; }
    public Long getCardId() { return cardId; }
    
    // Setters
    public void setDeckId(Long deckId) { this.deckId = deckId; }
    public void setCardId(Long cardId) { this.cardId = cardId; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DeckDetailId)) return false;
        DeckDetailId that = (DeckDetailId) o;
        return deckId.equals(that.deckId) && cardId.equals(that.cardId);
    }
    
    @Override
    public int hashCode() {
        return deckId.hashCode() + cardId.hashCode();
    }
}