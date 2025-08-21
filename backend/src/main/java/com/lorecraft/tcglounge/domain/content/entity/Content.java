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
@Table(name = "contents")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "content", columnDefinition = "LONGTEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false)
    private ContentType contentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ContentStatus status = ContentStatus.DRAFT;

    @Column(name = "summary", length = 500)
    private String summary;

    @Column(name = "tags", length = 255)
    private String tags;

    @Column(name = "featured_image_url", length = 255)
    private String featuredImageUrl;

    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;

    @Column(name = "is_pinned", nullable = false)
    private Boolean isPinned = false;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Admin author;

    // 비즈니스 메서드
    public void publish() {
        if (status == ContentStatus.DRAFT || status == ContentStatus.SCHEDULED) {
            this.status = ContentStatus.PUBLISHED;
            this.publishedAt = LocalDateTime.now();
        }
    }

    public void unpublish() {
        if (status == ContentStatus.PUBLISHED) {
            this.status = ContentStatus.DRAFT;
        }
    }

    public void archive() {
        this.status = ContentStatus.ARCHIVED;
    }

    public void schedulePublish(LocalDateTime publishTime) {
        this.status = ContentStatus.SCHEDULED;
        this.publishedAt = publishTime;
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    public void pin() {
        this.isPinned = true;
    }

    public void unpin() {
        this.isPinned = false;
    }

    public boolean isPublished() {
        return status == ContentStatus.PUBLISHED;
    }

    public boolean isDraft() {
        return status == ContentStatus.DRAFT;
    }

    public boolean isScheduled() {
        return status == ContentStatus.SCHEDULED;
    }

    public boolean shouldBePublished() {
        return isScheduled() && publishedAt != null && LocalDateTime.now().isAfter(publishedAt);
    }

    public boolean hasImage() {
        return featuredImageUrl != null && !featuredImageUrl.trim().isEmpty();
    }

    public boolean hasTags() {
        return tags != null && !tags.trim().isEmpty();
    }

    public String[] getTagsArray() {
        if (!hasTags()) return new String[0];
        return tags.split(",");
    }

    public void setTagsFromArray(String[] tagArray) {
        if (tagArray != null && tagArray.length > 0) {
            this.tags = String.join(",", tagArray);
        } else {
            this.tags = null;
        }
    }

    // Enum 정의
    public enum ContentType {
        NEWS("뉴스"),
        ANNOUNCEMENT("공지사항"),
        GUIDE("가이드"),
        RULE("룰북"),
        EVENT("이벤트"),
        PRODUCT_INFO("제품정보"),
        FAQ("자주묻는질문"),
        BLOG("블로그");

        private final String koreanName;

        ContentType(String koreanName) {
            this.koreanName = koreanName;
        }

        public String getKoreanName() {
            return koreanName;
        }
    }

    public enum ContentStatus {
        DRAFT("임시저장"),
        SCHEDULED("예약발행"),
        PUBLISHED("발행됨"),
        ARCHIVED("보관됨");

        private final String koreanName;

        ContentStatus(String koreanName) {
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