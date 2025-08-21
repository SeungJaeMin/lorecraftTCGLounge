package com.lorecraft.tcglounge.domain.competition.entity;

import com.lorecraft.tcglounge.domain.card.entity.CardDeck;
import com.lorecraft.tcglounge.domain.user.entity.Gamer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "enrollments",
    uniqueConstraints = @UniqueConstraint(columnNames = {"competition_id", "gamer_id"}))
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id", nullable = false)
    private Competition competition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gamer_id", nullable = false)
    private Gamer gamer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_deck_id")
    private CardDeck selectedDeck;

    @Enumerated(EnumType.STRING)
    @Column(name = "check_in_status", nullable = false)
    private CheckInStatus checkInStatus = CheckInStatus.NOT_CHECKED;

    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "seed_number")
    private Integer seedNumber;

    @Column(name = "final_rank")
    private Integer finalRank;

    @Column(name = "prize_awarded")
    private String prizeAwarded;

    @Column(name = "is_disqualified", nullable = false)
    private Boolean isDisqualified = false;

    @Column(name = "disqualification_reason", length = 255)
    private String disqualificationReason;

    @CreatedDate
    @Column(name = "enrolled_at", nullable = false)
    private LocalDateTime enrolledAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 연관관계
    @OneToMany(mappedBy = "enrollment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EnrollmentDeck> enrollmentDecks = new ArrayList<>();

    @OneToMany(mappedBy = "player1Enrollment", fetch = FetchType.LAZY)
    private List<Match> matchesAsPlayer1 = new ArrayList<>();

    @OneToMany(mappedBy = "player2Enrollment", fetch = FetchType.LAZY)
    private List<Match> matchesAsPlayer2 = new ArrayList<>();

    // 비즈니스 메서드
    public void checkIn() {
        if (canCheckIn()) {
            this.checkInStatus = CheckInStatus.CHECKED_IN;
            this.checkInTime = LocalDateTime.now();
        }
    }

    public void markAbsent() {
        this.checkInStatus = CheckInStatus.ABSENT;
        this.checkInTime = null;
    }

    public boolean canCheckIn() {
        return checkInStatus == CheckInStatus.NOT_CHECKED && 
               !isDisqualified &&
               competition.canEnroll();
    }

    public boolean isCheckedIn() {
        return checkInStatus == CheckInStatus.CHECKED_IN;
    }

    public boolean isAbsent() {
        return checkInStatus == CheckInStatus.ABSENT;
    }

    public boolean canParticipateInMatches() {
        return isCheckedIn() && !isDisqualified;
    }

    public void disqualify(String reason) {
        this.isDisqualified = true;
        this.disqualificationReason = reason;
    }

    public void reinstate() {
        this.isDisqualified = false;
        this.disqualificationReason = null;
    }

    public void setFinalRank(Integer rank) {
        this.finalRank = rank;
    }

    public void awardPrize(String prize) {
        this.prizeAwarded = prize;
    }

    public void setSeedNumber(Integer seed) {
        this.seedNumber = seed;
    }

    public void selectDeck(CardDeck deck) {
        if (deck.getGamer().equals(this.gamer) && deck.canBeUsedInCompetition()) {
            this.selectedDeck = deck;
        }
    }

    public void addDeck(CardDeck deck) {
        if (deck.getGamer().equals(this.gamer) && deck.canBeUsedInCompetition()) {
            EnrollmentDeck enrollmentDeck = EnrollmentDeck.builder()
                .enrollment(this)
                .deck(deck)
                .deckOrder(enrollmentDecks.size() + 1)
                .build();
            enrollmentDecks.add(enrollmentDeck);
        }
    }

    public void removeDeck(CardDeck deck) {
        enrollmentDecks.removeIf(ed -> ed.getDeck().equals(deck));
        // 순서 재정렬
        for (int i = 0; i < enrollmentDecks.size(); i++) {
            enrollmentDecks.get(i).setDeckOrder(i + 1);
        }
    }

    public boolean hasRequiredDecks() {
        return enrollmentDecks.size() >= competition.getDeckLimit();
    }

    public List<Match> getAllMatches() {
        List<Match> allMatches = new ArrayList<>();
        allMatches.addAll(matchesAsPlayer1);
        allMatches.addAll(matchesAsPlayer2);
        return allMatches;
    }

    public int getWinCount() {
        return (int) getAllMatches().stream()
            .filter(match -> match.getWinner() != null)
            .filter(match -> 
                (match.getPlayer1Enrollment().equals(this) && match.getResult() == MatchResult.PLAYER1_WIN) ||
                (match.getPlayer2Enrollment().equals(this) && match.getResult() == MatchResult.PLAYER2_WIN))
            .count();
    }

    public int getLossCount() {
        return (int) getAllMatches().stream()
            .filter(match -> match.getWinner() != null)
            .filter(match -> 
                (match.getPlayer1Enrollment().equals(this) && match.getResult() == MatchResult.PLAYER2_WIN) ||
                (match.getPlayer2Enrollment().equals(this) && match.getResult() == MatchResult.PLAYER1_WIN))
            .count();
    }

    public int getDrawCount() {
        return (int) getAllMatches().stream()
            .filter(match -> match.getResult() == MatchResult.DRAW)
            .count();
    }

    public boolean hasFinishedAllMatches() {
        return getAllMatches().stream()
            .allMatch(match -> match.getStatus() == MatchStatus.COMPLETED);
    }

    public double getWinRate() {
        int totalFinishedMatches = getWinCount() + getLossCount() + getDrawCount();
        if (totalFinishedMatches == 0) return 0.0;
        return (double) getWinCount() / totalFinishedMatches * 100;
    }
}