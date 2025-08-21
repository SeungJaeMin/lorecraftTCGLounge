package com.lorecraft.tcglounge.domain.store.entity;

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
@Table(name = "items")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_name", nullable = false, length = 100)
    private String itemName;

    @Column(name = "item_code", unique = true, nullable = false, length = 50)
    private String itemCode;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false)
    private ItemType itemType;

    @Column(name = "unit_price", nullable = false)
    private Integer unitPrice;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity = 0;

    @Column(name = "minimum_order_quantity", nullable = false)
    private Integer minimumOrderQuantity = 1;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;

    @Column(name = "release_date")
    private LocalDateTime releaseDate;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 연관관계
    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CardPack> cardPacks = new ArrayList<>();

    // 비즈니스 메서드
    public boolean isInStock() {
        return stockQuantity > 0;
    }

    public boolean canOrder(int quantity) {
        return isAvailable && 
               quantity >= minimumOrderQuantity && 
               stockQuantity >= quantity;
    }

    public void reduceStock(int quantity) {
        if (stockQuantity >= quantity) {
            this.stockQuantity -= quantity;
        }
    }

    public void increaseStock(int quantity) {
        this.stockQuantity += quantity;
    }

    public void updatePrice(int newPrice) {
        if (newPrice >= 0) {
            this.unitPrice = newPrice;
        }
    }

    public void discontinue() {
        this.isAvailable = false;
    }

    public void makeAvailable() {
        this.isAvailable = true;
    }

    public boolean isCardPack() {
        return itemType == ItemType.CARD_PACK;
    }

    public boolean isAccessory() {
        return itemType == ItemType.ACCESSORY;
    }

    public boolean hasImage() {
        return imageUrl != null && !imageUrl.trim().isEmpty();
    }

    // Enum 정의
    public enum ItemType {
        CARD_PACK("카드팩"),
        BOOSTER_BOX("부스터박스"),
        STARTER_DECK("스타터덱"),
        ACCESSORY("액세서리"),
        SLEEVE("슬리브"),
        PLAYMAT("플레이매트"),
        STORAGE("보관용품"),
        OTHER("기타");

        private final String koreanName;

        ItemType(String koreanName) {
            this.koreanName = koreanName;
        }

        public String getKoreanName() {
            return koreanName;
        }
    }
}