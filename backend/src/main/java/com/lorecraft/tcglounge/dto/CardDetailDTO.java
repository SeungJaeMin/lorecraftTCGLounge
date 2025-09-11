package com.lorecraft.tcglounge.dto;

import com.lorecraft.tcglounge.entity.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CardDetailDTO {
    
    // Common fields
    private Long cardId;
    private String cardName;
    private String cardImg;
    private String description;
    private String cardColor;
    private String rarity;
    private Integer cost;
    private String cardNumber;
    private String cardType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Leader specific fields
    private String leaderSkill;
    private Boolean isAwakened;
    private Integer burstSlot1;
    private Integer burstSlot2;
    private Integer burstSlot3;
    
    // Unit specific fields
    private Integer power;
    
    // Item specific fields
    private String effect;
    private String activationCondition;
    private Boolean isConsumable;
    
    // Field specific fields
    private String fieldEffect;
    private String affectedColors;
    private String affectedTypes;
    
    // Spell specific fields
    private String spellEffect;
    private String targetType;
    
    // Shared burst value
    private Integer burstValue;
    
    // Images
    private List<CardImageDTO> images;
    
    // Constructors
    public CardDetailDTO() {}
    
    public CardDetailDTO(Card card) {
        this.cardId = card.getCardId();
        this.cardName = card.getCardName();
        this.cardImg = card.getCardImg();
        this.description = card.getDescription();
        if (card.getCardColor() != null) {
            this.cardColor = card.getCardColor().name();
        }
        if (card.getRarity() != null) {
            this.rarity = card.getRarity().name();
        }
        this.cost = card.getCost();
        this.cardNumber = card.getCardNumber();
        this.createdAt = card.getCreatedAt();
        this.updatedAt = card.getUpdatedAt();
        
        // Set card type based on instance
        if (card instanceof LeaderCard) {
            this.cardType = "LEADER";
            LeaderCard leader = (LeaderCard) card;
            this.leaderSkill = leader.getLeaderSkill();
            this.isAwakened = leader.getIsAwakened();
            this.burstSlot1 = leader.getBurstSlot1();
            this.burstSlot2 = leader.getBurstSlot2();
            this.burstSlot3 = leader.getBurstSlot3();
        } else if (card instanceof UnitCard) {
            this.cardType = "UNIT";
            UnitCard unit = (UnitCard) card;
            this.power = unit.getPower();
            this.burstValue = unit.getBurstValue();
        } else if (card instanceof ItemCard) {
            this.cardType = "ITEM";
            ItemCard item = (ItemCard) card;
            this.effect = item.getEffect();
            this.activationCondition = item.getActivationCondition();
            this.isConsumable = item.getIsConsumable();
            this.burstValue = item.getBurstValue();
        } else if (card instanceof FieldCard) {
            this.cardType = "FIELD";
            FieldCard field = (FieldCard) card;
            this.fieldEffect = field.getFieldEffect();
            this.affectedColors = field.getAffectedColors();
            this.affectedTypes = field.getAffectedTypes();
            this.burstValue = field.getBurstValue();
        } else if (card instanceof SpellCard) {
            this.cardType = "SPELL";
            SpellCard spell = (SpellCard) card;
            this.spellEffect = spell.getSpellEffect();
            this.targetType = spell.getTargetType();
            this.burstValue = spell.getBurstValue();
        }
    }
    
    public CardDetailDTO(Card card, List<CardImage> images) {
        this(card);
        if (images != null) {
            this.images = images.stream()
                .map(CardImageDTO::new)
                .collect(Collectors.toList());
        }
    }
    
    // Getters
    public Long getCardId() { return cardId; }
    public String getCardName() { return cardName; }
    public String getCardImg() { return cardImg; }
    public String getDescription() { return description; }
    public String getCardColor() { return cardColor; }
    public String getRarity() { return rarity; }
    public Integer getCost() { return cost; }
    public String getCardNumber() { return cardNumber; }
    public String getCardType() { return cardType; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public String getLeaderSkill() { return leaderSkill; }
    public Boolean getIsAwakened() { return isAwakened; }
    public Integer getBurstSlot1() { return burstSlot1; }
    public Integer getBurstSlot2() { return burstSlot2; }
    public Integer getBurstSlot3() { return burstSlot3; }
    public Integer getPower() { return power; }
    public String getEffect() { return effect; }
    public String getActivationCondition() { return activationCondition; }
    public Boolean getIsConsumable() { return isConsumable; }
    public String getFieldEffect() { return fieldEffect; }
    public String getAffectedColors() { return affectedColors; }
    public String getAffectedTypes() { return affectedTypes; }
    public String getSpellEffect() { return spellEffect; }
    public String getTargetType() { return targetType; }
    public Integer getBurstValue() { return burstValue; }
    public List<CardImageDTO> getImages() { return images; }
    
    // Setters
    public void setCardId(Long cardId) { this.cardId = cardId; }
    public void setCardName(String cardName) { this.cardName = cardName; }
    public void setCardImg(String cardImg) { this.cardImg = cardImg; }
    public void setDescription(String description) { this.description = description; }
    public void setCardColor(String cardColor) { this.cardColor = cardColor; }
    public void setRarity(String rarity) { this.rarity = rarity; }
    public void setCost(Integer cost) { this.cost = cost; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public void setCardType(String cardType) { this.cardType = cardType; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setLeaderSkill(String leaderSkill) { this.leaderSkill = leaderSkill; }
    public void setIsAwakened(Boolean isAwakened) { this.isAwakened = isAwakened; }
    public void setBurstSlot1(Integer burstSlot1) { this.burstSlot1 = burstSlot1; }
    public void setBurstSlot2(Integer burstSlot2) { this.burstSlot2 = burstSlot2; }
    public void setBurstSlot3(Integer burstSlot3) { this.burstSlot3 = burstSlot3; }
    public void setPower(Integer power) { this.power = power; }
    public void setEffect(String effect) { this.effect = effect; }
    public void setActivationCondition(String activationCondition) { this.activationCondition = activationCondition; }
    public void setIsConsumable(Boolean isConsumable) { this.isConsumable = isConsumable; }
    public void setFieldEffect(String fieldEffect) { this.fieldEffect = fieldEffect; }
    public void setAffectedColors(String affectedColors) { this.affectedColors = affectedColors; }
    public void setAffectedTypes(String affectedTypes) { this.affectedTypes = affectedTypes; }
    public void setSpellEffect(String spellEffect) { this.spellEffect = spellEffect; }
    public void setTargetType(String targetType) { this.targetType = targetType; }
    public void setBurstValue(Integer burstValue) { this.burstValue = burstValue; }
    public void setImages(List<CardImageDTO> images) { this.images = images; }
}