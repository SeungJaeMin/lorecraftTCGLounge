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
    private String cardType;
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
        // Card type will be set based on the actual entity type
        this.cardType = card.getClass().getSimpleName().replace("Card", "").toUpperCase();
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
    public String getCardType() { return cardType; }
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
    public void setCardType(String cardType) { this.cardType = cardType; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setImages(List<CardImageDTO> images) { this.images = images; }
}