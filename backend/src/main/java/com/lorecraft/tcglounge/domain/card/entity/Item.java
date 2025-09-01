package com.lorecraft.tcglounge.domain.card.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "item")
@DiscriminatorValue("ITEM")
@PrimaryKeyJoinColumn(name = "card_id")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Item extends Card {

    @Column(name = "effect", columnDefinition = "TEXT")
    private String effect;

    @Column(name = "activation_condition", columnDefinition = "TEXT")
    private String activationCondition;

    @Column(name = "is_consumable", nullable = false)
    private Boolean isConsumable = true;

    @Column(name = "stack_limit")
    private Integer stackLimit;

    @Column(name = "burst_value", nullable = false)
    private Integer burstValue;  // 1~3

    // 비즈니스 메서드 - ERD V0.3 간소화
    public boolean hasEffect() {
        return effect != null && !effect.trim().isEmpty();
    }

    public boolean hasActivationCondition() {
        return activationCondition != null && !activationCondition.trim().isEmpty();
    }

    public boolean canStack() {
        return stackLimit != null && stackLimit > 1;
    }

    public boolean isValidBurstValue() {
        return burstValue >= 1 && burstValue <= 3;
    }
}