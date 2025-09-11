package com.lorecraft.tcglounge.repository;

import com.lorecraft.tcglounge.entity.ItemCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemCardRepository extends JpaRepository<ItemCard, Long> {
}