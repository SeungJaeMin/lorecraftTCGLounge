package com.lorecraft.tcglounge.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 사용자 기본 엔티티 (베이스 클래스)
 *
 * 역할:
 * - 모든 사용자 타입의 공통 속성 정의
 * - 인증/인가의 기본 단위
 * - JOINED 상속 전략을 통해 Gamer, Admin, StoreOwner로 확장
 *
 * 사용 지침:
 * - Controller에서는 항상 User 타입으로 받아서 처리
 * - userType 필드로 사용자 타입 확인 (GAMER, ADMIN, STORE_OWNER)
 * - 특정 타입의 추가 정보가 필요한 경우에만 캐스팅 사용
 *
 * 예시:
 * @CurrentUser User user -> 모든 컨트롤러에서 이 방식 사용
 * if ("GAMER".equals(user.getUserType())) { // 게이머 타입 확인 }
 */
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "user_type")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid")
    private Long uid;
    
    @Column(name = "userid", unique = true, nullable = false)
    private String userid;
    
    @Column(name = "password", nullable = false)
    private String password;
    
    @Column(name = "nickname")
    private String nickname;
    
    @Column(name = "email", unique = true, nullable = false)
    private String email;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @CreatedDate
    @Column(name = "register_date")
    private LocalDateTime registerDate;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "user_type", insertable = false, updatable = false)
    private String userType;
    
    public User() {}
    
    public User(String userid, String password, String nickname, String email) {
        this.userid = userid;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
    }
    
    // Getters
    public Long getUid() { return uid; }
    public String getUserid() { return userid; }
    public String getPassword() { return password; }
    public String getNickname() { return nickname; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public LocalDateTime getRegisterDate() { return registerDate; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public Boolean getIsActive() { return isActive; }
    public String getUserType() { return userType; }
    
    // Setters
    public void setUid(Long uid) { this.uid = uid; }
    public void setUserid(String userid) { this.userid = userid; }
    public void setPassword(String password) { this.password = password; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setRegisterDate(LocalDateTime registerDate) { this.registerDate = registerDate; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public void setUserType(String userType) { this.userType = userType; }
    
    // UserType enum
    public enum UserType {
        GAMER, ADMIN, STORE_OWNER
    }
}