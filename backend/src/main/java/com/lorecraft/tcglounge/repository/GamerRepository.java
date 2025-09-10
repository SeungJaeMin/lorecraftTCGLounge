package com.lorecraft.tcglounge.repository;

import com.lorecraft.tcglounge.entity.Gamer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GamerRepository extends JpaRepository<Gamer, Long> {
    
    @Query("SELECT g FROM Gamer g WHERE g.userid = :userid")
    Optional<Gamer> findByUserid(@Param("userid") String userid);
    
    @Query("SELECT g FROM Gamer g WHERE g.email = :email")
    Optional<Gamer> findByEmail(@Param("email") String email);
    
    @Query("SELECT COUNT(g) > 0 FROM Gamer g WHERE g.userid = :userid")
    boolean existsByUserid(@Param("userid") String userid);
    
    @Query("SELECT COUNT(g) > 0 FROM Gamer g WHERE g.email = :email")
    boolean existsByEmail(@Param("email") String email);
}