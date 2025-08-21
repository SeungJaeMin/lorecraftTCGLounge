package com.lorecraft.tcglounge.domain.store.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "orders")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_list_id", nullable = false)
    private OrderList orderList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false)
    private Integer unitPrice;

    @Column(name = "notes", length = 255)
    private String notes;

    // 비즈니스 메서드
    public int getTotalPrice() {
        return unitPrice * quantity;
    }

    public void updateQuantity(int newQuantity) {
        if (newQuantity > 0) {
            this.quantity = newQuantity;
        }
    }

    public void updateUnitPrice(int newPrice) {
        if (newPrice >= 0) {
            this.unitPrice = newPrice;
        }
    }

    public boolean hasNotes() {
        return notes != null && !notes.trim().isEmpty();
    }
}