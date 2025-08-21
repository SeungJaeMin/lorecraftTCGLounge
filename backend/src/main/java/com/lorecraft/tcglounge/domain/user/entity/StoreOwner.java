package com.lorecraft.tcglounge.domain.user.entity;

import com.lorecraft.tcglounge.domain.competition.entity.Competition;
import com.lorecraft.tcglounge.domain.store.entity.OrderList;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "store_owners")
@DiscriminatorValue("STORE_OWNER")
@PrimaryKeyJoinColumn(name = "user_id")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class StoreOwner extends User {

    @Column(name = "store_name", nullable = false, length = 100)
    private String storeName;

    @Column(name = "store_location", nullable = false, length = 255)
    private String storeLocation;

    @Column(name = "store_zipcode", length = 10)
    private String storeZipcode;

    @Column(name = "business_license", unique = true, length = 50)
    private String businessLicense;

    @Column(name = "contact_number", length = 20)
    private String contactNumber;

    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false;

    @Column(name = "is_authorized", nullable = false)
    private Boolean isAuthorized = false;

    // 연관관계
    @OneToMany(mappedBy = "assignedStoreOwner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Competition> managedCompetitions = new ArrayList<>();

    @OneToMany(mappedBy = "storeOwner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderList> orderLists = new ArrayList<>();

    // 비즈니스 메서드
    public void verify() {
        this.isVerified = true;
    }

    public void unverify() {
        this.isVerified = false;
    }

    public void authorize() {
        this.isAuthorized = true;
    }

    public void unauthorize() {
        this.isAuthorized = false;
    }

    public boolean canManageCompetitions() {
        return isVerified && isAuthorized && isActive;
    }

    public void assignCompetition(Competition competition) {
        managedCompetitions.add(competition);
        competition.setAssignedStoreOwner(this);
    }

    public void removeCompetition(Competition competition) {
        managedCompetitions.remove(competition);
        competition.setAssignedStoreOwner(null);
    }

    public int getActiveCompetitionsCount() {
        return (int) managedCompetitions.stream()
            .filter(comp -> comp.getStatus().isActive())
            .count();
    }
}