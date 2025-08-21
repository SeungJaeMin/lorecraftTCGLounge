package com.lorecraft.tcglounge.domain.competition.entity;

import com.lorecraft.tcglounge.domain.user.entity.StoreOwner;
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
@Table(name = "competitions")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Competition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cid")
    private Long cid;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private CompetitionType type;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "enrollment_start", nullable = false)
    private LocalDateTime enrollmentStart;

    @Column(name = "enrollment_end", nullable = false)
    private LocalDateTime enrollmentEnd;

    @Column(name = "max_participants", nullable = false)
    private Integer maxParticipants;

    @Column(name = "current_participants", nullable = false)
    private Integer currentParticipants = 0;

    @Column(name = "deck_limit", nullable = false)
    private Integer deckLimit = 1;

    @Column(name = "entry_fee")
    private Integer entryFee = 0;

    @Column(name = "prize_pool")
    private Integer prizePool = 0;

    @Column(name = "location", length = 255)
    private String location;

    @Column(name = "season", nullable = false, length = 20)
    private String season;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CompetitionStatus status = CompetitionStatus.SCHEDULED;

    @Column(name = "format_rules", columnDefinition = "TEXT")
    private String formatRules;

    @Column(name = "special_rules", columnDefinition = "TEXT")
    private String specialRules;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_store_owner_id")
    private StoreOwner assignedStoreOwner;

    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Enrollment> enrollments = new ArrayList<>();

    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Match> matches = new ArrayList<>();

    // 비즈니스 메서드
    public boolean canEnroll() {
        LocalDateTime now = LocalDateTime.now();
        return status == CompetitionStatus.SCHEDULED && 
               now.isAfter(enrollmentStart) && 
               now.isBefore(enrollmentEnd) &&
               currentParticipants < maxParticipants;
    }

    public boolean isEnrollmentOpen() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(enrollmentStart) && now.isBefore(enrollmentEnd);
    }

    public boolean isEnrollmentClosed() {
        return LocalDateTime.now().isAfter(enrollmentEnd) || currentParticipants >= maxParticipants;
    }

    public boolean hasStarted() {
        return LocalDateTime.now().isAfter(startDate);
    }

    public boolean hasEnded() {
        return LocalDateTime.now().isAfter(endDate);
    }

    public void incrementParticipants() {
        if (currentParticipants < maxParticipants) {
            this.currentParticipants++;
        }
    }

    public void decrementParticipants() {
        if (currentParticipants > 0) {
            this.currentParticipants--;
        }
    }

    public void start() {
        if (status == CompetitionStatus.SCHEDULED && hasStarted()) {
            this.status = CompetitionStatus.IN_PROGRESS;
        }
    }

    public void complete() {
        this.status = CompetitionStatus.COMPLETED;
    }

    public void cancel() {
        this.status = CompetitionStatus.CANCELLED;
    }

    public void suspend() {
        this.status = CompetitionStatus.SUSPENDED;
    }

    public void resume() {
        if (status == CompetitionStatus.SUSPENDED) {
            this.status = CompetitionStatus.IN_PROGRESS;
        }
    }

    public boolean isFull() {
        return currentParticipants >= maxParticipants;
    }

    public int getAvailableSlots() {
        return maxParticipants - currentParticipants;
    }

    public boolean requiresMultipleDecks() {
        return deckLimit > 1;
    }

    public boolean hasEntryFee() {
        return entryFee != null && entryFee > 0;
    }

    public boolean hasPrizePool() {
        return prizePool != null && prizePool > 0;
    }

    public boolean isAssigned() {
        return assignedStoreOwner != null;
    }

    public void assignStoreOwner(StoreOwner storeOwner) {
        this.assignedStoreOwner = storeOwner;
        storeOwner.assignCompetition(this);
    }

    public void unassignStoreOwner() {
        if (assignedStoreOwner != null) {
            assignedStoreOwner.removeCompetition(this);
            this.assignedStoreOwner = null;
        }
    }

    public List<Enrollment> getCheckedInEnrollments() {
        return enrollments.stream()
            .filter(enrollment -> enrollment.getCheckInStatus() == CheckInStatus.CHECKED_IN)
            .toList();
    }

    public int getCheckedInCount() {
        return getCheckedInEnrollments().size();
    }

    // Enum 정의
    public enum CompetitionType {
        SWISS_ROUND("스위스 라운드"),
        SINGLE_ELIMINATION("싱글 엘리미네이션"),
        DOUBLE_ELIMINATION("더블 엘리미네이션"),
        ROUND_ROBIN("라운드 로빈"),
        BEST_OF_THREE("3전 2승"),
        CUSTOM("커스텀");

        private final String koreanName;

        CompetitionType(String koreanName) {
            this.koreanName = koreanName;
        }

        public String getKoreanName() {
            return koreanName;
        }
    }

    public enum CompetitionStatus {
        SCHEDULED("예정"),
        IN_PROGRESS("진행중"),
        COMPLETED("완료"),
        CANCELLED("취소"),
        SUSPENDED("중단");

        private final String koreanName;

        CompetitionStatus(String koreanName) {
            this.koreanName = koreanName;
        }

        public String getKoreanName() {
            return koreanName;
        }

        public boolean isActive() {
            return this == SCHEDULED || this == IN_PROGRESS;
        }

        public boolean isFinished() {
            return this == COMPLETED || this == CANCELLED;
        }
    }
}