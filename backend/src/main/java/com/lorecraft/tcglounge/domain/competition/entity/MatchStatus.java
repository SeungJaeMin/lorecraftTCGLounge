package com.lorecraft.tcglounge.domain.competition.entity;

public enum MatchStatus {
    SCHEDULED("예정"),
    IN_PROGRESS("진행중"),
    COMPLETED("완료"),
    CANCELLED("취소"),
    POSTPONED("연기"),
    FORFEIT("기권"),
    NO_SHOW("불참");

    private final String koreanName;

    MatchStatus(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }

    public boolean isFinished() {
        return this == COMPLETED || this == CANCELLED || this == FORFEIT || this == NO_SHOW;
    }

    public boolean isActive() {
        return this == SCHEDULED || this == IN_PROGRESS;
    }

    public boolean canStart() {
        return this == SCHEDULED;
    }

    public boolean canComplete() {
        return this == IN_PROGRESS;
    }

    public boolean canCancel() {
        return this == SCHEDULED || this == IN_PROGRESS || this == POSTPONED;
    }

    public boolean canPostpone() {
        return this == SCHEDULED;
    }
}