package com.lorecraft.tcglounge.domain.content.entity;

import com.lorecraft.tcglounge.domain.user.entity.Admin;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "rulebooks")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Rulebook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "version", nullable = false, length = 20)
    private String version;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "file_url", nullable = false, length = 255)
    private String fileUrl;

    @Column(name = "file_name", nullable = false, length = 100)
    private String fileName;

    @Column(name = "file_size")
    private Long fileSize;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_type", nullable = false)
    private FileType fileType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RulebookStatus status = RulebookStatus.DRAFT;

    @Column(name = "effective_date")
    private LocalDateTime effectiveDate;

    @Column(name = "download_count", nullable = false)
    private Long downloadCount = 0L;

    @Column(name = "is_latest", nullable = false)
    private Boolean isLatest = false;

    @Column(name = "change_notes", columnDefinition = "TEXT")
    private String changeNotes;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by", nullable = false)
    private Admin uploadedBy;

    // 비즈니스 메서드
    public void publish() {
        this.status = RulebookStatus.PUBLISHED;
        if (effectiveDate == null) {
            this.effectiveDate = LocalDateTime.now();
        }
    }

    public void archive() {
        this.status = RulebookStatus.ARCHIVED;
        this.isLatest = false;
    }

    public void markAsLatest() {
        this.isLatest = true;
    }

    public void unmarkAsLatest() {
        this.isLatest = false;
    }

    public void incrementDownloadCount() {
        this.downloadCount++;
    }

    public boolean isPublished() {
        return status == RulebookStatus.PUBLISHED;
    }

    public boolean isEffective() {
        return isPublished() && 
               effectiveDate != null && 
               LocalDateTime.now().isAfter(effectiveDate);
    }

    public boolean isPdf() {
        return fileType == FileType.PDF;
    }

    public String getFormattedFileSize() {
        if (fileSize == null) return "Unknown";
        
        if (fileSize < 1024) {
            return fileSize + " B";
        } else if (fileSize < 1024 * 1024) {
            return String.format("%.1f KB", fileSize / 1024.0);
        } else {
            return String.format("%.1f MB", fileSize / (1024.0 * 1024.0));
        }
    }

    public String getVersionedTitle() {
        return title + " v" + version;
    }

    // Enum 정의
    public enum FileType {
        PDF("PDF"),
        DOC("DOC"),
        DOCX("DOCX"),
        HTML("HTML");

        private final String displayName;

        FileType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum RulebookStatus {
        DRAFT("임시저장"),
        PUBLISHED("발행됨"),
        ARCHIVED("보관됨");

        private final String koreanName;

        RulebookStatus(String koreanName) {
            this.koreanName = koreanName;
        }

        public String getKoreanName() {
            return koreanName;
        }

        public boolean isVisible() {
            return this == PUBLISHED;
        }
    }
}