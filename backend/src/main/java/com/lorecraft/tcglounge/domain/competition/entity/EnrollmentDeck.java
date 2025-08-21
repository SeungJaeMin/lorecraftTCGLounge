package com.lorecraft.tcglounge.domain.competition.entity;

import com.lorecraft.tcglounge.domain.card.entity.CardDeck;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "enrollment_decks",
    uniqueConstraints = @UniqueConstraint(columnNames = {"enrollment_id", "deck_id"}))
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentDeck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deck_id", nullable = false)
    private CardDeck deck;

    @Column(name = "deck_order", nullable = false)
    private Integer deckOrder;

    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary = false;

    @Column(name = "notes", length = 255)
    private String notes;

    @CreatedDate
    @Column(name = "added_at", nullable = false)
    private LocalDateTime addedAt;

    // 비즈니스 메서드
    public void markAsPrimary() {
        // 다른 덱들의 primary 상태 해제는 서비스 레이어에서 처리
        this.isPrimary = true;
    }

    public void unmarkAsPrimary() {
        this.isPrimary = false;
    }

    public boolean isValidForCompetition() {
        return deck != null && 
               deck.canBeUsedInCompetition() && 
               deck.getGamer().equals(enrollment.getGamer());
    }

    public void updateOrder(Integer newOrder) {
        this.deckOrder = newOrder;
    }

    public void addNote(String note) {
        if (this.notes == null || this.notes.trim().isEmpty()) {
            this.notes = note;
        } else {
            this.notes += "; " + note;
        }
    }

    public void clearNotes() {
        this.notes = null;
    }

    public boolean hasNotes() {
        return notes != null && !notes.trim().isEmpty();
    }
}