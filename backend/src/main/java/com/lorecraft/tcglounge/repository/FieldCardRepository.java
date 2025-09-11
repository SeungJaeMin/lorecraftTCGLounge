package com.lorecraft.tcglounge.repository;

import com.lorecraft.tcglounge.entity.FieldCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FieldCardRepository extends JpaRepository<FieldCard, Long> {
}