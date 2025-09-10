package com.lorecraft.tcglounge.controller;

import com.lorecraft.tcglounge.entity.Card;
import com.lorecraft.tcglounge.entity.CardImage;
import com.lorecraft.tcglounge.dto.CardDTO;
import com.lorecraft.tcglounge.dto.CardImageDTO;
import com.lorecraft.tcglounge.service.CardService;
import com.lorecraft.tcglounge.service.CardImageService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/cards")
@CrossOrigin(origins = "http://localhost:3000")
public class CardController {

    private final CardService cardService;
    private final CardImageService cardImageService;

    public CardController(CardService cardService, CardImageService cardImageService) {
        this.cardService = cardService;
        this.cardImageService = cardImageService;
    }

    @GetMapping
    public ResponseEntity<List<CardDTO>> getAllCards() {
        List<Card> cards = cardService.findAll();
        List<CardDTO> cardDTOs = cards.stream().map(card -> {
            List<CardImage> images = cardImageService.getImagesByCardId(card.getCardId());
            List<CardImageDTO> imageDTOs = images.stream()
                .map(CardImageDTO::new)
                .collect(java.util.stream.Collectors.toList());
            return new CardDTO(card, imageDTOs);
        }).collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(cardDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardDTO> getCard(@PathVariable Long id) {
        return cardService.findById(id)
            .map(card -> {
                List<CardImage> images = cardImageService.getImagesByCardId(card.getCardId());
                List<CardImageDTO> imageDTOs = images.stream()
                    .map(CardImageDTO::new)
                    .collect(java.util.stream.Collectors.toList());
                return ResponseEntity.ok(new CardDTO(card, imageDTOs));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<CardDTO>> searchCards(@RequestParam String name) {
        List<Card> cards = cardService.searchByName(name);
        List<CardDTO> cardDTOs = cards.stream().map(card -> {
            List<CardImage> images = cardImageService.getImagesByCardId(card.getCardId());
            List<CardImageDTO> imageDTOs = images.stream()
                .map(CardImageDTO::new)
                .collect(java.util.stream.Collectors.toList());
            return new CardDTO(card, imageDTOs);
        }).collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(cardDTOs);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createCard(@RequestBody Map<String, Object> request) {
        try {
            String cardName = (String) request.get("cardName");
            String colorStr = (String) request.get("cardColor");
            String rarityStr = (String) request.get("rarity");
            Integer cost = (Integer) request.get("cost");

            Card.CardColor cardColor = Card.CardColor.valueOf(colorStr);
            Card.CardRarity rarity = Card.CardRarity.valueOf(rarityStr);

            Card card = cardService.createCard(cardName, cardColor, rarity, cost);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Card created successfully");
            response.put("cardId", card.getCardId());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/init-sample-data")
    public ResponseEntity<Map<String, Object>> initSampleData() {
        try {
            // 샘플 카드 데이터 생성
            cardService.createCard("화염 드래곤", Card.CardColor.RED, Card.CardRarity.LEGENDARY, 8);
            cardService.createCard("빙결의 마법사", Card.CardColor.BLUE, Card.CardRarity.RARE, 5);
            cardService.createCard("번개 폭풍", Card.CardColor.YELLOW, Card.CardRarity.COMMON, 3);
            cardService.createCard("치유의 성수", Card.CardColor.COLORLESS, Card.CardRarity.COMMON, 2);
            cardService.createCard("어둠의 검사", Card.CardColor.BLACK, Card.CardRarity.RARE, 4);
            cardService.createCard("자연의 수호자", Card.CardColor.GREEN, Card.CardRarity.SUPER_RARE, 6);
            cardService.createCard("신성한 기사", Card.CardColor.COLORLESS, Card.CardRarity.ULTRA_RARE, 7);
            cardService.createCard("고대의 정령", Card.CardColor.GREEN, Card.CardRarity.SECRET_RARE, 9);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Sample data initialized successfully");
            response.put("cardsCreated", 8);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    // 이미지 업로드
    @PostMapping("/{cardId}/images")
    public ResponseEntity<Map<String, Object>> uploadImage(
            @PathVariable Long cardId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "category", defaultValue = "main") String category) {
        try {
            CardImage cardImage = cardImageService.uploadImage(cardId, file, category);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Image uploaded successfully");
            response.put("imageId", cardImage.getImageId());
            response.put("imageName", cardImage.getImageName());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    // 이미지 다운로드
    @GetMapping("/images/{imageId}")
    public ResponseEntity<byte[]> downloadImage(@PathVariable Long imageId) {
        try {
            byte[] imageData = cardImageService.getImageData(imageId);
            
            // 이미지 메타데이터 가져오기 - imageId로 직접 찾기
            CardImage cardImage = cardImageService.getImageById(imageId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(cardImage.getImageType()));
            headers.setContentLength(imageData.length);
            headers.set("Content-Disposition", "inline; filename=\"" + cardImage.getImageName() + "\"");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(imageData);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 카드의 모든 이미지 조회
    @GetMapping("/{cardId}/images")
    public ResponseEntity<List<CardImageDTO>> getCardImages(@PathVariable Long cardId) {
        try {
            List<CardImage> images = cardImageService.getImagesByCardId(cardId);
            List<CardImageDTO> imageDTOs = images.stream()
                .map(CardImageDTO::new)
                .collect(java.util.stream.Collectors.toList());
            return ResponseEntity.ok(imageDTOs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 이미지 삭제
    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<Map<String, Object>> deleteImage(@PathVariable Long imageId) {
        try {
            cardImageService.deleteImage(imageId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Image deleted successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }
}