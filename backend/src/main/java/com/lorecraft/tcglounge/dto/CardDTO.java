package com.lorecraft.tcglounge.dto;

import com.lorecraft.tcglounge.entity.Card;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CardDTO {
    
    private Long cardId;
    private String cardName;
    private String cardImg;
    private String description;
    private Card.CardColor cardColor;
    private Card.CardRarity rarity;
    private Integer cost;
    private String cardNumber;
    private Integer burstSlot1;
    private Integer burstSlot2;
    private Integer burstSlot3;
    private Integer burstValue;
    private Integer lifePoints;
    private Integer power;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CardImageDTO> images;
    
    public CardDTO() {}
    
    public CardDTO(Card card) {
        this.cardId = card.getCardId();
        this.cardName = card.getCardName();
        this.cardImg = card.getCardImg();
        this.description = card.getDescription();
        this.cardColor = card.getCardColor();
        this.rarity = card.getRarity();
        this.cost = card.getCost();
        this.cardNumber = card.getCardNumber();
        this.burstSlot1 = card.getBurstSlot1();
        this.burstSlot2 = card.getBurstSlot2();
        this.burstSlot3 = card.getBurstSlot3();
        this.burstValue = card.getBurstValue();
        this.lifePoints = card.getLifePoints();
        this.power = card.getPower();
        this.createdAt = card.getCreatedAt();
        this.updatedAt = card.getUpdatedAt();
        // images는 별도로 설정
    }
    
    public CardDTO(Card card, List<CardImageDTO> images) {
        this(card);
        this.images = images;
    }
    
    // Getters
    public Long getCardId() { return cardId; }
    public String getCardName() { return cardName; }
    public String getCardImg() { return cardImg; }
    public String getDescription() { return description; }
    public Card.CardColor getCardColor() { return cardColor; }
    public Card.CardRarity getRarity() { return rarity; }
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
    public List<CardImageDTO> getImages() { return images; }
    
    // Setters
    public void setCardId(Long cardId) { this.cardId = cardId; }
    public void setCardName(String cardName) { this.cardName = cardName; }
    public void setCardImg(String cardImg) { this.cardImg = cardImg; }
    public void setDescription(String description) { this.description = description; }
    public void setCardColor(Card.CardColor cardColor) { this.cardColor = cardColor; }
    public void setRarity(Card.CardRarity rarity) { this.rarity = rarity; }
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
    public void setImages(List<CardImageDTO> images) { this.images = images; }
}