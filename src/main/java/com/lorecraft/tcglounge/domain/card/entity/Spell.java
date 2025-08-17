package com.lorecraft.tcglounge.domain.card.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("SPELL")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Spell extends Card {

    @Enumerated(EnumType.STRING)
    @Column(name = "spell_type", nullable = false)
    private SpellType spellType;

    @Column(name = "spell_effect", columnDefinition = "TEXT")
    private String spellEffect;

    @Column(name = "target_type", length = 50)
    private String targetType;

    @Column(name = "cast_timing", length = 50)
    private String castTiming;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "is_instant", nullable = false)
    private Boolean isInstant = true;

    @Column(name = "is_counterable", nullable = false)
    private Boolean isCounterable = true;

    @Column(name = "magic_school", length = 50)
    private String magicSchool;

    @Column(name = "power_level")
    private Integer powerLevel;

    // 비즈니스 메서드
    public boolean isOffensive() {
        return spellType == SpellType.OFFENSIVE;
    }

    public boolean isDefensive() {
        return spellType == SpellType.DEFENSIVE;
    }

    public boolean isUtility() {
        return spellType == SpellType.UTILITY;
    }

    public boolean isHealing() {
        return spellType == SpellType.HEALING;
    }

    public boolean isBuff() {
        return spellType == SpellType.BUFF;
    }

    public boolean isDebuff() {
        return spellType == SpellType.DEBUFF;
    }

    public boolean hasSpellEffect() {
        return spellEffect != null && !spellEffect.trim().isEmpty();
    }

    public boolean hasTargetRestriction() {
        return targetType != null && !targetType.trim().isEmpty();
    }

    public boolean isPermanent() {
        return !isInstant && (duration == null || duration == -1);
    }

    public boolean hasTimedDuration() {
        return duration != null && duration > 0;
    }

    public boolean canBeCountered() {
        return isCounterable;
    }

    public boolean belongsToSchool(String school) {
        return magicSchool != null && magicSchool.equalsIgnoreCase(school);
    }

    public boolean isHighPowerSpell() {
        return powerLevel != null && powerLevel >= 7;
    }

    public boolean canCastAtTiming(String timing) {
        return castTiming == null || castTiming.contains(timing);
    }

    public String getSpellDescription() {
        StringBuilder desc = new StringBuilder();
        
        desc.append(spellType.getKoreanName());
        
        if (magicSchool != null) {
            desc.append(" (").append(magicSchool).append(")");
        }
        
        if (isInstant) {
            desc.append(" - 즉시");
        } else if (hasTimedDuration()) {
            desc.append(" - ").append(duration).append("턴");
        } else if (isPermanent()) {
            desc.append(" - 지속");
        }
        
        return desc.toString();
    }

    // Enum 정의
    public enum SpellType {
        OFFENSIVE("공격 마법"),
        DEFENSIVE("방어 마법"),
        UTILITY("유틸리티 마법"),
        HEALING("치유 마법"),
        BUFF("강화 마법"),
        DEBUFF("약화 마법"),
        SUMMONING("소환 마법"),
        CONTROL("제어 마법"),
        RITUAL("의식 마법");

        private final String koreanName;

        SpellType(String koreanName) {
            this.koreanName = koreanName;
        }

        public String getKoreanName() {
            return koreanName;
        }
    }
}