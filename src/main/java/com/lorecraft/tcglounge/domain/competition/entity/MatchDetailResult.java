package com.lorecraft.tcglounge.domain.competition.entity;

import com.lorecraft.tcglounge.domain.card.entity.CardDeck;
import com.lorecraft.tcglounge.domain.user.entity.MatchRecordList;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "match_detail_results")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MatchDetailResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gamer_record_id", nullable = false)
    private MatchRecordList gamerRecord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "used_deck_id")
    private CardDeck usedDeck;

    @Enumerated(EnumType.STRING)
    @Column(name = "individual_result", nullable = false)
    private MatchResult individualResult;

    @Column(name = "games_won", nullable = false)
    private Integer gamesWon = 0;

    @Column(name = "games_lost", nullable = false)
    private Integer gamesLost = 0;

    @Column(name = "rating_change")
    private Integer ratingChange = 0;

    @Column(name = "performance_score")
    private Double performanceScore;

    @Column(name = "match_notes", length = 500)
    private String matchNotes;

    @CreatedDate
    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    // 비즈니스 메서드
    public boolean isWin() {
        return individualResult == MatchResult.PLAYER1_WIN || individualResult == MatchResult.PLAYER2_WIN;
    }

    public boolean isLoss() {
        MatchResult oppositeResult = individualResult.getOppositeResult();
        return oppositeResult == MatchResult.PLAYER1_WIN || oppositeResult == MatchResult.PLAYER2_WIN;
    }

    public boolean isDraw() {
        return individualResult == MatchResult.DRAW;
    }

    public void updateGameScore(int won, int lost) {
        this.gamesWon = won;
        this.gamesLost = lost;
    }

    public void applyRatingChange(int change) {
        this.ratingChange = change;
        if (gamerRecord != null) {
            gamerRecord.updateRatingChange(change);
        }
    }

    public void setPerformanceScore(double score) {
        this.performanceScore = score;
    }

    public void addMatchNote(String note) {
        if (this.matchNotes == null || this.matchNotes.trim().isEmpty()) {
            this.matchNotes = note;
        } else {
            this.matchNotes += "; " + note;
        }
    }

    public int getTotalGames() {
        return gamesWon + gamesLost;
    }

    public double getWinRateInMatch() {
        int totalGames = getTotalGames();
        if (totalGames == 0) return 0.0;
        return (double) gamesWon / totalGames * 100;
    }

    public boolean usedDeck(CardDeck deck) {
        return usedDeck != null && usedDeck.equals(deck);
    }

    public void setUsedDeck(CardDeck deck) {
        this.usedDeck = deck;
    }

    public boolean hasRatingChange() {
        return ratingChange != null && ratingChange != 0;
    }

    public boolean hasPerformanceScore() {
        return performanceScore != null;
    }

    public boolean hasMatchNotes() {
        return matchNotes != null && !matchNotes.trim().isEmpty();
    }

    public LocalDateTime getMatchDate() {
        return match.getMatchDate();
    }

    public String getOpponentName() {
        Enrollment currentPlayerEnrollment = gamerRecord.getGamer().getEnrollments().stream()
            .filter(e -> e.getCompetition().equals(match.getCompetition()))
            .findFirst()
            .orElse(null);
        
        if (currentPlayerEnrollment != null) {
            Enrollment opponent = match.getOpponent(currentPlayerEnrollment);
            return opponent != null ? opponent.getGamer().getId() : "Unknown";
        }
        return "Unknown";
    }
}