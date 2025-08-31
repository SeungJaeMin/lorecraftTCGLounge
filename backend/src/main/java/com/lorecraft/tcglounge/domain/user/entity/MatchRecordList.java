package com.lorecraft.tcglounge.domain.user.entity;

import com.lorecraft.tcglounge.domain.competition.entity.Match;
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
@Table(name = "match_record_lists")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MatchRecordList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gamer_id", nullable = false)
    private Gamer gamer;

    @Column(name = "season", nullable = false)
    private String season;

    @Column(name = "total_matches", nullable = false)
    private Integer totalMatches = 0;

    @Column(name = "wins", nullable = false)
    private Integer wins = 0;

    @Column(name = "losses", nullable = false)
    private Integer losses = 0;

    @Column(name = "draws", nullable = false)
    private Integer draws = 0;

    @Column(name = "best_rank")
    private Integer bestRank;

    @Column(name = "current_rank")
    private Integer currentRank;

    @Column(name = "rating_change", nullable = false)
    private Integer ratingChange = 0;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 연관관계 - Match와 직접 연결하지 않고, 통계만 관리
    // 필요시 별도의 매핑 테이블을 통해 관리

    // 비즈니스 메서드
    public void recordWin() {
        this.wins++;
        this.totalMatches++;
    }

    public void recordLoss() {
        this.losses++;
        this.totalMatches++;
    }

    public void recordDraw() {
        this.draws++;
        this.totalMatches++;
    }

    public double getWinRate() {
        if (totalMatches == 0) return 0.0;
        return (double) wins / totalMatches * 100;
    }

    public void updateRank(Integer newRank) {
        if (bestRank == null || newRank < bestRank) {
            bestRank = newRank;
        }
        currentRank = newRank;
    }

    public void updateRatingChange(int change) {
        this.ratingChange += change;
    }

    public boolean hasPlayedThisSeason() {
        return totalMatches > 0;
    }
}