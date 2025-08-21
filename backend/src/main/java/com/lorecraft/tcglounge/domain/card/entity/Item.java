package com.lorecraft.tcglounge.domain.card.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("ITEM")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Item extends Card {

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false)
    private ItemType itemType;

    @Column(name = "effect", columnDefinition = "TEXT")
    private String effect;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "target_type", length = 50)
    private String targetType;

    @Column(name = "activation_condition", columnDefinition = "TEXT")
    private String activationCondition;

    @Column(name = "is_consumable", nullable = false)
    private Boolean isConsumable = true;

    @Column(name = "stack_limit")
    private Integer stackLimit;

    // 비즈니스 메서드
    public boolean isEquipment() {
        return itemType == ItemType.EQUIPMENT;
    }

    public boolean isSpell() {
        return itemType == ItemType.SPELL;
    }

    public boolean isTrap() {
        return itemType == ItemType.TRAP;
    }

    public boolean hasEffect() {
        return effect != null && !effect.trim().isEmpty();
    }

    public boolean hasActivationCondition() {
        return activationCondition != null && !activationCondition.trim().isEmpty();
    }

    public boolean isPermanent() {
        return !isConsumable || duration == null || duration == -1;
    }

    public boolean canStack() {
        return stackLimit != null && stackLimit > 1;
    }

    public boolean hasTargetRestriction() {
        return targetType != null && !targetType.trim().isEmpty();
    }

    // Enum 정의
    public enum ItemType {
        EQUIPMENT("장비"),
        SPELL("마법"),
        TRAP("함정"),
        ARTIFACT("아티팩트"),
        CONSUMABLE("소모품");

        private final String koreanName;

        ItemType(String koreanName) {
            this.koreanName = koreanName;
        }

        public String getKoreanName() {
            return koreanName;
        }
    }
}