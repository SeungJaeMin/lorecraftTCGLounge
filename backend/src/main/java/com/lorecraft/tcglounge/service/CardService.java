package com.lorecraft.tcglounge.service;

import com.lorecraft.tcglounge.entity.*;
import com.lorecraft.tcglounge.repository.*;
import com.lorecraft.tcglounge.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class CardService {
    
    private static final Logger log = LoggerFactory.getLogger(CardService.class);
    
    private final CardRepository cardRepository;
    private final LeaderCardRepository leaderCardRepository;
    private final UnitCardRepository unitCardRepository;
    private final ItemCardRepository itemCardRepository;
    private final FieldCardRepository fieldCardRepository;
    private final SpellCardRepository spellCardRepository;
    private final CardImageRepository cardImageRepository;
    
    public CardService(
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
    
    public Card createCard(String cardName, Card.CardColor cardColor, Card.CardRarity rarity, Integer cost) {
        Card card = new Card(cardName, cardColor, rarity, cost);
        Card saved = cardRepository.save(card);
        log.info("Created new card: {}", cardName);
        return saved;
    }
    
    @Transactional(readOnly = true)
    public Optional<Card> findById(Long id) {
        return cardRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public List<Card> findAll() {
        List<Card> cards = cardRepository.findAll();
        // batch_fetch_size는 CardImage를 별도로 조회할 때 최적화됨
        return cards;
    }
    
    @Transactional(readOnly = true)
    public List<Card> searchByName(String cardName) {
        List<Card> cards = cardRepository.findByCardNameContaining(cardName);
        // batch_fetch_size는 CardImage를 별도로 조회할 때 최적화됨
        return cards;
    }
    
    @Transactional(readOnly = true)
    public List<Card> findByColor(Card.CardColor cardColor) {
        return cardRepository.findByCardColor(cardColor);
    }
    
    @Transactional(readOnly = true)
    public List<Card> findByRarity(Card.CardRarity rarity) {
        return cardRepository.findByRarity(rarity);
    }
    
    @Transactional(readOnly = true)
    public List<Card> findByCostRange(Integer maxCost) {
        return cardRepository.findByCostLessThanEqual(maxCost);
    }
    
    // ========== 관리자 전용 기능들 (AdminCardService에서 이동) ==========
    
    // Get all cards with filtering and pagination (관리자용)
    @Transactional(readOnly = true)
    public Page<CardDetailDTO> getAllCards(Pageable pageable, String cardType, String cardColor, String rarity, String searchTerm) {
        Specification<Card> spec = buildSpecification(cardType, cardColor, rarity, searchTerm);
        Page<Card> cards = cardRepository.findAll(spec, pageable);
        
        return cards.map(card -> {
            List<CardImage> images = cardImageRepository.findByCard_CardId(card.getCardId());
            return new CardDetailDTO(card, images);
        });
    }

    // Get single card by ID with details (관리자용)
    @Transactional(readOnly = true)
    public CardDetailDTO getCardById(Long id) {
        Card card = cardRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Card not found with id: " + id));
        
        List<CardImage> images = cardImageRepository.findByCard_CardId(card.getCardId());
        return new CardDetailDTO(card, images);
    }

    // Create new card (관리자 전용)
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
        
        log.info("Created new {} card: {}", dto.getCardType(), dto.getCardName());
        return card;
    }

    // Update existing card (관리자 전용)
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
        
        Card updated = cardRepository.save(card);
        log.info("Updated card: {}", updated.getCardName());
        return updated;
    }

    // Delete card (관리자 전용)
    public void deleteCard(Long id) {
        if (!cardRepository.existsById(id)) {
            throw new EntityNotFoundException("Card not found with id: " + id);
        }
        
        // Delete associated images first
        List<CardImage> images = cardImageRepository.findByCard_CardId(id);
        cardImageRepository.deleteAll(images);
        
        // Delete the card
        cardRepository.deleteById(id);
        log.info("Deleted card with id: {}", id);
    }

    // Bulk delete cards (관리자 전용)
    public void bulkDeleteCards(List<Long> cardIds) {
        for (Long id : cardIds) {
            deleteCard(id);
        }
        log.info("Bulk deleted {} cards", cardIds.size());
    }

    // Get card statistics (관리자 전용)
    @Transactional(readOnly = true)
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
    
    // ========== Private Helper Methods ==========
    
    private Specification<Card> buildSpecification(String cardType, String cardColor, String rarity, String searchTerm) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (cardType != null && !cardType.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(
                    criteriaBuilder.treat(root, getCardClassByType(cardType)).type(), 
                    getCardClassByType(cardType)
                ));
            }
            
            if (cardColor != null && !cardColor.trim().isEmpty()) {
                try {
                    Card.CardColor color = Card.CardColor.valueOf(cardColor.toUpperCase());
                    predicates.add(criteriaBuilder.equal(root.get("cardColor"), color));
                } catch (IllegalArgumentException e) {
                    // Invalid color, ignore this filter
                }
            }
            
            if (rarity != null && !rarity.trim().isEmpty()) {
                try {
                    Card.CardRarity rarityEnum = Card.CardRarity.valueOf(rarity.toUpperCase());
                    predicates.add(criteriaBuilder.equal(root.get("rarity"), rarityEnum));
                } catch (IllegalArgumentException e) {
                    // Invalid rarity, ignore this filter
                }
            }
            
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                String pattern = "%" + searchTerm.toLowerCase() + "%";
                Predicate namePredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("cardName")), pattern);
                Predicate descPredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("description")), pattern);
                predicates.add(criteriaBuilder.or(namePredicate, descPredicate));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
    
    private Class<? extends Card> getCardClassByType(String cardType) {
        switch (cardType.toUpperCase()) {
            case "LEADER": return LeaderCard.class;
            case "UNIT": return UnitCard.class;
            case "ITEM": return ItemCard.class;
            case "FIELD": return FieldCard.class;
            case "SPELL": return SpellCard.class;
            default: return Card.class;
        }
    }
    
    private void setCommonFields(Card card, CardCreateDTO dto) {
        card.setCardName(dto.getCardName());
        card.setCardImg(dto.getCardImg());
        card.setDescription(dto.getDescription());
        card.setCardColor(Card.CardColor.valueOf(dto.getCardColor()));
        card.setRarity(Card.CardRarity.valueOf(dto.getRarity()));
        card.setCost(dto.getCost());
        card.setCardNumber(dto.getCardNumber());
        card.setCardType(dto.getCardType()); // ✅ 프론트엔드에서 받은 cardType 설정
    }
}