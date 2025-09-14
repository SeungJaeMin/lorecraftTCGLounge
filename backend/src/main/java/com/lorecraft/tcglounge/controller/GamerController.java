package com.lorecraft.tcglounge.controller;

import com.lorecraft.tcglounge.dto.GamerProfileDTO;
import com.lorecraft.tcglounge.entity.Gamer;
import com.lorecraft.tcglounge.repository.GamerRepository;
import com.lorecraft.tcglounge.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/v1/gamer")
@CrossOrigin(origins = "http://localhost:3000")
public class GamerController {

    @Autowired
    private GamerRepository gamerRepository;

    @Autowired
    private AuthService authService;

    @GetMapping("/profile")
    public ResponseEntity<?> getGamerProfile(@RequestHeader("Authorization") String token) {
        try {
            // JWT에서 Gamer ID 추출 - AuthService 사용
            Long gamerId = authService.getGamerIdFromToken(token);
            
            // 게이머 정보 조회 - UID로 직접 조회
            Optional<Gamer> gamerOpt = gamerRepository.findById(gamerId);
            
            if (!gamerOpt.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "게이머 정보를 찾을 수 없습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            Gamer gamer = gamerOpt.get();
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
    public ResponseEntity<?> getGamerDashboard(@RequestHeader("Authorization") String token) {
        try {
            // JWT에서 Gamer ID 추출 - AuthService 사용
            Long gamerId = authService.getGamerIdFromToken(token);
            
            // 게이머 정보 조회 - UID로 직접 조회
            Optional<Gamer> gamerOpt = gamerRepository.findById(gamerId);
            
            if (!gamerOpt.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "게이머 정보를 찾을 수 없습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            Gamer gamer = gamerOpt.get();
            
            // 대시보드 데이터 구성
            Map<String, Object> dashboardData = new HashMap<>();
            
            // 사용자 기본 정보
            GamerProfileDTO profile = new GamerProfileDTO(gamer);
            dashboardData.put("profile", profile);
            
            // TODO: 실제 데이터로 교체 필요
            // 덱 정보 (Mock 데이터)
            List<Map<String, Object>> decks = Arrays.asList(
                createMockDeck(1L, "메인 덱", 15, 25, "2시간 전"),
                createMockDeck(2L, "실험 덱", 8, 12, "1일 전"),
                createMockDeck(3L, "대회용 덱", 22, 30, "3일 전")
            );
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