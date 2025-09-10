package com.lorecraft.tcglounge.dto;

import com.lorecraft.tcglounge.entity.Gamer;

public class GamerProfileDTO {
    private Long userId;
    private String username;
    private String nickname;
    private String email;
    private Integer currentRating;
    private Integer highestRating;
    private Integer totalWins;
    private Integer totalLosses;
    private Integer totalDraws;
    private Integer usablePoint;
    private Integer usedPoint;
    
    public GamerProfileDTO() {}
    
    public GamerProfileDTO(Gamer gamer) {
        this.userId = gamer.getUid();
        this.username = gamer.getUserid();
        this.nickname = gamer.getNickname();
        this.email = gamer.getEmail();
        this.currentRating = gamer.getCurrentRating();
        this.highestRating = gamer.getHighestRating();
        this.totalWins = gamer.getTotalWins();
        this.totalLosses = gamer.getTotalLosses();
        this.totalDraws = gamer.getTotalDraws();
        this.usablePoint = gamer.getUsablePoint();
        this.usedPoint = gamer.getUsedPoint();
    }
    
    // 계산된 필드들
    public Integer getTotalGames() {
        return (totalWins != null ? totalWins : 0) + 
               (totalLosses != null ? totalLosses : 0) + 
               (totalDraws != null ? totalDraws : 0);
    }
    
    public Double getWinRate() {
        int total = getTotalGames();
        if (total == 0) return 0.0;
        return ((double) (totalWins != null ? totalWins : 0) / total) * 100;
    }
    
    public String getRank() {
        if (currentRating == null) return "BRONZE";
        if (currentRating >= 2500) return "MASTER";
        if (currentRating >= 2000) return "DIAMOND";
        if (currentRating >= 1500) return "GOLD";
        if (currentRating >= 1000) return "SILVER";
        return "BRONZE";
    }

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public Integer getCurrentRating() { return currentRating; }
    public void setCurrentRating(Integer currentRating) { this.currentRating = currentRating; }
    
    public Integer getHighestRating() { return highestRating; }
    public void setHighestRating(Integer highestRating) { this.highestRating = highestRating; }
    
    public Integer getTotalWins() { return totalWins; }
    public void setTotalWins(Integer totalWins) { this.totalWins = totalWins; }
    
    public Integer getTotalLosses() { return totalLosses; }
    public void setTotalLosses(Integer totalLosses) { this.totalLosses = totalLosses; }
    
    public Integer getTotalDraws() { return totalDraws; }
    public void setTotalDraws(Integer totalDraws) { this.totalDraws = totalDraws; }
    
    public Integer getUsablePoint() { return usablePoint; }
    public void setUsablePoint(Integer usablePoint) { this.usablePoint = usablePoint; }
    
    public Integer getUsedPoint() { return usedPoint; }
    public void setUsedPoint(Integer usedPoint) { this.usedPoint = usedPoint; }
}