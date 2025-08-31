package com.lorecraft.tcglounge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardSearchRequest {
    private String query;
    private String type;
    private String rarity;
    private String color;
}