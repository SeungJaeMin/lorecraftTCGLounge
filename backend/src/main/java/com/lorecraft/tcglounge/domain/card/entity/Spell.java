package com.lorecraft.tcglounge.domain.card.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "spell")
@DiscriminatorValue("SPELL")
@PrimaryKeyJoinColumn(name = "card_id")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Spell extends Card {

    @Column(name = "spell_effect", columnDefinition = "TEXT")
    private String spellEffect;

    @Column(name = "target_type", length = 50)
    private String targetType;

    @Column(name = "burst_value", nullable = false)
    private Integer burstValue;  // 1~3

    // 비즈니스 메서드 - ERD V0.3 간소화
    public boolean hasSpellEffect() {
        return spellEffect != null && !spellEffect.trim().isEmpty();
    }

    public boolean hasTargetRestriction() {
        return targetType != null && !targetType.trim().isEmpty();
    }

    public boolean isValidBurstValue() {
        return burstValue >= 1 && burstValue <= 3;
    }
}