package com.lorecraft.tcglounge.dto;

import com.lorecraft.tcglounge.entity.CardImage;
import java.time.LocalDateTime;

public class CardImageDTO {
    
    private Long imageId;
    private Long cardId;
    private String imageName;
    private String imageType;
    private Long imageSize;
    private String imageCategory;
    private Integer width;
    private Integer height;
    private LocalDateTime uploadedAt;
    
    public CardImageDTO() {}
    
    public CardImageDTO(CardImage cardImage) {
        this.imageId = cardImage.getImageId();
        this.cardId = cardImage.getCard().getCardId();
        this.imageName = cardImage.getImageName();
        this.imageType = cardImage.getImageType();
        this.imageSize = cardImage.getImageSize();
        this.imageCategory = cardImage.getImageCategory();
        this.width = cardImage.getWidth();
        this.height = cardImage.getHeight();
        this.uploadedAt = cardImage.getUploadedAt();
    }
    
    // Getters
    public Long getImageId() { return imageId; }
    public Long getCardId() { return cardId; }
    public String getImageName() { return imageName; }
    public String getImageType() { return imageType; }
    public Long getImageSize() { return imageSize; }
    public String getImageCategory() { return imageCategory; }
    public Integer getWidth() { return width; }
    public Integer getHeight() { return height; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    
    // Setters
    public void setImageId(Long imageId) { this.imageId = imageId; }
    public void setCardId(Long cardId) { this.cardId = cardId; }
    public void setImageName(String imageName) { this.imageName = imageName; }
    public void setImageType(String imageType) { this.imageType = imageType; }
    public void setImageSize(Long imageSize) { this.imageSize = imageSize; }
    public void setImageCategory(String imageCategory) { this.imageCategory = imageCategory; }
    public void setWidth(Integer width) { this.width = width; }
    public void setHeight(Integer height) { this.height = height; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}