package com.lorecraft.tcglounge.domain.user.repository;

import com.lorecraft.tcglounge.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByid(String id);
    
    Optional<User> findByEmail(String email);
    
    boolean existsByid(String id);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.id = :id AND u.isActive = true")
    Optional<User> findActiveUserByid(@Param("id") String id);
    
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.isActive = true")
    Optional<User> findActiveUserByEmail(@Param("email") String email);
}