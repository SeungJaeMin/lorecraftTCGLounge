package com.lorecraft.tcglounge.controller;

import com.lorecraft.tcglounge.dto.CardDto;
import com.lorecraft.tcglounge.dto.CardSearchRequest;
import com.lorecraft.tcglounge.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// @RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class CardController {

    private final CardService cardService;

    @GetMapping("/search")
    public ResponseEntity<Page<CardDto>> searchCards(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String rarity,
            @RequestParam(required = false) String color,
            Pageable pageable) {
        
        CardSearchRequest request = CardSearchRequest.builder()
                .query(query)
                .type(type)
                .rarity(rarity)
                .color(color)
                .build();
        
        return ResponseEntity.ok(cardService.searchCards(request, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardDto> getCard(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.getCard(id));
    }

    @GetMapping("/featured")
    public ResponseEntity<List<CardDto>> getFeaturedCards() {
        return ResponseEntity.ok(cardService.getFeaturedCards());
    }
}