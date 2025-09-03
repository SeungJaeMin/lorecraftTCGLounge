package com.lorecraft.tcglounge.domain.user.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

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
}