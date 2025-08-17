package com.lorecraft.tcglounge.domain.competition.entity;

public enum CheckInStatus {
    NOT_CHECKED("미체크인"),
    CHECKED_IN("체크인 완료"),
    ABSENT("불참");

    private final String koreanName;

    CheckInStatus(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }

    public boolean isPresent() {
        return this == CHECKED_IN;
    }

    public boolean canParticipate() {
        return this == CHECKED_IN;
    }
}