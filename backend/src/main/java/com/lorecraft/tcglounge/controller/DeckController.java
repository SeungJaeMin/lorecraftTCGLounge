package com.lorecraft.tcglounge.controller;

import com.lorecraft.tcglounge.dto.deck.*;
import com.lorecraft.tcglounge.entity.CardDeck;
import com.lorecraft.tcglounge.entity.DeckDetail;
import com.lorecraft.tcglounge.entity.User;
import com.lorecraft.tcglounge.service.DeckService;
import com.lorecraft.tcglounge.security.CurrentUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/v1/decks")
@CrossOrigin(origins = "http://localhost:3000")
public class DeckController {

    private final DeckService deckService;

    public DeckController(DeckService deckService) {
        this.deckService = deckService;
    }

    // 1. 내 덱 목록 조회 (간단한 정보)
    @GetMapping("/my-decks")
    public ResponseEntity<List<DeckSummaryDTO>> getMyDecks(@CurrentUser User user) {
        try {
            // 모든 사용자가 자신의 덱을 조회할 수 있음
            List<CardDeck> decks = deckService.getUserDecks(user.getUserid());
            List<DeckSummaryDTO> deckSummaries = decks.stream()
                .map(DeckSummaryDTO::from)
                .collect(Collectors.toList());

            return ResponseEntity.ok(deckSummaries);
        } catch (Exception e) {
            log.error("Error getting user decks", e);
            return ResponseEntity.badRequest().build();
        }
    }

    // 2. 특정 덱 조회 (전체 정보 + 카드 리스트)
    @GetMapping("/{deckId}")
    public ResponseEntity<Object> getDeck(
            @PathVariable Long deckId,
            @CurrentUser User user) {
        try {
            // 모든 사용자가 덱을 조회할 수 있음
            log.info("=== Getting deck {} for user {} ===", deckId, user.getUserid());

            CardDeck deck = deckService.getDeckById(deckId, user.getUserid())
                .orElseThrow(() -> new RuntimeException("Deck not found: " + deckId));

            List<DeckDetail> deckCards = deckService.getDeckCards(deckId, user.getUserid());
            log.info("Found {} cards for deck {}", deckCards.size(), deckId);

            DeckFullResponseDTO response = DeckFullResponseDTO.from(deck, deckCards);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("=== ERROR getting deck {} for user {} ===", deckId, user.getUserid());
            log.error("Error type: {}", e.getClass().getName());
            log.error("Error message: {}", e.getMessage());
            log.error("Stack trace:", e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("error", e.getClass().getSimpleName());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // 3. 덱 저장 (생성/수정 통합)
    // 새 덱 생성
    @PostMapping
    public ResponseEntity<Map<String, Object>> createDeck(
            @Valid @RequestBody DeckSaveRequestDTO request,
            @CurrentUser User user) {
        try {
            log.info("Creating new deck: {} for user: {}", request.getDeckName(), user.getUserid());
            request.setDeckId(null); // 새 덱 생성 보장
            CardDeck savedDeck = deckService.saveDeck(user.getUserid(), request);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("deck", DeckSummaryDTO.from(savedDeck));
            response.put("message", "덱이 생성되었습니다");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error creating deck", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 덱 수정
    @PutMapping("/{deckId}")
    public ResponseEntity<Map<String, Object>> updateDeck(
            @PathVariable Long deckId,
            @Valid @RequestBody DeckSaveRequestDTO request,
            @CurrentUser User user) {
        try {
            log.info("Updating deck: {} for user: {}", deckId, user.getUserid());
            request.setDeckId(deckId); // 덱 ID 설정
            CardDeck savedDeck = deckService.saveDeck(user.getUserid(), request);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("deck", DeckSummaryDTO.from(savedDeck));
            response.put("message", "덱이 수정되었습니다");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error saving deck", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 4. 덱 삭제
    @DeleteMapping("/{deckId}")
    public ResponseEntity<Map<String, Object>> deleteDeck(
            @PathVariable Long deckId,
            @CurrentUser User user) {
        try {
            deckService.deleteDeck(deckId, user.getUserid());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "덱이 삭제되었습니다");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error deleting deck: {}", deckId, e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 5. 덱 검색 (덱 코드로 조회)
    @GetMapping("/search/{deckCode}")
    public ResponseEntity<DeckFullResponseDTO> searchDeck(
            @PathVariable String deckCode,
            @CurrentUser User user) {
        try {
            log.info("Searching deck with code: {}", deckCode);

            CardDeck deck = deckService.getDeckByCode(deckCode)
                .orElseThrow(() -> new RuntimeException("덱을 찾을 수 없습니다"));

            List<DeckDetail> deckCards = deckService.getDeckCardsByDeckId(deck.getDeckId());

            DeckFullResponseDTO response = DeckFullResponseDTO.from(deck, deckCards);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error searching deck with code: {}", deckCode, e);
            return ResponseEntity.badRequest().build();
        }
    }

    // 추가: 랜덤 덱 생성 (기존 기능 유지)
    @PostMapping("/random")
    public ResponseEntity<Map<String, Object>> generateRandomDeck(
            @RequestBody Map<String, String> request,
            @CurrentUser User user) {
        try {
            String deckName = request.get("deckName");

            if (deckName == null || deckName.trim().isEmpty()) {
                deckName = "Random Deck " + System.currentTimeMillis();
            }

            CardDeck deck = deckService.generateRandomDeck(user.getUserid(), deckName.trim());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("deck", DeckSummaryDTO.from(deck));
            response.put("message", "랜덤 덱이 생성되었습니다");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error generating random deck", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}