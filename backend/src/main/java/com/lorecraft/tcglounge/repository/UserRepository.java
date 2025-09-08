package com.lorecraft.tcglounge.repository;

import com.lorecraft.tcglounge.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserid(String userid);
    Optional<User> findByEmail(String email);
    boolean existsByUserid(String userid);
    boolean existsByEmail(String email);
}