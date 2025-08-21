package com.lorecraft.tcglounge.domain.user.entity;

import com.lorecraft.tcglounge.domain.card.entity.CardDeck;
import com.lorecraft.tcglounge.domain.competition.entity.Enrollment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "gamers")
@DiscriminatorValue("GAMER")
@PrimaryKeyJoinColumn(name = "user_id")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Gamer extends User {

    @Column(name = "nickname", length = 50)
    private String nickname;

    @Column(name = "total_wins", nullable = false)
    private Integer totalWins = 0;

    @Column(name = "total_losses", nullable = false)
    private Integer totalLosses = 0;

    @Column(name = "total_draws", nullable = false)
    private Integer totalDraws = 0;

    @Column(name = "current_rating", nullable = false)
    private Integer currentRating = 1000;

    @Column(name = "highest_rating", nullable = false)
    private Integer highestRating = 1000;

    // 연관관계
    @OneToMany(mappedBy = "gamer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CardDeck> cardDecks = new ArrayList<>();

    @OneToMany(mappedBy = "gamer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Enrollment> enrollments = new ArrayList<>();

    @OneToOne(mappedBy = "gamer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private MatchRecordList matchRecordList;

    // 비즈니스 메서드
    public void addWin() {
        this.totalWins++;
    }

    public void addLoss() {
        this.totalLosses++;
    }

    public void addDraw() {
        this.totalDraws++;
    }

    public void updateRating(int newRating) {
        this.currentRating = newRating;
        if (newRating > this.highestRating) {
            this.highestRating = newRating;
        }
    }

    public double getWinRate() {
        int totalGames = totalWins + totalLosses + totalDraws;
        if (totalGames == 0) return 0.0;
        return (double) totalWins / totalGames * 100;
    }

    public int getTotalGames() {
        return totalWins + totalLosses + totalDraws;
    }

    public void addDeck(CardDeck deck) {
        cardDecks.add(deck);
        deck.setGamer(this);
    }

    public void removeDeck(CardDeck deck) {
        cardDecks.remove(deck);
        deck.setGamer(null);
    }
}