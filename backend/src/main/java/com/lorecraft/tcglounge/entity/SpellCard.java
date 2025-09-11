package com.lorecraft.tcglounge.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "spells")
@DiscriminatorValue("SPELL")
public class SpellCard extends Card {
    
    @Column(name = "spell_effect", columnDefinition = "TEXT")
    private String spellEffect;
    
    @Column(name = "target_type")
    private String targetType;
    
    @Column(name = "burst_value", nullable = false)
    private Integer burstValue; // 1~3
    
    // Constructors
    public SpellCard() {
        super();
    }
    
    public SpellCard(String cardName, CardColor cardColor, CardRarity rarity, Integer cost,
                     String spellEffect, String targetType, Integer burstValue) {
        super(cardName, cardColor, rarity, cost);
        this.spellEffect = spellEffect;
        this.targetType = targetType;
        this.burstValue = burstValue;
    }
    
    // Getters
    public String getSpellEffect() { return spellEffect; }
    public String getTargetType() { return targetType; }
    public Integer getBurstValue() { return burstValue; }
    
    // Setters
    public void setSpellEffect(String spellEffect) { this.spellEffect = spellEffect; }
    public void setTargetType(String targetType) { this.targetType = targetType; }
    public void setBurstValue(Integer burstValue) { this.burstValue = burstValue; }
}