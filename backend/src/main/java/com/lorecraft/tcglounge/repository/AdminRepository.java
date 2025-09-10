package com.lorecraft.tcglounge.repository;

import com.lorecraft.tcglounge.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByUserid(String userid);
    Optional<Admin> findByEmail(String email);
    boolean existsByUserid(String userid);
    boolean existsByEmail(String email);
}