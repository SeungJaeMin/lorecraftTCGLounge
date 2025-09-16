package com.lorecraft.tcglounge.controller;

import com.lorecraft.tcglounge.entity.CardDeck;
import com.lorecraft.tcglounge.entity.DeckDetail;
import com.lorecraft.tcglounge.entity.User;
import com.lorecraft.tcglounge.entity.Gamer;
import com.lorecraft.tcglounge.service.DeckService;
import com.lorecraft.tcglounge.security.CurrentUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/v1/decks")
@CrossOrigin(origins = "http://localhost:3000")
public class DeckController {
    
    private static final Logger log = LoggerFactory.getLogger(DeckController.class);
    
    private final DeckService deckService;
    
    public DeckController(DeckService deckService) {
        this.deckService = deckService;
    }
    
    @GetMapping("/my-decks")
    public ResponseEntity<Map<String, Object>> getMyDecks(@CurrentUser Gamer gamer) {
        try {
            List<CardDeck> decks = deckService.getGamerDecks(gamer.getUid());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("decks", decks);
            response.put("totalCount", decks.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting user decks", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/{deckId}")
    public ResponseEntity<Map<String, Object>> getDeck(
            @PathVariable Long deckId, 
            @CurrentUser Gamer gamer) {
        try {
            CardDeck deck = deckService.getDeckById(deckId, gamer.getUid())
                .orElseThrow(() -> new RuntimeException("Deck not found"));
            
            List<DeckDetail> deckCards = deckService.getDeckCards(deckId, gamer.getUid());
            Map<String, Object> deckStats = deckService.getDeckStats(deckId, gamer.getUid());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("deck", deck);
            response.put("cards", deckCards);
            response.put("stats", deckStats);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting deck: " + deckId, e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> createDeck(
            @RequestBody Map<String, String> request,
            @CurrentUser Gamer gamer) {
        try {
            String deckName = request.get("deckName");
            String description = request.get("description");
            
            if (deckName == null || deckName.trim().isEmpty()) {
                throw new RuntimeException("Deck name is required");
            }
            
            CardDeck deck = deckService.createDeck(gamer.getUid(), deckName.trim(), description);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("deck", deck);
            response.put("message", "Deck created successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error creating deck", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PutMapping("/{deckId}")
    public ResponseEntity<Map<String, Object>> updateDeck(
            @PathVariable Long deckId,
            @RequestBody Map<String, Object> request,
            @CurrentUser Gamer gamer) {
        try {
            String deckName = (String) request.get("deckName");
            String description = (String) request.get("description");
            Boolean isPublic = (Boolean) request.get("isPublic");
            
            CardDeck deck = deckService.updateDeck(deckId, gamer.getUid(), deckName, description, isPublic);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("deck", deck);
            response.put("message", "Deck updated successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating deck: " + deckId, e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveOrUpdateDeck(
            @RequestBody Map<String, Object> request,
            @CurrentUser Gamer gamer) {
        try {
            Long deckId = request.get("deckId") != null ? Long.valueOf(request.get("deckId").toString()) : null;
            String deckName = (String) request.get("deckName");
            String description = (String) request.get("description");
            Boolean isPublic = request.get("isPublic") != null ? Boolean.valueOf(request.get("isPublic").toString()) : false;
            
            CardDeck savedDeck = deckService.saveOrUpdateDeck(deckId, gamer.getUid(), deckName, description, isPublic);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("deck", savedDeck);
            response.put("message", deckId == null ? "Deck created successfully" : "Deck updated successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error saving deck", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/save-with-cards")
    public ResponseEntity<Map<String, Object>> saveDeckWithCards(
            @RequestBody Map<String, Object> request,
            @CurrentUser Gamer gamer) {
        try {
            Long deckId = request.get("deckId") != null ? Long.valueOf(request.get("deckId").toString()) : null;
            String deckName = (String) request.get("deckName");
            String description = (String) request.get("description");
            Boolean isPublic = request.get("isPublic") != null ? Boolean.valueOf(request.get("isPublic").toString()) : false;
            
            // 덱과 카드 목록 저장
            CardDeck savedDeck = deckService.saveOrUpdateDeck(deckId, gamer.getUid(), deckName, description, isPublic);
            
            // 카드 목록이 포함된 경우 처리
            List<?> cardsList = (List<?>) request.get("cards");
            if (cardsList != null && !cardsList.isEmpty()) {
                deckService.updateDeckCards(savedDeck.getDeckId(), gamer.getUid(), cardsList);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("deck", savedDeck);
            response.put("message", deckId == null ? "Deck and cards created successfully" : "Deck and cards updated successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error saving deck with cards", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @DeleteMapping("/{deckId}")
    public ResponseEntity<Map<String, Object>> deleteDeck(
            @PathVariable Long deckId,
            @CurrentUser Gamer gamer) {
        try {
            deckService.deleteDeck(deckId, gamer.getUid());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Deck deleted successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error deleting deck: " + deckId, e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/{deckId}/cards")
    public ResponseEntity<Map<String, Object>> addCardToDeck(
            @PathVariable Long deckId,
            @RequestBody Map<String, Object> request,
            @CurrentUser Gamer gamer) {
        try {
            Long cardId = Long.valueOf(request.get("cardId").toString());
            Integer quantity = Integer.valueOf(request.get("quantity").toString());
            
            DeckDetail deckDetail = deckService.addCardToDeck(deckId, gamer.getUid(), cardId, quantity);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("deckDetail", deckDetail);
            response.put("message", "Card added to deck successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error adding card to deck: " + deckId, e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @DeleteMapping("/{deckId}/cards/{cardId}")
    public ResponseEntity<Map<String, Object>> removeCardFromDeck(
            @PathVariable Long deckId,
            @PathVariable Long cardId,
            @RequestBody Map<String, Object> request,
            @CurrentUser Gamer gamer) {
        try {
            Integer quantity = Integer.valueOf(request.get("quantity").toString());
            
            deckService.removeCardFromDeck(deckId, gamer.getUid(), cardId, quantity);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Card removed from deck successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error removing card from deck: " + deckId, e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/random")
    public ResponseEntity<Map<String, Object>> generateRandomDeck(
            @RequestBody Map<String, String> request,
            @CurrentUser Gamer gamer) {
        try {
            String deckName = request.get("deckName");
            
            if (deckName == null || deckName.trim().isEmpty()) {
                deckName = "Random Deck " + System.currentTimeMillis();
            }
            
            CardDeck deck = deckService.generateRandomDeck(gamer.getUid(), deckName.trim());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("deck", deck);
            response.put("message", "Random deck generated successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error generating random deck", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/{deckId}/stats")
    public ResponseEntity<Map<String, Object>> getDeckStats(
            @PathVariable Long deckId,
            @CurrentUser Gamer gamer) {
        try {
            Map<String, Object> stats = deckService.getDeckStats(deckId, gamer.getUid());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("stats", stats);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting deck stats: " + deckId, e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}