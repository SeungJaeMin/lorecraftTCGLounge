package com.lorecraft.tcglounge.repository;

import com.lorecraft.tcglounge.entity.UnitCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitCardRepository extends JpaRepository<UnitCard, Long> {
}