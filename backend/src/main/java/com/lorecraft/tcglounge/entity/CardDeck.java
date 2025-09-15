package com.lorecraft.tcglounge.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "card_decks")
@EntityListeners(AuditingEntityListener.class)
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
    
    public CardDeck() {}
    
    public CardDeck(Gamer gamer, String deckName) {
        this.gamer = gamer;
        this.deckName = deckName;
        generateDeckCode();
    }
    
    // Getters
    public Long getDeckId() { return deckId; }
    public Gamer getGamer() { return gamer; }
    public String getDeckName() { return deckName; }
    public String getDescription() { return description; }
    public DeckType getDeckType() { return deckType; }
    public Card getLeaderCard() { return leaderCard; }
    public Boolean getIsPublic() { return isPublic; }
    public Boolean getIsTournamentLegal() { return isTournamentLegal; }
    public Integer getTotalCards() { return totalCards; }
    public String getDeckCode() { return deckCode; }
    public Integer getLikesCount() { return likesCount; }
    public Integer getViewsCount() { return viewsCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public List<DeckDetail> getDeckDetails() { return deckDetails; }
    
    // Setters
    public void setDeckId(Long deckId) { this.deckId = deckId; }
    public void setGamer(Gamer gamer) { this.gamer = gamer; }
    public void setDeckName(String deckName) { this.deckName = deckName; }
    public void setDescription(String description) { this.description = description; }
    public void setDeckType(DeckType deckType) { this.deckType = deckType; }
    public void setLeaderCard(Card leaderCard) { this.leaderCard = leaderCard; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }
    public void setIsTournamentLegal(Boolean isTournamentLegal) { this.isTournamentLegal = isTournamentLegal; }
    public void setTotalCards(Integer totalCards) { this.totalCards = totalCards; }
    public void setDeckCode(String deckCode) { this.deckCode = deckCode; }
    public void setLikesCount(Integer likesCount) { this.likesCount = likesCount; }
    public void setViewsCount(Integer viewsCount) { this.viewsCount = viewsCount; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setDeckDetails(List<DeckDetail> deckDetails) { this.deckDetails = deckDetails; }
    
    // Business methods
    public void incrementViews() {
        this.viewsCount++;
    }
    
    public void incrementLikes() {
        this.likesCount++;
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