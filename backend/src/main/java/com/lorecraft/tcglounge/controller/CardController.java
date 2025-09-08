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

    @PostMapping("/init-sample-data")
    public ResponseEntity<Map<String, Object>> initSampleData() {
        try {
            // 샘플 카드 데이터 생성
            cardService.createCard("화염 드래곤", Card.CardColor.RED, Card.CardRarity.LEGENDARY, 8);
            cardService.createCard("빙결의 마법사", Card.CardColor.BLUE, Card.CardRarity.RARE, 5);
            cardService.createCard("번개 폭풍", Card.CardColor.YELLOW, Card.CardRarity.COMMON, 3);
            cardService.createCard("치유의 성수", Card.CardColor.COLORLESS, Card.CardRarity.COMMON, 2);
            cardService.createCard("어둠의 검사", Card.CardColor.BLACK, Card.CardRarity.RARE, 4);
            cardService.createCard("자연의 수호자", Card.CardColor.GREEN, Card.CardRarity.SUPER_RARE, 6);
            cardService.createCard("신성한 기사", Card.CardColor.COLORLESS, Card.CardRarity.ULTRA_RARE, 7);
            cardService.createCard("고대의 정령", Card.CardColor.GREEN, Card.CardRarity.SECRET_RARE, 9);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Sample data initialized successfully");
            response.put("cardsCreated", 8);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }
}