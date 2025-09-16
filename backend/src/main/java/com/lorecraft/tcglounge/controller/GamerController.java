package com.lorecraft.tcglounge.controller;

import com.lorecraft.tcglounge.dto.GamerProfileDTO;
import com.lorecraft.tcglounge.entity.Gamer;
import com.lorecraft.tcglounge.entity.CardDeck;
import com.lorecraft.tcglounge.repository.GamerRepository;
import com.lorecraft.tcglounge.service.DeckService;
import com.lorecraft.tcglounge.security.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@RestController
@RequestMapping("/v1/gamer")
@CrossOrigin(origins = "http://localhost:3000")
public class GamerController {

    private static final Logger log = LoggerFactory.getLogger(GamerController.class);

    @Autowired
    private GamerRepository gamerRepository;
    
    @Autowired
    private DeckService deckService;

    @GetMapping("/profile")
    public ResponseEntity<?> getGamerProfile(@CurrentUser Gamer gamer) {
        try {
            GamerProfileDTO profileDTO = new GamerProfileDTO(gamer);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", profileDTO);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "프로필 조회 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> getGamerDashboard(@CurrentUser Gamer gamer) {
        try {
            // 대시보드 데이터 구성
            Map<String, Object> dashboardData = new HashMap<>();
            
            // 사용자 기본 정보
            GamerProfileDTO profile = new GamerProfileDTO(gamer);
            dashboardData.put("profile", profile);
            
            // 실제 덱 정보 조회
            List<CardDeck> userDecks = deckService.getGamerDecks(gamer.getUid());
            List<Map<String, Object>> decks = new ArrayList<>();
            
            for (CardDeck deck : userDecks) {
                Map<String, Object> deckData = new HashMap<>();
                deckData.put("id", deck.getDeckId());
                deckData.put("deckName", deck.getDeckName());
                deckData.put("description", deck.getDescription());
                deckData.put("totalCards", deck.getTotalCards());
                deckData.put("isComplete", deck.getTotalCards() >= 40);
                deckData.put("isPublic", deck.getIsPublic());
                deckData.put("isTournamentLegal", deck.getIsTournamentLegal());
                deckData.put("updatedAt", deck.getUpdatedAt());
                
                // Leader card info if exists (safely handle lazy loading)
                try {
                    if (deck.getLeaderCard() != null) {
                        Map<String, Object> leaderInfo = new HashMap<>();
                        leaderInfo.put("cardId", deck.getLeaderCard().getCardId());
                        leaderInfo.put("cardName", deck.getLeaderCard().getCardName());
                        leaderInfo.put("cardColor", deck.getLeaderCard().getCardColor().toString());
                        leaderInfo.put("cardType", deck.getLeaderCard().getCardType());
                        deckData.put("leaderCard", leaderInfo);
                    }
                } catch (Exception e) {
                    // Skip leader card info if lazy loading fails
                    log.warn("Failed to load leader card for deck {}: {}", deck.getDeckId(), e.getMessage());
                }
                
                decks.add(deckData);
            }
            dashboardData.put("myDecks", decks);
            
            // 최근 매치 (Mock 데이터)
            List<Map<String, Object>> recentMatches = Arrays.asList(
                createMockMatch(1L, "상대방1", "WIN", "메인 덱", "+25"),
                createMockMatch(2L, "상대방2", "LOSS", "실험 덱", "-18"),
                createMockMatch(3L, "상대방3", "WIN", "메인 덱", "+30"),
                createMockMatch(4L, "상대방4", "WIN", "대회용 덱", "+15"),
                createMockMatch(5L, "상대방5", "LOSS", "실험 덱", "-22")
            );
            dashboardData.put("recentMatches", recentMatches);
            
            // 예정된 대회 (Mock 데이터)
            List<Map<String, Object>> tournaments = Arrays.asList(
                createMockTournament(1L, "주간 챔피언십", "2024-08-25", "100만원", "128/256"),
                createMockTournament(2L, "초보자 리그", "2024-08-26", "30만원", "45/64"),
                createMockTournament(3L, "마스터즈 토너먼트", "2024-08-28", "500만원", "256/256")
            );
            dashboardData.put("upcomingTournaments", tournaments);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", dashboardData);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "대시보드 조회 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Mock 데이터 생성 헬퍼 메소드들
    private Map<String, Object> createMockDeck(Long id, String name, int wins, int games, String lastPlayed) {
        Map<String, Object> deck = new HashMap<>();
        deck.put("id", id);
        deck.put("name", name);
        deck.put("wins", wins);
        deck.put("games", games);
        deck.put("winRate", games > 0 ? Math.round((double) wins / games * 100) : 0);
        deck.put("lastPlayed", lastPlayed);
        return deck;
    }
    
    private Map<String, Object> createMockMatch(Long id, String opponent, String result, String deck, String ratingChange) {
        Map<String, Object> match = new HashMap<>();
        match.put("id", id);
        match.put("opponent", opponent);
        match.put("result", result);
        match.put("deck", deck);
        match.put("ratingChange", ratingChange);
        return match;
    }
    
    private Map<String, Object> createMockTournament(Long id, String name, String date, String prize, String participants) {
        Map<String, Object> tournament = new HashMap<>();
        tournament.put("id", id);
        tournament.put("name", name);
        tournament.put("date", date);
        tournament.put("prize", prize);
        tournament.put("participants", participants);
        return tournament;
    }
}