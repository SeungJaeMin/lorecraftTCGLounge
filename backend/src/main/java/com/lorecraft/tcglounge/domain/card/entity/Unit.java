package com.lorecraft.tcglounge.domain.card.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "unit")
@DiscriminatorValue("UNIT")
@PrimaryKeyJoinColumn(name = "card_id")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Unit extends Card {

    @Column(name = "power", nullable = false)
    private Integer power;

    @Column(name = "burst_value", nullable = false)
    private Integer burstValue;  // 1~3

    // 비즈니스 메서드 - ERD V0.3 간소화
    public boolean isValidBurstValue() {
        return burstValue >= 1 && burstValue <= 3;
    }
}