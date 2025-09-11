package com.lorecraft.tcglounge.repository;

import com.lorecraft.tcglounge.entity.LeaderCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaderCardRepository extends JpaRepository<LeaderCard, Long> {
}