package com.lorecraft.tcglounge.domain.store.entity;

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
@Table(name = "order_lists")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class OrderList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_owner_id", nullable = false)
    private StoreOwner storeOwner;

    @Column(name = "order_number", unique = true, nullable = false, length = 50)
    private String orderNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(name = "total_amount", nullable = false)
    private Integer totalAmount = 0;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "requested_delivery_date")
    private LocalDateTime requestedDeliveryDate;

    @Column(name = "actual_delivery_date")
    private LocalDateTime actualDeliveryDate;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 연관관계
    @OneToMany(mappedBy = "orderList", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();

    // 비즈니스 메서드
    public void addOrder(Order order) {
        orders.add(order);
        order.setOrderList(this);
        calculateTotalAmount();
    }

    public void removeOrder(Order order) {
        orders.remove(order);
        order.setOrderList(null);
        calculateTotalAmount();
    }

    private void calculateTotalAmount() {
        this.totalAmount = orders.stream()
            .mapToInt(Order::getTotalPrice)
            .sum();
    }

    public void confirm() {
        if (status == OrderStatus.PENDING) {
            this.status = OrderStatus.CONFIRMED;
        }
    }

    public void process() {
        if (status == OrderStatus.CONFIRMED) {
            this.status = OrderStatus.PROCESSING;
        }
    }

    public void ship() {
        if (status == OrderStatus.PROCESSING) {
            this.status = OrderStatus.SHIPPED;
        }
    }

    public void deliver() {
        if (status == OrderStatus.SHIPPED) {
            this.status = OrderStatus.DELIVERED;
            this.actualDeliveryDate = LocalDateTime.now();
        }
    }

    public void cancel() {
        if (status == OrderStatus.PENDING || status == OrderStatus.CONFIRMED) {
            this.status = OrderStatus.CANCELLED;
        }
    }

    public boolean canModify() {
        return status == OrderStatus.PENDING;
    }

    public boolean isDelivered() {
        return status == OrderStatus.DELIVERED;
    }

    public boolean isCancelled() {
        return status == OrderStatus.CANCELLED;
    }

    // Enum 정의
    public enum OrderStatus {
        PENDING("대기중"),
        CONFIRMED("확인됨"),
        PROCESSING("처리중"),
        SHIPPED("배송중"),
        DELIVERED("배송완료"),
        CANCELLED("취소됨");

        private final String koreanName;

        OrderStatus(String koreanName) {
            this.koreanName = koreanName;
        }

        public String getKoreanName() {
            return koreanName;
        }
    }
}