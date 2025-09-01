package com.lorecraft.tcglounge.domain.user.entity;

import com.lorecraft.tcglounge.domain.content.entity.Content;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_admin")
@DiscriminatorValue("ADMIN")
@PrimaryKeyJoinColumn(name = "user_id")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Admin extends User {

    @Column(name = "admin_level", nullable = false)
    @Builder.Default
    private Integer adminLevel = 1;

    @Column(name = "department", length = 50)
    private String department;

    @Column(name = "employee_id", unique = true, length = 20)
    private String employeeId;

    @Column(name = "can_manage_users", nullable = false)
    @Builder.Default
    private Boolean canManageUsers = false;

    @Column(name = "can_manage_competitions", nullable = false)
    @Builder.Default
    private Boolean canManageCompetitions = false;

    @Column(name = "can_manage_content", nullable = false)
    @Builder.Default
    private Boolean canManageContent = false;

    @Column(name = "can_manage_system", nullable = false)
    @Builder.Default
    private Boolean canManageSystem = false;

    // 연관관계
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Content> createdContents = new ArrayList<>();

    // 비즈니스 메서드
    public boolean isSuperAdmin() {
        return adminLevel >= 9;
    }

    public boolean canManageStoreOwners() {
        return canManageUsers || isSuperAdmin();
    }

    public boolean canCreateCompetitions() {
        return canManageCompetitions || isSuperAdmin();
    }

    public boolean canPublishContent() {
        return canManageContent || isSuperAdmin();
    }

    public boolean canAccessSystemSettings() {
        return canManageSystem || isSuperAdmin();
    }

    public void grantUserManagement() {
        this.canManageUsers = true;
    }

    public void grantCompetitionManagement() {
        this.canManageCompetitions = true;
    }

    public void grantContentManagement() {
        this.canManageContent = true;
    }

    public void grantSystemManagement() {
        this.canManageSystem = true;
    }

    public void grantAllPermissions() {
        this.canManageUsers = true;
        this.canManageCompetitions = true;
        this.canManageContent = true;
        this.canManageSystem = true;
        this.adminLevel = 9;
    }

    public void addContent(Content content) {
        createdContents.add(content);
        content.setAuthor(this);
    }
}