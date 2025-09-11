package com.lorecraft.tcglounge.dto;

public class CardUpdateDTO {
    
    // Common fields for all card types
    private String cardName;
    private String cardImg;
    private String description;
    private String cardColor;
    private String rarity;
    private Integer cost;
    private String cardNumber;
    
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
    
    // Shared burst value for Unit, Item, Field, Spell
    private Integer burstValue;
    
    // Constructors
    public CardUpdateDTO() {}
    
    // Getters
    public String getCardName() { return cardName; }
    public String getCardImg() { return cardImg; }
    public String getDescription() { return description; }
    public String getCardColor() { return cardColor; }
    public String getRarity() { return rarity; }
    public Integer getCost() { return cost; }
    public String getCardNumber() { return cardNumber; }
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
    
    // Setters
    public void setCardName(String cardName) { this.cardName = cardName; }
    public void setCardImg(String cardImg) { this.cardImg = cardImg; }
    public void setDescription(String description) { this.description = description; }
    public void setCardColor(String cardColor) { this.cardColor = cardColor; }
    public void setRarity(String rarity) { this.rarity = rarity; }
    public void setCost(Integer cost) { this.cost = cost; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
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
}