package com.lorecraft.tcglounge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardDto {
    private Long id;
    private String name;
    private String type;
    private String rarity;
    private String color;
    private Integer cost;
    private Integer attack;
    private Integer defense;
    private String description;
    private String imageUrl;
    private String setCode;
    private String cardNumber;
}