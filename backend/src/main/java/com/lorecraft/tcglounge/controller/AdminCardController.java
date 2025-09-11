package com.lorecraft.tcglounge.controller;

import com.lorecraft.tcglounge.dto.CardCreateDTO;
import com.lorecraft.tcglounge.dto.CardUpdateDTO;
import com.lorecraft.tcglounge.dto.CardDetailDTO;
import com.lorecraft.tcglounge.entity.*;
import com.lorecraft.tcglounge.service.AdminCardService;
import com.lorecraft.tcglounge.service.CardImageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/cards")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class AdminCardController {

    private final AdminCardService adminCardService;
    private final CardImageService cardImageService;

    public AdminCardController(AdminCardService adminCardService, CardImageService cardImageService) {
        this.adminCardService = adminCardService;
        this.cardImageService = cardImageService;
    }

    // Get all cards with pagination
    @GetMapping
    public ResponseEntity<Page<CardDetailDTO>> getAllCards(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String cardType,
            @RequestParam(required = false) String cardColor,
            @RequestParam(required = false) String rarity,
            @RequestParam(required = false) String searchTerm) {
        
        Page<CardDetailDTO> cards = adminCardService.getAllCards(pageable, cardType, cardColor, rarity, searchTerm);
        return ResponseEntity.ok(cards);
    }

    // Get single card details
    @GetMapping("/{id}")
    public ResponseEntity<CardDetailDTO> getCardById(@PathVariable Long id) {
        CardDetailDTO card = adminCardService.getCardById(id);
        return ResponseEntity.ok(card);
    }

    // Create new card based on type
    @PostMapping
    public ResponseEntity<Map<String, Object>> createCard(@RequestBody CardCreateDTO cardCreateDTO) {
        try {
            Card createdCard = adminCardService.createCard(cardCreateDTO);
            
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

    // Update existing card
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateCard(
            @PathVariable Long id,
            @RequestBody CardUpdateDTO cardUpdateDTO) {
        try {
            Card updatedCard = adminCardService.updateCard(id, cardUpdateDTO);
            
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

    // Delete card
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteCard(@PathVariable Long id) {
        try {
            adminCardService.deleteCard(id);
            
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

    // Upload card image
    @PostMapping("/{id}/images")
    public ResponseEntity<Map<String, Object>> uploadCardImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "category", defaultValue = "main") String category) {
        try {
            CardImage cardImage = cardImageService.uploadImage(id, file, category);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Image uploaded successfully");
            response.put("imageId", cardImage.getImageId());
            response.put("imageUrl", "/api/v1/cards/images/" + cardImage.getImageId());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to upload image: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Delete card image
    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<Map<String, Object>> deleteCardImage(@PathVariable Long imageId) {
        try {
            cardImageService.deleteImage(imageId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Image deleted successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to delete image: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Simple test endpoint
    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> testEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "AdminCardController is working");
        response.put("message", "Controller loaded successfully");
        return ResponseEntity.ok(response);
    }

    // Get card statistics
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getCardStatistics() {
        Map<String, Object> stats = adminCardService.getCardStatistics();
        return ResponseEntity.ok(stats);
    }

    // Bulk operations
    @PostMapping("/bulk-delete")
    public ResponseEntity<Map<String, Object>> bulkDeleteCards(@RequestBody List<Long> cardIds) {
        try {
            adminCardService.bulkDeleteCards(cardIds);
            
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