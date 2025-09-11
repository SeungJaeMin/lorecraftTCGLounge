package com.lorecraft.tcglounge.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "fields")
@DiscriminatorValue("FIELD")
public class FieldCard extends Card {
    
    @Column(name = "field_effect", columnDefinition = "TEXT")
    private String fieldEffect;
    
    @Column(name = "affected_colors")
    private String affectedColors; // Comma-separated colors
    
    @Column(name = "affected_types")
    private String affectedTypes; // Comma-separated types
    
    @Column(name = "burst_value", nullable = false)
    private Integer burstValue; // 1~3
    
    // Constructors
    public FieldCard() {
        super();
    }
    
    public FieldCard(String cardName, CardColor cardColor, CardRarity rarity, Integer cost,
                     String fieldEffect, Integer burstValue) {
        super(cardName, cardColor, rarity, cost);
        this.fieldEffect = fieldEffect;
        this.burstValue = burstValue;
    }
    
    // Getters
    public String getFieldEffect() { return fieldEffect; }
    public String getAffectedColors() { return affectedColors; }
    public String getAffectedTypes() { return affectedTypes; }
    public Integer getBurstValue() { return burstValue; }
    
    // Setters
    public void setFieldEffect(String fieldEffect) { this.fieldEffect = fieldEffect; }
    public void setAffectedColors(String affectedColors) { this.affectedColors = affectedColors; }
    public void setAffectedTypes(String affectedTypes) { this.affectedTypes = affectedTypes; }
    public void setBurstValue(Integer burstValue) { this.burstValue = burstValue; }
}