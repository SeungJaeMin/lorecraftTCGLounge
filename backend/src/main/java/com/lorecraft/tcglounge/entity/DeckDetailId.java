package com.lorecraft.tcglounge.entity;

import jakarta.persistence.*;

@Embeddable
public class DeckDetailId {
    
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