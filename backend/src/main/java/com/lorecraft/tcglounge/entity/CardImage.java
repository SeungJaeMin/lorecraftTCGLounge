package com.lorecraft.tcglounge.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

@Entity
@Table(name = "card_images")
@EntityListeners(AuditingEntityListener.class)
public class CardImage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long imageId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    @JsonIgnore
    private Card card;
    
    @Column(name = "file_path", nullable = false)
    private String filePath;
    
    @Column(name = "encryption_key", nullable = false)
    private String encryptionKey;
    
    @Column(name = "image_name")
    private String imageName;
    
    @Column(name = "image_type", nullable = false)
    private String imageType; // MIME type: image/jpeg, image/png
    
    @Column(name = "image_size", nullable = false)
    private Long imageSize;
    
    @Column(name = "image_category")
    private String imageCategory; // main, thumbnail, artwork
    
    @Column(name = "width")
    private Integer width;
    
    @Column(name = "height")
    private Integer height;
    
    @CreatedDate
    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;
    
    // Constructors
    public CardImage() {}
    
    public CardImage(Card card, String filePath, String encryptionKey, String imageName, String imageType, Long imageSize) {
        this.card = card;
        this.filePath = filePath;
        this.encryptionKey = encryptionKey;
        this.imageName = imageName;
        this.imageType = imageType;
        this.imageSize = imageSize;
    }
    
    // Getters
    public Long getImageId() { return imageId; }
    @JsonIgnore
    public Card getCard() { return card; }
    public String getFilePath() { return filePath; }
    public String getEncryptionKey() { return encryptionKey; }
    public String getImageName() { return imageName; }
    public String getImageType() { return imageType; }
    public Long getImageSize() { return imageSize; }
    public String getImageCategory() { return imageCategory; }
    public Integer getWidth() { return width; }
    public Integer getHeight() { return height; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    
    // Setters
    public void setImageId(Long imageId) { this.imageId = imageId; }
    public void setCard(Card card) { this.card = card; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public void setEncryptionKey(String encryptionKey) { this.encryptionKey = encryptionKey; }
    public void setImageName(String imageName) { this.imageName = imageName; }
    public void setImageType(String imageType) { this.imageType = imageType; }
    public void setImageSize(Long imageSize) { this.imageSize = imageSize; }
    public void setImageCategory(String imageCategory) { this.imageCategory = imageCategory; }
    public void setWidth(Integer width) { this.width = width; }
    public void setHeight(Integer height) { this.height = height; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}