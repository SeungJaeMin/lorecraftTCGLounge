package com.lorecraft.tcglounge.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "leaders")
@DiscriminatorValue("LEADER")
public class LeaderCard extends Card {
    
    @Column(name = "leader_skill", columnDefinition = "TEXT")
    private String leaderSkill;
    
    @Column(name = "is_awakened")
    private Boolean isAwakened = false;
    
    @Column(name = "burst_slot1", nullable = false)
    private Integer burstSlot1; // 1~3
    
    @Column(name = "burst_slot2", nullable = false)
    private Integer burstSlot2; // 1~3
    
    @Column(name = "burst_slot3", nullable = false)
    private Integer burstSlot3; // 1~3
    
    // Constructors
    public LeaderCard() {
        super();
    }
    
    public LeaderCard(String cardName, CardColor cardColor, CardRarity rarity, Integer cost,
                      Integer burstSlot1, Integer burstSlot2, Integer burstSlot3) {
        super(cardName, cardColor, rarity, cost);
        this.burstSlot1 = burstSlot1;
        this.burstSlot2 = burstSlot2;
        this.burstSlot3 = burstSlot3;
    }
    
    // Getters
    public String getLeaderSkill() { return leaderSkill; }
    public Boolean getIsAwakened() { return isAwakened; }
    public Integer getBurstSlot1() { return burstSlot1; }
    public Integer getBurstSlot2() { return burstSlot2; }
    public Integer getBurstSlot3() { return burstSlot3; }
    
    // Setters
    public void setLeaderSkill(String leaderSkill) { this.leaderSkill = leaderSkill; }
    public void setIsAwakened(Boolean isAwakened) { this.isAwakened = isAwakened; }
    public void setBurstSlot1(Integer burstSlot1) { this.burstSlot1 = burstSlot1; }
    public void setBurstSlot2(Integer burstSlot2) { this.burstSlot2 = burstSlot2; }
    public void setBurstSlot3(Integer burstSlot3) { this.burstSlot3 = burstSlot3; }
}