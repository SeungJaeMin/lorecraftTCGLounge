package com.lorecraft.tcglounge.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "cards")
@EntityListeners(AuditingEntityListener.class)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
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
    
    @Column(name = "burst_slot1", nullable = false)
    private Integer burstSlot1 = 0;
    
    @Column(name = "burst_slot2", nullable = false)
    private Integer burstSlot2 = 0;
    
    @Column(name = "burst_slot3", nullable = false)
    private Integer burstSlot3 = 0;
    
    @Column(name = "burst_value", nullable = false)
    private Integer burstValue = 0;
    
    @Column(name = "life_points", nullable = false)
    private Integer lifePoints = 0;
    
    @Column(name = "power", nullable = false)
    private Integer power = 0;
    
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
    public Integer getBurstSlot1() { return burstSlot1; }
    public Integer getBurstSlot2() { return burstSlot2; }
    public Integer getBurstSlot3() { return burstSlot3; }
    public Integer getBurstValue() { return burstValue; }
    public Integer getLifePoints() { return lifePoints; }
    public Integer getPower() { return power; }
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
    public void setBurstSlot1(Integer burstSlot1) { this.burstSlot1 = burstSlot1; }
    public void setBurstSlot2(Integer burstSlot2) { this.burstSlot2 = burstSlot2; }
    public void setBurstSlot3(Integer burstSlot3) { this.burstSlot3 = burstSlot3; }
    public void setBurstValue(Integer burstValue) { this.burstValue = burstValue; }
    public void setLifePoints(Integer lifePoints) { this.lifePoints = lifePoints; }
    public void setPower(Integer power) { this.power = power; }
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