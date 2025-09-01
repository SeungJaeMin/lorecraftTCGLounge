package com.lorecraft.tcglounge.domain.card.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "field")
@DiscriminatorValue("FIELD")
@PrimaryKeyJoinColumn(name = "card_id")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Field extends Card {

    @Column(name = "field_effect", columnDefinition = "TEXT")
    private String fieldEffect;

    @Column(name = "affected_colors", length = 100)
    private String affectedColors;

    @Column(name = "affected_types", length = 100)
    private String affectedTypes;

    @Column(name = "activation_timing", length = 50)
    private String activationTiming;

    @Column(name = "is_global", nullable = false)
    private Boolean isGlobal = false;

    @Column(name = "max_active_count")
    private Integer maxActiveCount = 1;

    @Column(name = "maintenance_cost")
    private Integer maintenanceCost;

    @Column(name = "burst_value", nullable = false)
    private Integer burstValue;  // 1~3

    // 비즈니스 메서드 - ERD V0.3 간소화
    public boolean hasFieldEffect() {
        return fieldEffect != null && !fieldEffect.trim().isEmpty();
    }

    public boolean affectsColor(Card.CardColor color) {
        return affectedColors != null && affectedColors.contains(color.name());
    }

    public boolean affectsCardType(String cardType) {
        return affectedTypes != null && affectedTypes.contains(cardType);
    }

    public boolean affectsAllPlayers() {
        return isGlobal;
    }

    public boolean hasMaintenanceCost() {
        return maintenanceCost != null && maintenanceCost > 0;
    }

    public boolean isStackable() {
        return maxActiveCount != null && maxActiveCount > 1;
    }

    public boolean isValidBurstValue() {
        return burstValue >= 1 && burstValue <= 3;
    }
}