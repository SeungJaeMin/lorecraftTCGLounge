package com.lorecraft.tcglounge.repository;

import com.lorecraft.tcglounge.entity.StoreOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreOwnerRepository extends JpaRepository<StoreOwner, Long> {
    Optional<StoreOwner> findByUserid(String userid);
    Optional<StoreOwner> findByEmail(String email);
    boolean existsByUserid(String userid);
    boolean existsByEmail(String email);
}