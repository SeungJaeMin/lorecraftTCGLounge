package com.lorecraft.tcglounge.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "card_decks")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CardDeck {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deck_id")
    private Long deckId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Gamer gamer;
    
    @Column(name = "deck_name", nullable = false)
    private String deckName;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "deck_type")
    private DeckType deckType = DeckType.STANDARD;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_card_id")
    private Card leaderCard;
    
    @Column(name = "is_public")
    private Boolean isPublic = false;
    
    @Column(name = "is_tournament_legal")
    private Boolean isTournamentLegal = false;
    
    @Column(name = "total_cards")
    private Integer totalCards = 0;
    
    @Column(name = "deck_code", unique = true)
    private String deckCode;
    
    @Column(name = "likes_count")
    private Integer likesCount = 0;
    
    @Column(name = "views_count")
    private Integer viewsCount = 0;
    
    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "deck", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<DeckDetail> deckDetails = new ArrayList<>();
    
    @Builder
    private CardDeck(Gamer gamer, String deckName, String description, DeckType deckType,
                    Boolean isPublic, Boolean isTournamentLegal, Card leaderCard) {
        this.gamer = gamer;
        this.deckName = deckName;
        this.description = description;
        this.deckType = deckType != null ? deckType : DeckType.STANDARD;
        this.isPublic = isPublic != null ? isPublic : false;
        this.isTournamentLegal = isTournamentLegal != null ? isTournamentLegal : false;
        this.leaderCard = leaderCard;
        this.totalCards = 0;
        this.likesCount = 0;
        this.viewsCount = 0;
        generateDeckCode();
    }

    // JPA용 간단 생성자 (하위 호환성)
    public CardDeck(Gamer gamer, String deckName) {
        this.gamer = gamer;
        this.deckName = deckName;
        this.deckType = DeckType.STANDARD;
        this.isPublic = false;
        this.isTournamentLegal = false;
        this.totalCards = 0;
        this.likesCount = 0;
        this.viewsCount = 0;
        generateDeckCode();
    }
    
    // Getters는 @Getter 어노테이션으로 자동 생성
    
    // Business methods (Setter 대체)
    public void updateDeckInfo(String deckName, String description) {
        if (deckName != null && !deckName.trim().isEmpty()) {
            this.deckName = deckName.trim();
        }
        if (description != null) {
            this.description = description.trim();
        }
    }

    public void updateSettings(DeckType deckType, Boolean isPublic, Boolean isTournamentLegal) {
        if (deckType != null) {
            this.deckType = deckType;
        }
        if (isPublic != null) {
            this.isPublic = isPublic;
        }
        if (isTournamentLegal != null) {
            this.isTournamentLegal = isTournamentLegal;
        }
    }

    public void changeLeaderCard(Card leaderCard) {
        this.leaderCard = leaderCard;
    }

    public void togglePublic() {
        this.isPublic = !this.isPublic;
    }

    public void toggleTournamentLegal() {
        this.isTournamentLegal = !this.isTournamentLegal;
    }

    public void incrementViews() {
        this.viewsCount++;
    }

    public void incrementLikes() {
        this.likesCount++;
    }

    public void decrementLikes() {
        if (this.likesCount > 0) {
            this.likesCount--;
        }
    }

    public void updateTotalCards() {
        this.totalCards = deckDetails.stream()
            .mapToInt(DeckDetail::getQuantity)
            .sum();
    }

    private void generateDeckCode() {
        this.deckCode = "DECK_" + System.currentTimeMillis();
    }


}