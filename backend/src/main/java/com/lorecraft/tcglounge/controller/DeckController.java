package com.lorecraft.tcglounge.controller;

import com.lorecraft.tcglounge.dto.deck.*;
import com.lorecraft.tcglounge.entity.CardDeck;
import com.lorecraft.tcglounge.entity.DeckDetail;
import com.lorecraft.tcglounge.entity.Gamer;
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
    public ResponseEntity<List<DeckSummaryDTO>> getMyDecks(@CurrentUser Gamer gamer) {
        try {
            List<CardDeck> decks = deckService.getGamerDecks(gamer.getUid());
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
    public ResponseEntity<DeckFullResponseDTO> getDeck(
            @PathVariable Long deckId,
            @CurrentUser Gamer gamer) {
        try {
            log.info("=== Getting deck {} for gamer {} ===", deckId, gamer.getUid());

            CardDeck deck = deckService.getDeckById(deckId, gamer.getUid())
                .orElseThrow(() -> new RuntimeException("Deck not found"));

            List<DeckDetail> deckCards = deckService.getDeckCards(deckId, gamer.getUid());

            DeckFullResponseDTO response = DeckFullResponseDTO.from(deck, deckCards);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("=== ERROR getting deck {} ===", deckId);
            log.error("Error message: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    // 3. 덱 저장 (생성/수정 통합)
    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveDeck(
            @Valid @RequestBody DeckSaveRequestDTO request,
            @CurrentUser Gamer gamer) {
        try {
            log.info("Saving deck: {} for gamer: {}", request.getDeckName(), gamer.getUid());

            CardDeck savedDeck = deckService.saveFullDeck(gamer.getUid(), request);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("deck", DeckSummaryDTO.from(savedDeck));
            response.put("message", request.getDeckId() == null ? "덱이 생성되었습니다" : "덱이 수정되었습니다");

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
            @CurrentUser Gamer gamer) {
        try {
            deckService.deleteDeck(deckId, gamer.getUid());

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
            @CurrentUser Gamer gamer) {
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
            @CurrentUser Gamer gamer) {
        try {
            String deckName = request.get("deckName");

            if (deckName == null || deckName.trim().isEmpty()) {
                deckName = "Random Deck " + System.currentTimeMillis();
            }

            CardDeck deck = deckService.generateRandomDeck(gamer.getUid(), deckName.trim());

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