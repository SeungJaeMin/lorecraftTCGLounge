package com.lorecraft.tcglounge.controller;

import com.lorecraft.tcglounge.entity.Card;
import com.lorecraft.tcglounge.entity.CardImage;
import com.lorecraft.tcglounge.dto.CardDTO;
import com.lorecraft.tcglounge.dto.CardImageDTO;
import com.lorecraft.tcglounge.dto.CardCreateDTO;
import com.lorecraft.tcglounge.dto.CardUpdateDTO;
import com.lorecraft.tcglounge.dto.CardDetailDTO;
import com.lorecraft.tcglounge.service.CardService;
import com.lorecraft.tcglounge.service.CardImageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/cards")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class CardController {

    private final CardService cardService;
    private final CardImageService cardImageService;

    public CardController(CardService cardService, CardImageService cardImageService) {
        this.cardService = cardService;
        this.cardImageService = cardImageService;
    }

    @GetMapping
    public ResponseEntity<List<CardDTO>> getAllCards() {
        // N+1 문제 해결: 배치 로딩 사용
        List<CardDetailDTO> cardDetails = cardService.findAllWithImages();
        List<CardDTO> cardDTOs = cardDetails.stream().map(cardDetail -> {
            // CardDetailDTO를 CardDTO로 변환
            CardDTO cardDTO = new CardDTO();
            cardDTO.setCardId(cardDetail.getCardId());
            cardDTO.setCardName(cardDetail.getCardName());
            cardDTO.setCardImg(cardDetail.getCardImg());
            cardDTO.setDescription(cardDetail.getDescription());
            if (cardDetail.getCardColor() != null) {
                cardDTO.setCardColor(Card.CardColor.valueOf(cardDetail.getCardColor()));
            }
            if (cardDetail.getRarity() != null) {
                cardDTO.setRarity(Card.CardRarity.valueOf(cardDetail.getRarity()));
            }
            cardDTO.setCost(cardDetail.getCost());
            cardDTO.setCardNumber(cardDetail.getCardNumber());
            cardDTO.setCardType(cardDetail.getCardType());
            cardDTO.setCreatedAt(cardDetail.getCreatedAt());
            cardDTO.setUpdatedAt(cardDetail.getUpdatedAt());
            cardDTO.setImages(cardDetail.getImages());

            return cardDTO;
        }).collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(cardDTOs);
    }

    // ========== 관리자 전용 기능들 ==========
    
    // 관리자용 카드 목록 조회 (페이징, 필터링 포함)
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<CardDetailDTO>> getAdminCards(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String cardType,
            @RequestParam(required = false) String cardColor,
            @RequestParam(required = false) String rarity,
            @RequestParam(required = false) String searchTerm) {
        
        Page<CardDetailDTO> cards = cardService.getAllCards(pageable, cardType, cardColor, rarity, searchTerm);
        return ResponseEntity.ok(cards);
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

    // 카드 생성 (관리자 전용)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createCard(@RequestBody CardCreateDTO cardCreateDTO) {
        try {
            Card createdCard = cardService.createCard(cardCreateDTO);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Card created successfully");
            response.put("cardId", createdCard.getCardId());
            response.put("cardName", createdCard.getCardName());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to create card: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 카드 수정 (관리자 전용)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateCard(
            @PathVariable Long id,
            @RequestBody CardUpdateDTO cardUpdateDTO) {
        try {
            Card updatedCard = cardService.updateCard(id, cardUpdateDTO);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Card updated successfully");
            response.put("cardId", updatedCard.getCardId());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to update card: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 카드 삭제 (관리자 전용)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteCard(@PathVariable Long id) {
        try {
            cardService.deleteCard(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Card deleted successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to delete card: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 카드 통계 조회 (관리자 전용)
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getCardStatistics() {
        Map<String, Object> stats = cardService.getCardStatistics();
        return ResponseEntity.ok(stats);
    }

    // 대량 삭제 (관리자 전용)
    @PostMapping("/bulk-delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> bulkDeleteCards(@RequestBody List<Long> cardIds) {
        try {
            cardService.bulkDeleteCards(cardIds);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", cardIds.size() + " cards deleted successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to delete cards: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }

}