package com.lorecraft.tcglounge.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "items")
@DiscriminatorValue("ITEM")
public class ItemCard extends Card {
    
    @Column(name = "effect", columnDefinition = "TEXT")
    private String effect;
    
    @Column(name = "activation_condition", columnDefinition = "TEXT")
    private String activationCondition;
    
    @Column(name = "is_consumable")
    private Boolean isConsumable = true;
    
    @Column(name = "burst_value", nullable = false)
    private Integer burstValue; // 1~3
    
    // Constructors
    public ItemCard() {
        super();
    }
    
    public ItemCard(String cardName, CardColor cardColor, CardRarity rarity, Integer cost,
                    String effect, Integer burstValue) {
        super(cardName, cardColor, rarity, cost);
        this.effect = effect;
        this.burstValue = burstValue;
    }
    
    // Getters
    public String getEffect() { return effect; }
    public String getActivationCondition() { return activationCondition; }
    public Boolean getIsConsumable() { return isConsumable; }
    public Integer getBurstValue() { return burstValue; }
    
    // Setters
    public void setEffect(String effect) { this.effect = effect; }
    public void setActivationCondition(String activationCondition) { this.activationCondition = activationCondition; }
    public void setIsConsumable(Boolean isConsumable) { this.isConsumable = isConsumable; }
    public void setBurstValue(Integer burstValue) { this.burstValue = burstValue; }
}