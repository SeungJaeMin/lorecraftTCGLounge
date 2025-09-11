package com.lorecraft.tcglounge.service;

import com.lorecraft.tcglounge.dto.CardCreateDTO;
import com.lorecraft.tcglounge.dto.CardUpdateDTO;
import com.lorecraft.tcglounge.dto.CardDetailDTO;
import com.lorecraft.tcglounge.entity.*;
import com.lorecraft.tcglounge.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import java.util.*;

@Service
@Transactional
public class AdminCardService {

    private final CardRepository cardRepository;
    private final LeaderCardRepository leaderCardRepository;
    private final UnitCardRepository unitCardRepository;
    private final ItemCardRepository itemCardRepository;
    private final FieldCardRepository fieldCardRepository;
    private final SpellCardRepository spellCardRepository;
    private final CardImageRepository cardImageRepository;

    public AdminCardService(
            CardRepository cardRepository,
            LeaderCardRepository leaderCardRepository,
            UnitCardRepository unitCardRepository,
            ItemCardRepository itemCardRepository,
            FieldCardRepository fieldCardRepository,
            SpellCardRepository spellCardRepository,
            CardImageRepository cardImageRepository) {
        this.cardRepository = cardRepository;
        this.leaderCardRepository = leaderCardRepository;
        this.unitCardRepository = unitCardRepository;
        this.itemCardRepository = itemCardRepository;
        this.fieldCardRepository = fieldCardRepository;
        this.spellCardRepository = spellCardRepository;
        this.cardImageRepository = cardImageRepository;
    }

    // Get all cards with filtering and pagination
    public Page<CardDetailDTO> getAllCards(Pageable pageable, String cardType, String cardColor, String rarity, String searchTerm) {
        Specification<Card> spec = buildSpecification(cardType, cardColor, rarity, searchTerm);
        Page<Card> cards = cardRepository.findAll(spec, pageable);
        
        return cards.map(card -> {
            List<CardImage> images = cardImageRepository.findByCard_CardId(card.getCardId());
            return new CardDetailDTO(card, images);
        });
    }

    // Get single card by ID
    public CardDetailDTO getCardById(Long id) {
        Card card = cardRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Card not found with id: " + id));
        
        List<CardImage> images = cardImageRepository.findByCard_CardId(card.getCardId());
        return new CardDetailDTO(card, images);
    }

    // Create new card
    public Card createCard(CardCreateDTO dto) {
        Card card = null;
        
        switch (dto.getCardType().toUpperCase()) {
            case "LEADER":
                LeaderCard leader = new LeaderCard();
                setCommonFields(leader, dto);
                leader.setLeaderSkill(dto.getLeaderSkill());
                leader.setIsAwakened(dto.getIsAwakened() != null ? dto.getIsAwakened() : false);
                leader.setBurstSlot1(dto.getBurstSlot1() != null ? dto.getBurstSlot1() : 1);
                leader.setBurstSlot2(dto.getBurstSlot2() != null ? dto.getBurstSlot2() : 1);
                leader.setBurstSlot3(dto.getBurstSlot3() != null ? dto.getBurstSlot3() : 1);
                card = leaderCardRepository.save(leader);
                break;
                
            case "UNIT":
                UnitCard unit = new UnitCard();
                setCommonFields(unit, dto);
                unit.setPower(dto.getPower() != null ? dto.getPower() : 0);
                unit.setBurstValue(dto.getBurstValue() != null ? dto.getBurstValue() : 1);
                card = unitCardRepository.save(unit);
                break;
                
            case "ITEM":
                ItemCard item = new ItemCard();
                setCommonFields(item, dto);
                item.setEffect(dto.getEffect());
                item.setActivationCondition(dto.getActivationCondition());
                item.setIsConsumable(dto.getIsConsumable() != null ? dto.getIsConsumable() : true);
                item.setBurstValue(dto.getBurstValue() != null ? dto.getBurstValue() : 1);
                card = itemCardRepository.save(item);
                break;
                
            case "FIELD":
                FieldCard field = new FieldCard();
                setCommonFields(field, dto);
                field.setFieldEffect(dto.getFieldEffect());
                field.setAffectedColors(dto.getAffectedColors());
                field.setAffectedTypes(dto.getAffectedTypes());
                field.setBurstValue(dto.getBurstValue() != null ? dto.getBurstValue() : 1);
                card = fieldCardRepository.save(field);
                break;
                
            case "SPELL":
                SpellCard spell = new SpellCard();
                setCommonFields(spell, dto);
                spell.setSpellEffect(dto.getSpellEffect());
                spell.setTargetType(dto.getTargetType());
                spell.setBurstValue(dto.getBurstValue() != null ? dto.getBurstValue() : 1);
                card = spellCardRepository.save(spell);
                break;
                
            default:
                throw new IllegalArgumentException("Invalid card type: " + dto.getCardType());
        }
        
        return card;
    }

