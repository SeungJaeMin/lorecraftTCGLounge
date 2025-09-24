# Lombok 사용 가이드라인

> **Lorecraft TCG Lounge 프로젝트의 안전한 Lombok 사용을 위한 공식 가이드**

## 📋 목차
1. [핵심 원칙](#핵심-원칙)
2. [lombok.config 설정](#lombokconfig-설정)
3. [허용되는 어노테이션](#허용되는-어노테이션)
4. [금지되는 어노테이션](#금지되는-어노테이션)
5. [DTO 패턴](#dto-패턴)
6. [Entity 패턴](#entity-패턴)
7. [실전 예제](#실전-예제)
8. [체크리스트](#체크리스트)

---

## 핵심 원칙

### ✅ 기본 규칙
1. **보수적 사용**: 검증된 안전한 기능만 사용
2. **명시적 코드**: 암묵적 동작보다 명시적 코드 선호
3. **타입 안전성**: Map 대신 DTO 클래스 사용 필수
4. **영속성 보호**: Entity에는 최소한의 Lombok만 적용

### 🎯 목표
- **개발 효율성**: 보일러플레이트 코드 감소
- **유지보수성**: 명확하고 예측 가능한 코드
- **안정성**: 런타임 오류 방지, 컴파일 타임 검증

---

## lombok.config 설정

프로젝트 루트에 `lombok.config` 파일을 생성하고 다음 내용을 추가:

```properties
# lombok.config
config.stopBubbling = true

# 위험한 기능들 사용 금지 (컴파일 에러 발생)
lombok.data.flagUsage = error
lombok.value.flagUsage = error
lombok.allArgsConstructor.flagUsage = error
lombok.requiredArgsConstructor.flagUsage = error
lombok.nonNull.flagUsage = error
lombok.sneakyThrows.flagUsage = error
lombok.cleanup.flagUsage = error

# 주의가 필요한 기능 (경고만 발생)
lombok.equalsAndHashCode.flagUsage = warning
lombok.synchronized.flagUsage = warning

# 실험적 기능 전체 금지
lombok.experimental.flagUsage = error

# 로거 설정
lombok.log.fieldName = log
lombok.log.fieldIsStatic = true
```

---

## 허용되는 어노테이션

### ✅ @Getter
**용도**: 필드의 getter 메서드 자동 생성
```java
@Getter
public class DeckDTO {
    private Long id;
    private String deckName;
}
```

### ✅ @Setter (DTO에만 제한적 사용)
**용도**: DTO의 setter 메서드 자동 생성
```java
@Getter
@Setter  // DTO에만 사용, Entity에는 사용 금지
public class UserUpdateDTO {
    private String nickname;
    private String email;
}
```

### ✅ @ToString
**용도**: toString() 메서드 자동 생성
```java
@ToString(exclude = "password")  // 민감정보 제외
public class UserDTO {
    private String username;
    private String password;
}
```

### ✅ @NoArgsConstructor
**용도**: 기본 생성자 생성 (JPA, Jackson 등에 필요)
```java
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // Entity용
@NoArgsConstructor  // DTO용
```

### ✅ @Builder (생성자에만)
**용도**: 빌더 패턴 구현
```java
public class DeckCreateDTO {
    private String deckName;
    
    @Builder  // 클래스가 아닌 생성자에 적용
    private DeckCreateDTO(String deckName) {
        this.deckName = deckName;
    }
}
```

### ✅ @Slf4j
**용도**: SLF4J 로거 자동 생성
```java
@Slf4j
@Service
public class DeckService {
    public void createDeck() {
        log.info("Creating new deck");
    }
}
```

---

## 금지되는 어노테이션

### ❌ @Data
**이유**: @EqualsAndHashCode, @RequiredArgsConstructor 포함으로 위험
```java
// 잘못된 예
@Data  // 절대 사용 금지
public class DeckDTO { }

// 올바른 예
@Getter
@Setter
@ToString
public class DeckDTO { }
```

### ❌ @AllArgsConstructor, @RequiredArgsConstructor
**이유**: 필드 순서 변경 시 파라미터 순서도 자동 변경되어 버그 발생
```java
// 위험한 예
@AllArgsConstructor
public class Order {
    private long cancelPrice;  // 순서 변경시
    private long orderPrice;   // 생성자 파라미터도 자동 변경!
}

// 안전한 예
public class Order {
    private long cancelPrice;
    private long orderPrice;
    
    @Builder
    private Order(long cancelPrice, long orderPrice) {
        this.cancelPrice = cancelPrice;
        this.orderPrice = orderPrice;
    }
}
```

### ❌ @EqualsAndHashCode (파라미터 없이 사용 시)
**이유**: Mutable 객체의 hashCode 변경으로 Set/Map에서 문제 발생
```java
// 위험한 예
@EqualsAndHashCode  // 모든 필드 포함
public class DeckDTO { }

// 안전한 예 (꼭 필요한 경우만)
@EqualsAndHashCode(of = {"id"})  // 불변 필드만 명시
public class DeckDTO {
    private final Long id;  // 불변
    private String name;    // 가변
}
```

### ❌ @Value
**이유**: @AllArgsConstructor 포함으로 위험

### ❌ @NonNull
**이유**: 불필요한 branch coverage 증가, Preconditions 사용 권장

---

## DTO 패턴

### 📝 Request DTO
```java
package com.lorecraft.tcglounge.dto.deck;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@ToString
public class DeckCreateRequestDTO {
    
    @NotBlank(message = "덱 이름은 필수입니다")
    @Size(min = 1, max = 50, message = "덱 이름은 1-50자여야 합니다")
    private String deckName;
    
    @Size(max = 200, message = "설명은 200자 이내여야 합니다")
    private String description;
    
    private Boolean isPublic = false;
    
    @Builder
    private DeckCreateRequestDTO(String deckName, String description, Boolean isPublic) {
        this.deckName = deckName;
        this.description = description;
        this.isPublic = isPublic != null ? isPublic : false;
    }
}
```

### 📤 Response DTO
```java
package com.lorecraft.tcglounge.dto.deck;

import lombok.Builder;
import lombok.Getter;
import com.lorecraft.tcglounge.entity.CardDeck;
import java.time.LocalDateTime;

@Getter
public class DeckResponseDTO {
    
    private final Long id;
    private final String deckName;
    private final String description;
    private final Boolean isPublic;
    private final Integer totalCards;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    
    @Builder
    private DeckResponseDTO(Long id, String deckName, String description, 
                          Boolean isPublic, Integer totalCards,
                          LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.deckName = deckName;
        this.description = description;
        this.isPublic = isPublic;
        this.totalCards = totalCards;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Entity -> DTO 변환 메서드
    public static DeckResponseDTO from(CardDeck deck) {
        return DeckResponseDTO.builder()
            .id(deck.getDeckId())
            .deckName(deck.getDeckName())
            .description(deck.getDescription())
            .isPublic(deck.getIsPublic())
            .totalCards(deck.getTotalCards())
            .createdAt(deck.getCreatedAt())
            .updatedAt(deck.getUpdatedAt())
            .build();
    }
}
```

### 🔄 Update DTO
```java
@Getter
@NoArgsConstructor
@ToString
public class DeckUpdateRequestDTO {
    
    @Size(min = 1, max = 50, message = "덱 이름은 1-50자여야 합니다")
    private String deckName;
    
    @Size(max = 200, message = "설명은 200자 이내여야 합니다")
    private String description;
    
    private Boolean isPublic;
    
    @Builder
    private DeckUpdateRequestDTO(String deckName, String description, Boolean isPublic) {
        this.deckName = deckName;
        this.description = description;
        this.isPublic = isPublic;
    }
}
```

---

## Entity 패턴

### 📦 JPA Entity
```java
package com.lorecraft.tcglounge.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "card_deck")
@Getter  // Getter만 사용
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // JPA용 protected 생성자
public class CardDeck {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deck_id")
    private Long deckId;
    
    @Column(name = "deck_name", nullable = false, length = 50)
    private String deckName;
    
    @Column(name = "description", length = 200)
    private String description;
    
    @Column(name = "is_public")
    private Boolean isPublic = false;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 생성자에 @Builder 적용
    @Builder
    private CardDeck(String deckName, String description, Boolean isPublic) {
        this.deckName = deckName;
        this.description = description;
        this.isPublic = isPublic != null ? isPublic : false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // 비즈니스 메서드로 상태 변경 (Setter 대신)
    public void updateDeckInfo(String deckName, String description) {
        if (deckName != null && !deckName.trim().isEmpty()) {
            this.deckName = deckName;
        }
        if (description != null) {
            this.description = description;
        }
        this.updatedAt = LocalDateTime.now();
    }
    
    public void togglePublic() {
        this.isPublic = !this.isPublic;
        this.updatedAt = LocalDateTime.now();
    }
    
    // JPA 콜백
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

---

## 실전 예제

### Controller 개선 예제

#### ❌ Before (Map 사용)
```java
@PostMapping
public ResponseEntity<Map<String, Object>> createDeck(
    @RequestBody Map<String, String> request,
    @CurrentUser Gamer gamer) {
    
    String deckName = request.get("deckName");  // 타입 불안전
    if (deckName == null || deckName.trim().isEmpty()) {
        // 수동 검증 필요
    }
    // ...
}
```

#### ✅ After (DTO 사용)
```java
@PostMapping
public ResponseEntity<DeckResponseDTO> createDeck(
    @Valid @RequestBody DeckCreateRequestDTO request,  // 자동 검증
    @CurrentUser Gamer gamer) {
    
    CardDeck deck = deckService.createDeck(
        gamer,
        request.getDeckName(),  // IDE 자동완성, 타입 안전
        request.getDescription(),
        request.getIsPublic()
    );
    
    return ResponseEntity.ok(DeckResponseDTO.from(deck));
}
```

### Service 계층 예제

```java
@Slf4j
@Service
@Transactional(readOnly = true)
public class DeckService {
    
    private final CardDeckRepository deckRepository;
    
    // 생성자 주입 (수동 작성)
    public DeckService(CardDeckRepository deckRepository) {
        this.deckRepository = deckRepository;
    }
    
    @Transactional
    public CardDeck createDeck(Gamer gamer, String deckName, 
                              String description, Boolean isPublic) {
        log.info("Creating new deck for gamer: {}", gamer.getUid());
        
        // Preconditions로 null 체크 (branch coverage 문제 없음)
        Preconditions.checkNotNull(gamer, "Gamer must not be null");
        Preconditions.checkNotNull(deckName, "Deck name must not be null");
        
        CardDeck deck = CardDeck.builder()
            .deckName(deckName)
            .description(description)
            .isPublic(isPublic)
            .build();
        
        return deckRepository.save(deck);
    }
}
```

---

## 체크리스트

### 🔍 코드 리뷰 체크포인트

#### DTO 클래스
- [ ] `@Data` 사용하지 않았는가?
- [ ] `@AllArgsConstructor` 사용하지 않았는가?
- [ ] `@Builder`를 생성자에만 적용했는가?
- [ ] Validation 어노테이션을 추가했는가?
- [ ] `from()` 또는 `toEntity()` 변환 메서드가 있는가?

#### Entity 클래스
- [ ] `@Getter`만 사용했는가?
- [ ] `@Setter` 대신 비즈니스 메서드를 사용했는가?
- [ ] `@NoArgsConstructor`에 `AccessLevel.PROTECTED`를 설정했는가?
- [ ] `@EqualsAndHashCode`를 사용한다면 불변 필드만 지정했는가?

#### Controller
- [ ] Map 대신 DTO를 사용했는가?
- [ ] `@Valid` 어노테이션을 추가했는가?
- [ ] Response도 DTO로 반환하는가?

### 📊 마이그레이션 우선순위

1. **긴급 (즉시 수정)**
   - Map을 받는 모든 Controller 메서드
   - `@Data` 사용 중인 모든 클래스

2. **높음 (다음 스프린트)**
   - `@AllArgsConstructor` 사용 중인 클래스
   - Entity의 `@Setter` 제거

3. **보통 (점진적 개선)**
   - `@EqualsAndHashCode` 검토 및 수정
   - 모든 DTO에 Validation 추가

---

## 참고 자료

- [Lombok 공식 문서](https://projectlombok.org/features/all)
- [Lombok Configuration](https://projectlombok.org/features/configuration)
- [Effective Java 3rd Edition](https://www.oreilly.com/library/view/effective-java-3rd/9780134686097/)
- [Spring Boot DTO Pattern Best Practices](https://www.baeldung.com/java-dto-pattern)

---

*Last Updated: 2024-09-24*
*Version: 1.0.0*