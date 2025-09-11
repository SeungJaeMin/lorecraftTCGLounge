package com.lorecraft.tcglounge.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "cards")
@EntityListeners(AuditingEntityListener.class)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "card_type")
public class Card {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    private Long cardId;
    
    @Column(name = "card_name", nullable = false)
    private String cardName;
    
    @Column(name = "card_img")
    private String cardImg;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "card_color")
    private CardColor cardColor;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "rarity")
    private CardRarity rarity;
    
    @Column(name = "cost")
    private Integer cost;
    
    @Column(name = "card_number")
    private String cardNumber;
    
    
    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public Card() {}
    
    public Card(String cardName, CardColor cardColor, CardRarity rarity, Integer cost) {
        this.cardName = cardName;
        this.cardColor = cardColor;
        this.rarity = rarity;
        this.cost = cost;
    }
    
    // Getters
    public Long getCardId() { return cardId; }
    public String getCardName() { return cardName; }
    public String getCardImg() { return cardImg; }
    public String getDescription() { return description; }
    public CardColor getCardColor() { return cardColor; }
    public CardRarity getRarity() { return rarity; }
    public Integer getCost() { return cost; }
    public String getCardNumber() { return cardNumber; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    
    // Setters
    public void setCardId(Long cardId) { this.cardId = cardId; }
    public void setCardName(String cardName) { this.cardName = cardName; }
    public void setCardImg(String cardImg) { this.cardImg = cardImg; }
    public void setDescription(String description) { this.description = description; }
    public void setCardColor(CardColor cardColor) { this.cardColor = cardColor; }
    public void setRarity(CardRarity rarity) { this.rarity = rarity; }
    public void setCost(Integer cost) { this.cost = cost; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Enums
    public enum CardColor {
        RED, BLUE, GREEN, YELLOW, BLACK, COLORLESS
    }
    
    public enum CardRarity {
        COMMON, RARE, SUPER_RARE, ULTRA_RARE, SECRET_RARE, LEGENDARY
    }
}