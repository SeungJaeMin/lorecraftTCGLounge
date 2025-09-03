package com.lorecraft.tcglounge.controller;

import com.lorecraft.tcglounge.domain.card.entity.Card;
import com.lorecraft.tcglounge.domain.card.service.CardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cards")
@CrossOrigin(origins = "http://localhost:3000")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping
    public ResponseEntity<List<Card>> getAllCards() {
        List<Card> cards = cardService.findAll();
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Card> getCard(@PathVariable Long id) {
        return cardService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Card>> searchCards(@RequestParam String name) {
        List<Card> cards = cardService.searchByName(name);
        return ResponseEntity.ok(cards);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createCard(@RequestBody Map<String, Object> request) {
        try {
            String cardName = (String) request.get("cardName");
            String colorStr = (String) request.get("cardColor");
            String rarityStr = (String) request.get("rarity");
            Integer cost = (Integer) request.get("cost");

            Card.CardColor cardColor = Card.CardColor.valueOf(colorStr);
            Card.CardRarity rarity = Card.CardRarity.valueOf(rarityStr);

            Card card = cardService.createCard(cardName, cardColor, rarity, cost);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Card created successfully");
            response.put("cardId", card.getCardId());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }
}