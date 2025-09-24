package com.lorecraft.tcglounge.entity;

import jakarta.persistence.*;

/**
 * 게이머 엔티티 (User 하위 클래스)
 *
 * 역할:
 * - 게임 플레이어의 통계 및 포인트 정보 관리
 * - 전적, 레이팅, 포인트 등 게임 특화 데이터 보관
 * - 마이페이지 접근 시 필요한 추가 정보 제공
 *
 * 사용 지침:
 * - Controller에서는 직접 Gamer 타입을 받지 말고 User로 받은 후 userType 확인
 * - 필요한 경우에만 repository를 통해 Gamer 엔티티 조회
 * - 예: Gamer gamer = gamerRepository.findById(user.getUid()).orElseThrow();
 */
@Entity
@Table(name = "gamers")
@DiscriminatorValue("GAMER")
public class Gamer extends User {
    
    @Column(name = "total_wins")
    private Integer totalWins = 0;
    
    @Column(name = "total_losses")
    private Integer totalLosses = 0;
    
    @Column(name = "total_draws") 
    private Integer totalDraws = 0;
    
    @Column(name = "usable_point")
    private Integer usablePoint = 0;
    
    @Column(name = "used_point")
    private Integer usedPoint = 0;
    
    @Column(name = "current_rating")
    private Integer currentRating = 1000;
    
    @Column(name = "highest_rating")
    private Integer highestRating = 1000;
    
    public Gamer() {}
    
    public Gamer(String userid, String password, String nickname, String email) {
        super(userid, password, nickname, email);
    }
    
    // Getters
    public Integer getTotalWins() { return totalWins; }
    public Integer getTotalLosses() { return totalLosses; }
    public Integer getTotalDraws() { return totalDraws; }
    public Integer getUsablePoint() { return usablePoint; }
    public Integer getUsedPoint() { return usedPoint; }
    public Integer getCurrentRating() { return currentRating; }
    public Integer getHighestRating() { return highestRating; }
    
    // Setters
    public void setTotalWins(Integer totalWins) { this.totalWins = totalWins; }
    public void setTotalLosses(Integer totalLosses) { this.totalLosses = totalLosses; }
    public void setTotalDraws(Integer totalDraws) { this.totalDraws = totalDraws; }
    public void setUsablePoint(Integer usablePoint) { this.usablePoint = usablePoint; }
    public void setUsedPoint(Integer usedPoint) { this.usedPoint = usedPoint; }
    public void setCurrentRating(Integer currentRating) { this.currentRating = currentRating; }
    public void setHighestRating(Integer highestRating) { this.highestRating = highestRating; }
    
    // Business methods
    public void addWin() {
        this.totalWins++;
        updateRating(30); // 승리시 +30 points
    }
    
    public void addLoss() {
        this.totalLosses++;
        updateRating(-20); // 패배시 -20 points  
    }
    
    public void addDraw() {
        this.totalDraws++;
        updateRating(5); // 무승부시 +5 points
    }
    
    private void updateRating(int change) {
        this.currentRating = Math.max(0, this.currentRating + change);
        if (this.currentRating > this.highestRating) {
            this.highestRating = this.currentRating;
        }
    }
    
    public Double getWinRate() {
        int totalGames = totalWins + totalLosses + totalDraws;
        if (totalGames == 0) return 0.0;
        return (double) totalWins / totalGames * 100;
    }
}