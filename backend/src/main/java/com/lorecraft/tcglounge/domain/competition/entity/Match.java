package com.lorecraft.tcglounge.domain.competition.entity;

import com.lorecraft.tcglounge.domain.user.entity.MatchRecordList;
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
@Table(name = "matches")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "match_id")
    private Long matchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id", nullable = false)
    private Competition competition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player1_enrollment_id", nullable = false)
    private Enrollment player1Enrollment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player2_enrollment_id", nullable = false)
    private Enrollment player2Enrollment;

    @Column(name = "round_number", nullable = false)
    private Integer roundNumber;

    @Column(name = "match_number")
    private Integer matchNumber;

    @Column(name = "table_number")
    private Integer tableNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MatchStatus status = MatchStatus.SCHEDULED;

    @Enumerated(EnumType.STRING)
    @Column(name = "result")
    private MatchResult result;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_enrollment_id")
    private Enrollment winner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loser_enrollment_id")
    private Enrollment loser;

    @Column(name = "player1_score")
    private Integer player1Score = 0;

    @Column(name = "player2_score")
    private Integer player2Score = 0;

    @Column(name = "scheduled_time")
    private LocalDateTime scheduledTime;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "referee_notes", columnDefinition = "TEXT")
    private String refereeNotes;

    @Column(name = "is_bye", nullable = false)
    private Boolean isBye = false;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 연관관계
    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MatchDetailResult> detailResults = new ArrayList<>();

    // 비즈니스 메서드
    public void start() {
        if (status == MatchStatus.SCHEDULED) {
            this.status = MatchStatus.IN_PROGRESS;
            this.startTime = LocalDateTime.now();
        }
    }

    public void complete(MatchResult matchResult) {
        if (status == MatchStatus.IN_PROGRESS) {
            this.status = MatchStatus.COMPLETED;
            this.result = matchResult;
            this.endTime = LocalDateTime.now();
            
            if (startTime != null) {
                this.durationMinutes = (int) java.time.Duration.between(startTime, endTime).toMinutes();
            }
            
            updateWinnerAndLoser();
        }
    }

    private void updateWinnerAndLoser() {
        if (result == MatchResult.PLAYER1_WIN) {
            this.winner = player1Enrollment;
            this.loser = player2Enrollment;
        } else if (result == MatchResult.PLAYER2_WIN) {
            this.winner = player2Enrollment;
            this.loser = player1Enrollment;
        } else if (result == MatchResult.PLAYER1_FORFEIT) {
            this.winner = player2Enrollment;
            this.loser = player1Enrollment;
        } else if (result == MatchResult.PLAYER2_FORFEIT) {
            this.winner = player1Enrollment;
            this.loser = player2Enrollment;
        } else {
            this.winner = null;
            this.loser = null;
        }
    }

    public void cancel() {
        if (status.canCancel()) {
            this.status = MatchStatus.CANCELLED;
        }
    }

    public void postpone() {
        if (status.canPostpone()) {
            this.status = MatchStatus.POSTPONED;
        }
    }

    public void reschedule(LocalDateTime newTime) {
        if (status == MatchStatus.SCHEDULED || status == MatchStatus.POSTPONED) {
            this.scheduledTime = newTime;
            this.status = MatchStatus.SCHEDULED;
        }
    }

    public void updateScore(int player1Score, int player2Score) {
        this.player1Score = player1Score;
        this.player2Score = player2Score;
    }

    public void setTable(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public void addRefereeNote(String note) {
        if (this.refereeNotes == null || this.refereeNotes.trim().isEmpty()) {
            this.refereeNotes = note;
        } else {
            this.refereeNotes += "\n" + LocalDateTime.now() + ": " + note;
        }
    }

    public void addNote(String note) {
        if (this.notes == null || this.notes.trim().isEmpty()) {
            this.notes = note;
        } else {
            this.notes += "\n" + note;
        }
    }

    public boolean isFinished() {
        return status.isFinished();
    }

    public boolean hasWinner() {
        return winner != null;
    }

    public boolean isDraw() {
        return result == MatchResult.DRAW;
    }

    public boolean isForfeit() {
        return result != null && result.isForfeit();
    }

    public boolean isRegularMatch() {
        return !isBye;
    }

    public void setAsBye(Enrollment byePlayer) {
        this.isBye = true;
        this.player1Enrollment = byePlayer;
        this.player2Enrollment = null;
        this.result = MatchResult.PLAYER1_WIN;
        this.winner = byePlayer;
        this.status = MatchStatus.COMPLETED;
        this.startTime = LocalDateTime.now();
        this.endTime = LocalDateTime.now();
        this.durationMinutes = 0;
    }

    public Enrollment getOpponent(Enrollment player) {
        if (player.equals(player1Enrollment)) {
            return player2Enrollment;
        } else if (player.equals(player2Enrollment)) {
            return player1Enrollment;
        }
        return null;
    }

    public boolean involves(Enrollment enrollment) {
        return player1Enrollment.equals(enrollment) || 
               (player2Enrollment != null && player2Enrollment.equals(enrollment));
    }

    public String getMatchDescription() {
        String p1Name = player1Enrollment.getGamer().getId();
        String p2Name = player2Enrollment != null ? player2Enrollment.getGamer().getId() : "BYE";
        return String.format("R%d-%d: %s vs %s", roundNumber, matchNumber, p1Name, p2Name);
    }

    public LocalDateTime getMatchDate() {
        return scheduledTime != null ? scheduledTime : createdAt;
    }
}