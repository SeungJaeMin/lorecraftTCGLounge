package com.lorecraft.tcglounge.service;

import com.lorecraft.tcglounge.dto.CardDto;
import com.lorecraft.tcglounge.dto.CardSearchRequest;
import com.lorecraft.tcglounge.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

// @Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;

    public Page<CardDto> searchCards(CardSearchRequest request, Pageable pageable) {
        // Mock data for development
        List<CardDto> cards = new ArrayList<>();
        
        cards.add(CardDto.builder()
                .id(1L)
                .name("드래곤 나이트")
                .type("유닛")
                .rarity("전설")
                .color("빨강")
                .cost(5)
                .attack(7)
                .defense(5)
                .description("전투 개시: 상대 유닛 1개에 3 데미지를 준다.")
                .imageUrl("https://via.placeholder.com/200x280/ff6b6b/ffffff?text=드래곤+나이트")
                .setCode("SET001")
                .cardNumber("001")
                .build());
        
        cards.add(CardDto.builder()
                .id(2L)
                .name("마법사 견습생")
                .type("유닛")
                .rarity("일반")
                .color("파랑")
                .cost(2)
                .attack(2)
                .defense(3)
                .description("소환 시: 카드 1장을 뽑는다.")
                .imageUrl("https://via.placeholder.com/200x280/4ecdc4/ffffff?text=마법사+견습생")
                .setCode("SET001")
                .cardNumber("002")
                .build());
        
        cards.add(CardDto.builder()
                .id(3L)
                .name("숲의 수호자")
                .type("유닛")
                .rarity("희귀")
                .color("초록")
                .cost(3)
                .attack(3)
                .defense(4)
                .description("다른 아군 유닛들에게 +1/+1을 부여한다.")
                .imageUrl("https://via.placeholder.com/200x280/45b7d1/ffffff?text=숲의+수호자")
                .setCode("SET001")
                .cardNumber("003")
                .build());
        
        // Filter based on search criteria
        if (request.getQuery() != null && !request.getQuery().isEmpty()) {
            cards = cards.stream()
                    .filter(card -> card.getName().toLowerCase().contains(request.getQuery().toLowerCase()))
                    .toList();
        }
        
        if (request.getType() != null && !request.getType().equals("all")) {
            cards = cards.stream()
                    .filter(card -> card.getType().equals(request.getType()))
                    .toList();
        }
        
        if (request.getRarity() != null && !request.getRarity().equals("all")) {
            cards = cards.stream()
                    .filter(card -> card.getRarity().equals(request.getRarity()))
                    .toList();
        }
        
        if (request.getColor() != null && !request.getColor().equals("all")) {
            cards = cards.stream()
                    .filter(card -> card.getColor().equals(request.getColor()))
                    .toList();
        }
        
        return new PageImpl<>(cards, pageable, cards.size());
    }

    public CardDto getCard(Long id) {
        // Mock data
        return CardDto.builder()
                .id(id)
                .name("드래곤 나이트")
                .type("유닛")
                .rarity("전설")
                .color("빨강")
                .cost(5)
                .attack(7)
                .defense(5)
                .description("전투 개시: 상대 유닛 1개에 3 데미지를 준다.")
                .imageUrl("https://via.placeholder.com/200x280/ff6b6b/ffffff?text=드래곤+나이트")
                .setCode("SET001")
                .cardNumber("001")
                .build();
    }

    public List<CardDto> getFeaturedCards() {
        // Mock data for featured cards
        List<CardDto> featuredCards = new ArrayList<>();
        
        featuredCards.add(CardDto.builder()
                .id(1L)
                .name("드래곤 나이트")
                .type("유닛")
                .rarity("전설")
                .color("빨강")
                .cost(5)
                .attack(7)
                .defense(5)
                .description("전투 개시: 상대 유닛 1개에 3 데미지를 준다.")
                .imageUrl("https://via.placeholder.com/200x280/ff6b6b/ffffff?text=드래곤+나이트")
                .setCode("SET001")
                .cardNumber("001")
                .build());
        
        featuredCards.add(CardDto.builder()
                .id(4L)
                .name("천둥 마법사")
                .type("유닛")
                .rarity("전설")
                .color("파랑")
                .cost(6)
                .attack(5)
                .defense(6)
                .description("주문 시전 시: 무작위 적에게 2 데미지를 준다.")
                .imageUrl("https://via.placeholder.com/200x280/6c5ce7/ffffff?text=천둥+마법사")
                .setCode("SET001")
                .cardNumber("004")
                .build());
        
        featuredCards.add(CardDto.builder()
                .id(5L)
                .name("성기사단장")
                .type("유닛")
                .rarity("전설")
                .color("노랑")
                .cost(7)
                .attack(6)
                .defense(8)
                .description("도발. 신성한 방패.")
                .imageUrl("https://via.placeholder.com/200x280/ffd93d/ffffff?text=성기사단장")
                .setCode("SET001")
                .cardNumber("005")
                .build());
        
        return featuredCards;
    }
}