    // Update existing card
    public Card updateCard(Long id, CardUpdateDTO dto) {
        Card card = cardRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Card not found with id: " + id));
        
        // Update common fields
        if (dto.getCardName() != null) card.setCardName(dto.getCardName());
        if (dto.getCardImg() != null) card.setCardImg(dto.getCardImg());
        if (dto.getDescription() != null) card.setDescription(dto.getDescription());
        if (dto.getCardColor() != null) card.setCardColor(Card.CardColor.valueOf(dto.getCardColor()));
        if (dto.getRarity() != null) card.setRarity(Card.CardRarity.valueOf(dto.getRarity()));
        if (dto.getCost() != null) card.setCost(dto.getCost());
        if (dto.getCardNumber() != null) card.setCardNumber(dto.getCardNumber());
        
        // Update type-specific fields
        if (card instanceof LeaderCard) {
            LeaderCard leader = (LeaderCard) card;
            if (dto.getLeaderSkill() != null) leader.setLeaderSkill(dto.getLeaderSkill());
            if (dto.getIsAwakened() != null) leader.setIsAwakened(dto.getIsAwakened());
            if (dto.getBurstSlot1() != null) leader.setBurstSlot1(dto.getBurstSlot1());
            if (dto.getBurstSlot2() != null) leader.setBurstSlot2(dto.getBurstSlot2());
            if (dto.getBurstSlot3() != null) leader.setBurstSlot3(dto.getBurstSlot3());
        } else if (card instanceof UnitCard) {
            UnitCard unit = (UnitCard) card;
            if (dto.getPower() != null) unit.setPower(dto.getPower());
            if (dto.getBurstValue() != null) unit.setBurstValue(dto.getBurstValue());
        } else if (card instanceof ItemCard) {
            ItemCard item = (ItemCard) card;
            if (dto.getEffect() != null) item.setEffect(dto.getEffect());
            if (dto.getActivationCondition() != null) item.setActivationCondition(dto.getActivationCondition());
            if (dto.getIsConsumable() != null) item.setIsConsumable(dto.getIsConsumable());
            if (dto.getBurstValue() != null) item.setBurstValue(dto.getBurstValue());
        } else if (card instanceof FieldCard) {
            FieldCard field = (FieldCard) card;
            if (dto.getFieldEffect() != null) field.setFieldEffect(dto.getFieldEffect());
            if (dto.getAffectedColors() != null) field.setAffectedColors(dto.getAffectedColors());
            if (dto.getAffectedTypes() != null) field.setAffectedTypes(dto.getAffectedTypes());
            if (dto.getBurstValue() != null) field.setBurstValue(dto.getBurstValue());
        } else if (card instanceof SpellCard) {
            SpellCard spell = (SpellCard) card;
            if (dto.getSpellEffect() != null) spell.setSpellEffect(dto.getSpellEffect());
            if (dto.getTargetType() != null) spell.setTargetType(dto.getTargetType());
            if (dto.getBurstValue() != null) spell.setBurstValue(dto.getBurstValue());
        }
        
        return cardRepository.save(card);
    }

    // Delete card
    public void deleteCard(Long id) {
        if (!cardRepository.existsById(id)) {
            throw new EntityNotFoundException("Card not found with id: " + id);
        }
        
        // Delete associated images first
        List<CardImage> images = cardImageRepository.findByCard_CardId(id);
        cardImageRepository.deleteAll(images);
        
        // Delete the card
        cardRepository.deleteById(id);
    }

    // Bulk delete cards
    public void bulkDeleteCards(List<Long> cardIds) {
        for (Long id : cardIds) {
            deleteCard(id);
        }
    }

    // Get card statistics
    public Map<String, Object> getCardStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalCards", cardRepository.count());
        stats.put("leaderCount", leaderCardRepository.count());
        stats.put("unitCount", unitCardRepository.count());
        stats.put("itemCount", itemCardRepository.count());
        stats.put("fieldCount", fieldCardRepository.count());
        stats.put("spellCount", spellCardRepository.count());
        
        // Count by color
        Map<String, Long> colorCounts = new HashMap<>();
        for (Card.CardColor color : Card.CardColor.values()) {
            colorCounts.put(color.name(), cardRepository.countByCardColor(color));
        }
        stats.put("colorCounts", colorCounts);
        
        // Count by rarity
        Map<String, Long> rarityCounts = new HashMap<>();
        for (Card.CardRarity rarity : Card.CardRarity.values()) {
            rarityCounts.put(rarity.name(), cardRepository.countByRarity(rarity));
        }
        stats.put("rarityCounts", rarityCounts);
        
        return stats;
    }

    // Helper method to set common fields
    private void setCommonFields(Card card, CardCreateDTO dto) {
        card.setCardName(dto.getCardName());
        card.setCardImg(dto.getCardImg());
        card.setDescription(dto.getDescription());
        if (dto.getCardColor() != null) {
            card.setCardColor(Card.CardColor.valueOf(dto.getCardColor()));
        }
        if (dto.getRarity() != null) {
            card.setRarity(Card.CardRarity.valueOf(dto.getRarity()));
        }
        card.setCost(dto.getCost());
        card.setCardNumber(dto.getCardNumber());
    }

    // Build specification for filtering
    private Specification<Card> buildSpecification(String cardType, String cardColor, String rarity, String searchTerm) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (cardType != null && !cardType.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("class"), cardType));
            }
            
            if (cardColor != null && !cardColor.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("cardColor"), Card.CardColor.valueOf(cardColor)));
            }
            
            if (rarity != null && !rarity.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("rarity"), Card.CardRarity.valueOf(rarity)));
            }
            
            if (searchTerm != null && !searchTerm.isEmpty()) {
                Predicate namePredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("cardName")), 
                    "%" + searchTerm.toLowerCase() + "%"
                );
                Predicate descPredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("description")), 
                    "%" + searchTerm.toLowerCase() + "%"
                );
                predicates.add(criteriaBuilder.or(namePredicate, descPredicate));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}