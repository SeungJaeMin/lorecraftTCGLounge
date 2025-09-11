package com.lorecraft.tcglounge.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "units")
@DiscriminatorValue("UNIT")
public class UnitCard extends Card {
    
    @Column(name = "power", nullable = false)
    private Integer power;
    
    @Column(name = "burst_value", nullable = false)
    private Integer burstValue; // 1~3
    
    // Constructors
    public UnitCard() {
        super();
    }
    
    public UnitCard(String cardName, CardColor cardColor, CardRarity rarity, Integer cost,
                    Integer power, Integer burstValue) {
        super(cardName, cardColor, rarity, cost);
        this.power = power;
        this.burstValue = burstValue;
    }
    
    // Getters
    public Integer getPower() { return power; }
    public Integer getBurstValue() { return burstValue; }
    
    // Setters
    public void setPower(Integer power) { this.power = power; }
    public void setBurstValue(Integer burstValue) { this.burstValue = burstValue; }
}