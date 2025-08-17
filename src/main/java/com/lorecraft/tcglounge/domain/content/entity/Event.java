package com.lorecraft.tcglounge.domain.content.entity;

import com.lorecraft.tcglounge.domain.user.entity.Admin;
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

@Entity
@Table(name = "events")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "registration_start")
    private LocalDateTime registrationStart;

    @Column(name = "registration_end")
    private LocalDateTime registrationEnd;

    @Column(name = "location", length = 255)
    private String location;

    @Column(name = "max_participants")
    private Integer maxParticipants;

    @Column(name = "current_participants", nullable = false)
    private Integer currentParticipants = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private EventType eventType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EventStatus status = EventStatus.SCHEDULED;

    @Column(name = "requirements", columnDefinition = "TEXT")
    private String requirements;

    @Column(name = "rewards", columnDefinition = "TEXT")
    private String rewards;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(name = "is_featured", nullable = false)
    private Boolean isFeatured = false;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private Admin createdBy;

    // 비즈니스 메서드
    public boolean canRegister() {
        LocalDateTime now = LocalDateTime.now();
        return status == EventStatus.SCHEDULED &&
               (registrationStart == null || now.isAfter(registrationStart)) &&
               (registrationEnd == null || now.isBefore(registrationEnd)) &&
               (maxParticipants == null || currentParticipants < maxParticipants);
    }

    public boolean isRegistrationOpen() {
        LocalDateTime now = LocalDateTime.now();
        return (registrationStart == null || now.isAfter(registrationStart)) &&
               (registrationEnd == null || now.isBefore(registrationEnd));
    }

    public boolean hasStarted() {
        return LocalDateTime.now().isAfter(startDate);
    }

    public boolean hasEnded() {
        return LocalDateTime.now().isAfter(endDate);
    }

    public boolean isFull() {
        return maxParticipants != null && currentParticipants >= maxParticipants;
    }

    public void start() {
        if (status == EventStatus.SCHEDULED && hasStarted()) {
            this.status = EventStatus.IN_PROGRESS;
        }
    }

    public void complete() {
        this.status = EventStatus.COMPLETED;
    }

    public void cancel() {
        this.status = EventStatus.CANCELLED;
    }

    public void incrementParticipants() {
        if (maxParticipants == null || currentParticipants < maxParticipants) {
            this.currentParticipants++;
        }
    }

    public void decrementParticipants() {
        if (currentParticipants > 0) {
            this.currentParticipants--;
        }
    }

    public void feature() {
        this.isFeatured = true;
    }

    public void unfeature() {
        this.isFeatured = false;
    }

    public int getAvailableSlots() {
        if (maxParticipants == null) return Integer.MAX_VALUE;
        return maxParticipants - currentParticipants;
    }

    // Enum 정의
    public enum EventType {
        TOURNAMENT("토너먼트"),
        CASUAL_PLAY("캐주얼 플레이"),
        WORKSHOP("워크샵"),
        RELEASE_EVENT("출시 이벤트"),
        SEASONAL("시즌 이벤트"),
        ONLINE("온라인 이벤트"),
        COMMUNITY("커뮤니티 이벤트");

        private final String koreanName;

        EventType(String koreanName) {
            this.koreanName = koreanName;
        }

        public String getKoreanName() {
            return koreanName;
        }
    }

    public enum EventStatus {
        SCHEDULED("예정"),
        IN_PROGRESS("진행중"),
        COMPLETED("완료"),
        CANCELLED("취소");

        private final String koreanName;

        EventStatus(String koreanName) {
            this.koreanName = koreanName;
        }

        public String getKoreanName() {
            return koreanName;
        }

        public boolean isActive() {
            return this == SCHEDULED || this == IN_PROGRESS;
        }
    }
}