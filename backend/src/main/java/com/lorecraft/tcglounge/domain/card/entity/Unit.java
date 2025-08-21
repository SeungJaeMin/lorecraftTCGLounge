package com.lorecraft.tcglounge.domain.card.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("UNIT")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Unit extends Card {

    @Column(name = "attack_power", nullable = false)
    private Integer attackPower;

    @Column(name = "defense_power", nullable = false)
    private Integer defensePower;

    @Column(name = "unit_type", length = 50)
    private String unitType;

    @Column(name = "tribe", length = 50)
    private String tribe;

    @Column(name = "special_ability", columnDefinition = "TEXT")
    private String specialAbility;

    @Column(name = "summon_condition", columnDefinition = "TEXT")
    private String summonCondition;

    @Column(name = "can_attack_leader", nullable = false)
    private Boolean canAttackLeader = true;

    @Column(name = "can_block", nullable = false)
    private Boolean canBlock = true;

    // 비즈니스 메서드
    public boolean isStrongAgainst(Unit other) {
        return this.attackPower > other.defensePower;
    }

    public boolean canDefeatInBattle(Unit other) {
        return this.attackPower >= other.defensePower;
    }

    public boolean hasSpecialAbility() {
        return specialAbility != null && !specialAbility.trim().isEmpty();
    }

    public boolean hasSummonCondition() {
        return summonCondition != null && !summonCondition.trim().isEmpty();
    }

    public boolean isSameTribe(Unit other) {
        return this.tribe != null && this.tribe.equals(other.tribe);
    }

    public int getBattlePower() {
        return attackPower + defensePower;
    }

    public boolean canParticipateInBattle() {
        return canAttackLeader || canBlock;
    }
}