package com.lorecraft.tcglounge.repository;

import com.lorecraft.tcglounge.entity.SpellCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpellCardRepository extends JpaRepository<SpellCard, Long> {
}