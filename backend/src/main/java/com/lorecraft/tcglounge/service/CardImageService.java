package com.lorecraft.tcglounge.service;

import com.lorecraft.tcglounge.entity.Card;
import com.lorecraft.tcglounge.entity.CardImage;
import com.lorecraft.tcglounge.repository.CardImageRepository;
import com.lorecraft.tcglounge.repository.CardRepository;
import com.lorecraft.tcglounge.util.ImageEncryptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
public class CardImageService {
    
    @Autowired
    private CardImageRepository cardImageRepository;
    
    @Autowired
    private CardRepository cardRepository;
    
    @Autowired
    private ImageEncryptionUtil encryptionUtil;
    
    @Value("${app.image.storage.path:./images/encrypted}")
    private String imageStoragePath;
    
    public CardImage uploadImage(Long cardId, MultipartFile file, String category) throws Exception {
        
        Optional<Card> cardOpt = cardRepository.findById(cardId);
        if (!cardOpt.isPresent()) {
            throw new RuntimeException("Card not found");
        }
        Card card = cardOpt.get();
        
        // 디렉토리 생성
        Path cardDir = Paths.get(imageStoragePath, "card_" + cardId, category);
        Files.createDirectories(cardDir);
        
        // 암호화키 생성
        SecretKey secretKey = encryptionUtil.generateKey();
        String keyString = encryptionUtil.keyToString(secretKey);
        
        // 파일명 생성
        String fileName = encryptionUtil.generateFileName(cardId, category);
        Path filePath = cardDir.resolve(fileName);
        
        // 이미지 암호화 및 저장
        byte[] originalData = file.getBytes();
        byte[] encryptedData = encryptionUtil.encrypt(originalData, secretKey);
        Files.write(filePath, encryptedData);
        
        // DB에 메타데이터 저장
        CardImage cardImage = new CardImage(
            card,
            filePath.toString(),
            keyString,
            file.getOriginalFilename(),
            file.getContentType(),
            file.getSize()
        );
        cardImage.setImageCategory(category);
        
        return cardImageRepository.save(cardImage);
    }
    
    public byte[] getImageData(Long imageId) throws Exception {
        Optional<CardImage> imageOpt = cardImageRepository.findById(imageId);
        if (!imageOpt.isPresent()) {
            throw new RuntimeException("Image not found");
        }
        
        CardImage cardImage = imageOpt.get();
        Path filePath = Paths.get(cardImage.getFilePath());
        
        if (!Files.exists(filePath)) {
            throw new RuntimeException("Image file not found");
        }
        
        // 파일 읽기 및 복호화
        byte[] encryptedData = Files.readAllBytes(filePath);
        SecretKey secretKey = encryptionUtil.stringToKey(cardImage.getEncryptionKey());
        
        return encryptionUtil.decrypt(encryptedData, secretKey);
    }
    
    public List<CardImage> getImagesByCardId(Long cardId) {
        return cardImageRepository.findByCardCardId(cardId);
    }
    
    public CardImage getImageById(Long imageId) {
        Optional<CardImage> imageOpt = cardImageRepository.findById(imageId);
        if (!imageOpt.isPresent()) {
            throw new RuntimeException("Image not found");
        }
        return imageOpt.get();
    }
    
    public List<CardImage> getImagesByCardIdAndCategory(Long cardId, String category) {
        return cardImageRepository.findByCardCardIdAndImageCategory(cardId, category);
    }
    
    public void deleteImage(Long imageId) throws Exception {
        Optional<CardImage> imageOpt = cardImageRepository.findById(imageId);
        if (!imageOpt.isPresent()) {
            throw new RuntimeException("Image not found");
        }
        
        CardImage cardImage = imageOpt.get();
        Path filePath = Paths.get(cardImage.getFilePath());
        
        // 암호화 파일 삭제
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
        
        // DB 레코드 삭제
        cardImageRepository.delete(cardImage);
    }
}