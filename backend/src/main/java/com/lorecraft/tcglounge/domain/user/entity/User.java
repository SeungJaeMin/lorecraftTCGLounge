package com.lorecraft.tcglounge.domain.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "user_type")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;

    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String id;

    @Column(name = "password", nullable = false, length = 255)
    private String pw;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "register_date", nullable = false)
    @CreatedDate
    private LocalDateTime registerDate;

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    protected Boolean isActive = true;

    @Column(name = "user_type", insertable = false, updatable = false)
    private String userType;

    // 비즈니스 메서드
    public void deactivate() {
        this.isActive = false;
    }

    public void activate() {
        this.isActive = true;
    }

    public boolean isAdmin() {
        return this instanceof Admin;
    }

    public boolean isGamer() {
        return this instanceof Gamer;
    }

    public boolean isStoreOwner() {
        return this instanceof StoreOwner;
    }
}