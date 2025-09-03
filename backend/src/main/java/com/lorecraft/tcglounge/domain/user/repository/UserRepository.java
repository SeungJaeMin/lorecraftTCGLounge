package com.lorecraft.tcglounge.domain.user.repository;

import com.lorecraft.tcglounge.domain.user.entity.User;
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