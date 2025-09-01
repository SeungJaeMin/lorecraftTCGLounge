package com.lorecraft.tcglounge.domain.card.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "leader")
@DiscriminatorValue("LEADER")
@PrimaryKeyJoinColumn(name = "card_id")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Leader extends Card {

    @Column(name = "life_points", nullable = false)
    private Integer lifePoints;

    @Column(name = "leader_skill", columnDefinition = "TEXT")
    private String leaderSkill;

    @Column(name = "awakening_condition", columnDefinition = "TEXT")
    private String awakeningCondition;

    @Column(name = "awakened_form_id")
    private Long awakenedFormId;

    @Column(name = "is_awakened", nullable = false)
    private Boolean isAwakened = false;

    @Column(name = "burst_slot1", nullable = false)
    private Integer burstSlot1;  // 1~3

    @Column(name = "burst_slot2", nullable = false)
    private Integer burstSlot2;  // 1~3

    @Column(name = "burst_slot3", nullable = false)
    private Integer burstSlot3;  // 1~3

    // 비즈니스 메서드
    public void awaken() {
        this.isAwakened = true;
    }

    public boolean canAwaken() {
        return !isAwakened && awakenedFormId != null;
    }

    public boolean hasLeaderSkill() {
        return leaderSkill != null && !leaderSkill.trim().isEmpty();
    }

    public boolean hasAwakeningCondition() {
        return awakeningCondition != null && !awakeningCondition.trim().isEmpty();
    }
}