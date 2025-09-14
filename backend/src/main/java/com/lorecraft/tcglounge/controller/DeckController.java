package com.lorecraft.tcglounge.controller;

import com.lorecraft.tcglounge.entity.CardDeck;
import com.lorecraft.tcglounge.entity.DeckDetail;
import com.lorecraft.tcglounge.service.DeckService;
import com.lorecraft.tcglounge.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/decks")
@CrossOrigin(origins = "http://localhost:3000")
public class DeckController {
    
    private static final Logger log = LoggerFactory.getLogger(DeckController.class);
    
    private final DeckService deckService;
    private final AuthService authService;
    
    public DeckController(DeckService deckService, AuthService authService) {
        this.deckService = deckService;
        this.authService = authService;
    }
    
    @GetMapping("/my")
    public ResponseEntity<Map<String, Object>> getMyDecks(@RequestHeader("Authorization") String authHeader) {
        try {
            Long gamerId = authService.getGamerIdFromToken(authHeader);
            List<CardDeck> decks = deckService.getGamerDecks(gamerId);
            
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
            @RequestHeader("Authorization") String authHeader) {
        try {
            Long gamerId = authService.getGamerIdFromToken(authHeader);
            CardDeck deck = deckService.getDeckById(deckId, gamerId)
                .orElseThrow(() -> new RuntimeException("Deck not found"));
            
            List<DeckDetail> deckCards = deckService.getDeckCards(deckId, gamerId);
            Map<String, Object> deckStats = deckService.getDeckStats(deckId, gamerId);
            
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
            @RequestHeader("Authorization") String authHeader) {
        try {
            Long gamerId = authService.getGamerIdFromToken(authHeader);
            String deckName = request.get("deckName");
            String description = request.get("description");
            
            if (deckName == null || deckName.trim().isEmpty()) {
                throw new RuntimeException("Deck name is required");
            }
            
            CardDeck deck = deckService.createDeck(gamerId, deckName.trim(), description);
            
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
            @RequestHeader("Authorization") String authHeader) {
        try {
            Long gamerId = authService.getGamerIdFromToken(authHeader);
            String deckName = (String) request.get("deckName");
            String description = (String) request.get("description");
            Boolean isPublic = (Boolean) request.get("isPublic");
            
            CardDeck deck = deckService.updateDeck(deckId, gamerId, deckName, description, isPublic);
            
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
    
    @DeleteMapping("/{deckId}")
    public ResponseEntity<Map<String, Object>> deleteDeck(
            @PathVariable Long deckId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            Long gamerId = authService.getGamerIdFromToken(authHeader);
            deckService.deleteDeck(deckId, gamerId);
            
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
            @RequestHeader("Authorization") String authHeader) {
        try {
            Long gamerId = authService.getGamerIdFromToken(authHeader);
            Long cardId = Long.valueOf(request.get("cardId").toString());
            Integer quantity = Integer.valueOf(request.get("quantity").toString());
            
            DeckDetail deckDetail = deckService.addCardToDeck(deckId, gamerId, cardId, quantity);
            
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
    
    @DeleteMapping("/{deckId}/cards")
    public ResponseEntity<Map<String, Object>> removeCardFromDeck(
            @PathVariable Long deckId,
            @RequestBody Map<String, Object> request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            Long gamerId = authService.getGamerIdFromToken(authHeader);
            Long cardId = Long.valueOf(request.get("cardId").toString());
            Integer quantity = Integer.valueOf(request.get("quantity").toString());
            
            deckService.removeCardFromDeck(deckId, gamerId, cardId, quantity);
            
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
            @RequestHeader("Authorization") String authHeader) {
        try {
            Long gamerId = authService.getGamerIdFromToken(authHeader);
            String deckName = request.get("deckName");
            
            if (deckName == null || deckName.trim().isEmpty()) {
                deckName = "Random Deck " + System.currentTimeMillis();
            }
            
            CardDeck deck = deckService.generateRandomDeck(gamerId, deckName.trim());
            
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
            @RequestHeader("Authorization") String authHeader) {
        try {
            Long gamerId = authService.getGamerIdFromToken(authHeader);
            Map<String, Object> stats = deckService.getDeckStats(deckId, gamerId);
            
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