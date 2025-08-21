package com.lorecraft.tcglounge.domain.card.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("FIELD")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Field extends Card {

    @Enumerated(EnumType.STRING)
    @Column(name = "field_type", nullable = false)
    private FieldType fieldType;

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

    // 비즈니스 메서드
    public boolean isEnvironment() {
        return fieldType == FieldType.ENVIRONMENT;
    }

    public boolean isTerrain() {
        return fieldType == FieldType.TERRAIN;
    }

    public boolean isWeather() {
        return fieldType == FieldType.WEATHER;
    }

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

    public boolean canCoexistWith(Field otherField) {
        return !this.fieldType.equals(otherField.fieldType) || maxActiveCount > 1;
    }

    public boolean isStackable() {
        return maxActiveCount != null && maxActiveCount > 1;
    }

    // Enum 정의
    public enum FieldType {
        ENVIRONMENT("환경"),
        TERRAIN("지형"),
        WEATHER("날씨"),
        DIMENSION("차원"),
        BARRIER("결계");

        private final String koreanName;

        FieldType(String koreanName) {
            this.koreanName = koreanName;
        }

        public String getKoreanName() {
            return koreanName;
        }
    }
}