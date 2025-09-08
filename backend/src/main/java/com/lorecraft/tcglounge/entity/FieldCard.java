package com.lorecraft.tcglounge.entity;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("FIELD")
public class FieldCard extends Card {
    
    @Column(name = "field_effect", columnDefinition = "TEXT")
    private String fieldEffect;
    
    @Column(name = "affected_colors")
    private String affectedColors; // Comma-separated colors
    
    @Column(name = "affected_types")
    private String affectedTypes; // Comma-separated types
    
    @Column(name = "activation_timing")
    private String activationTiming;
    
    @Column(name = "is_global")
    private Boolean isGlobal = false;
    
    @Column(name = "max_active_count")
    private Integer maxActiveCount = 1;
    
    @Column(name = "maintenance_cost")
    private Integer maintenanceCost;
    
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
    public String getActivationTiming() { return activationTiming; }
    public Boolean getIsGlobal() { return isGlobal; }
    public Integer getMaxActiveCount() { return maxActiveCount; }
    public Integer getMaintenanceCost() { return maintenanceCost; }
    public Integer getBurstValue() { return burstValue; }
    
    // Setters
    public void setFieldEffect(String fieldEffect) { this.fieldEffect = fieldEffect; }
    public void setAffectedColors(String affectedColors) { this.affectedColors = affectedColors; }
    public void setAffectedTypes(String affectedTypes) { this.affectedTypes = affectedTypes; }
    public void setActivationTiming(String activationTiming) { this.activationTiming = activationTiming; }
    public void setIsGlobal(Boolean isGlobal) { this.isGlobal = isGlobal; }
    public void setMaxActiveCount(Integer maxActiveCount) { this.maxActiveCount = maxActiveCount; }
    public void setMaintenanceCost(Integer maintenanceCost) { this.maintenanceCost = maintenanceCost; }
    public void setBurstValue(Integer burstValue) { this.burstValue = burstValue; }
}