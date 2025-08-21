package com.lorecraft.tcglounge.domain.competition.entity;

public enum MatchResult {
    PLAYER1_WIN("플레이어1 승리"),
    PLAYER2_WIN("플레이어2 승리"),
    DRAW("무승부"),
    PLAYER1_FORFEIT("플레이어1 기권"),
    PLAYER2_FORFEIT("플레이어2 기권"),
    DOUBLE_FORFEIT("양자 기권"),
    NO_SHOW("불참"),
    DISQUALIFICATION("실격");

    private final String koreanName;

    MatchResult(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }

    public boolean isWin() {
        return this == PLAYER1_WIN || this == PLAYER2_WIN;
    }

    public boolean isDraw() {
        return this == DRAW;
    }

    public boolean isForfeit() {
        return this == PLAYER1_FORFEIT || this == PLAYER2_FORFEIT || this == DOUBLE_FORFEIT;
    }

    public boolean isRegularResult() {
        return this == PLAYER1_WIN || this == PLAYER2_WIN || this == DRAW;
    }

    public boolean isIrregularResult() {
        return !isRegularResult();
    }

    public MatchResult getOppositeResult() {
        return switch (this) {
            case PLAYER1_WIN -> PLAYER2_WIN;
            case PLAYER2_WIN -> PLAYER1_WIN;
            case PLAYER1_FORFEIT -> PLAYER2_WIN;
            case PLAYER2_FORFEIT -> PLAYER1_WIN;
            default -> this;
        };
    }
}