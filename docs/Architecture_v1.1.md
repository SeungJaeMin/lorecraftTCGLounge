# Lorecraft TCG Lounge - Architecture v1.1
## 권한 기반 서비스 통합 패턴 (Permission-Based Service Integration Pattern)

> **업데이트**: 2025-09-12  
> **기준 버전**: v1.0.0  
> **주요 변경**: Controller-Service 레이어 통합 및 권한 기반 분기처리 표준화

---

## 📋 개요

Architecture v1.1에서는 **권한 기반 서비스 통합 패턴**을 도입하여 코드 중복을 제거하고 서비스 재사용성을 극대화합니다.

### 핵심 철학
> **"같은 데이터, 같은 서비스, 다른 권한, 다른 응답"**

---

## 🎯 권한 기반 분기처리 원칙

### 1.1 권한별 접근 제어

#### **일반 사용자 (Guest/User)**
- **접근 범위**: READ-ONLY
- **데이터 제한**: 공개된 정보만
- **응답 형태**: 기본 DTO (CardDTO, ArticleDTO)

#### **관리자 (Admin)**  
- **접근 범위**: FULL-CRUD
- **데이터 제한**: 모든 정보 (비공개, 삭제된 것 포함)
- **응답 형태**: 상세 DTO (CardDetailDTO, ArticleDTO)

### 1.2 Spring Security 활용

```java
// 권한 기반 분기처리
@GetMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")  // 관리자 전용
public ResponseEntity<Page<CardDetailDTO>> getAdminCards() { ... }

@GetMapping
// 권한 없음 (모든 사용자)
public ResponseEntity<List<CardDTO>> getAllCards() { ... }
```

---

## 🔄 서비스 재사용 극대화

### 2.1 성공 패턴: Article

#### ✅ **올바른 구조**
```java
@RestController
@RequestMapping("/v1/articles")
public class ArticleController {
    @Autowired private ArticleService articleService;  // 공통 서비스
    
    // 일반 사용자: 공개된 글만
    @GetMapping
    public ResponseEntity<Page<ArticleDTO>> getPublishedArticles() {
        Page<Article> articles = articleService.findPublishedArticles(pageable);
        return ResponseEntity.ok(articles.map(ArticleDTO::new));
    }
}

@RestController  
@RequestMapping("/v1/admin/articles")
public class AdminArticleController {
    @Autowired private ArticleService articleService;  // 같은 서비스!
    
    // 관리자: 모든 상태의 글
    @GetMapping
    public ResponseEntity<Page<ArticleDTO>> getAllArticles() {
        Page<Article> articles = articleService.findAllArticles(pageable);
        return ResponseEntity.ok(articles.map(ArticleDTO::new));
    }
}
```

#### **장점**
- 하나의 `ArticleService`로 모든 요청 처리
- 같은 Repository, 다른 쿼리 메서드
- 코드 중복 없음
- 유지보수성 향상

### 2.2 개선 대상: Card (Before)

#### ❌ **문제가 있는 구조**
```java
@RestController
@RequestMapping("/v1/cards")
public class CardController {
    @Autowired private CardService cardService;           // 일반용 서비스
    @Autowired private AdminCardService adminCardService; // ❌ 별도 관리자 서비스
    
    // 일반 사용자
    @GetMapping
    public ResponseEntity<List<CardDTO>> getAllCards() {
        List<Card> cards = cardService.findAll();  // CardRepository 사용
        return ResponseEntity.ok(cardDTOs);
    }
    
    // 관리자  
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<CardDetailDTO>> getAdminCards() {
        Page<CardDetailDTO> cards = adminCardService.getAllCards();  // ❌ 같은 CardRepository 사용!
        return ResponseEntity.ok(cards);
    }
}
```

#### **문제점**
- `CardService` ≠ `AdminCardService` (서비스 중복)
- 결국 같은 `CardRepository` 사용
- 로직 중복으로 인한 유지보수 문제
- 코드베이스 복잡성 증가

---

## 🛠 Card 서비스 통합 리팩토링

### 3.1 목표 구조 (After)

#### ✅ **개선된 구조**
```java
@RestController
@RequestMapping("/v1/cards") 
public class CardController {
    @Autowired private CardService cardService;  // ✅ 하나의 통합 서비스
    
    // 일반 사용자: 기본 정보만
    @GetMapping
    public ResponseEntity<List<CardDTO>> getAllCards() {
        List<Card> cards = cardService.findAll();
        List<CardDTO> cardDTOs = cards.stream()
            .map(card -> new CardDTO(card, card.getCardImages()))
            .collect(Collectors.toList());
        return ResponseEntity.ok(cardDTOs);
    }
    
    // 관리자: 상세 정보 + 페이징
    @GetMapping("/admin")  
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<CardDetailDTO>> getAdminCards(Pageable pageable) {
        Page<Card> cards = cardService.findAllWithDetails(pageable);  // ✅ 같은 서비스, 다른 메서드
        return ResponseEntity.ok(cards.map(CardDetailDTO::new));
    }
    
    // 관리자: 생성/수정/삭제 (CRUD)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createCard(@RequestBody CardCreateDTO dto) {
        Card created = cardService.createCard(dto);  // ✅ 통합된 서비스 사용
        return ResponseEntity.ok(response);
    }
}
```

### 3.2 CardService 통합

#### **Before: 분리된 서비스**
```java
// CardService.java
@Service
public class CardService {
    private final CardRepository cardRepository;
    
    public List<Card> findAll() { ... }
    public Optional<Card> findById(Long id) { ... }
}

// AdminCardService.java  
@Service
public class AdminCardService {
    private final CardRepository cardRepository;        // ❌ 같은 Repository
    private final LeaderCardRepository leaderRepo;     // ❌ 중복 의존성  
    private final UnitCardRepository unitRepo;         // ❌ 중복 의존성
    
    public Page<CardDetailDTO> getAllCards(Pageable p) { ... }
    public Card createCard(CardCreateDTO dto) { ... }
}
```

#### **After: 통합된 서비스**
```java
// CardService.java (통합)
@Service
@Transactional
public class CardService {
    private final CardRepository cardRepository;
    private final LeaderCardRepository leaderCardRepository;
    private final UnitCardRepository unitCardRepository;
    private final ItemCardRepository itemCardRepository;
    private final FieldCardRepository fieldCardRepository;  
    private final SpellCardRepository spellCardRepository;
    private final CardImageRepository cardImageRepository;
    
    // ===== 공통 조회 메서드 =====
    public List<Card> findAll() { 
        List<Card> cards = cardRepository.findAll();
        cards.forEach(card -> card.getCardImages().size()); // batch loading
        return cards;
    }
    
    public Page<Card> findAllWithDetails(Pageable pageable) {
        Page<Card> cards = cardRepository.findAll(pageable);  
        cards.getContent().forEach(card -> card.getCardImages().size());
        return cards;
    }
    
    // ===== 관리자 전용 CUD 메서드 =====
    public Card createCard(CardCreateDTO dto) { ... }
    public Card updateCard(Long id, CardUpdateDTO dto) { ... }
    public void deleteCard(Long id) { ... }
    public void bulkDeleteCards(List<Long> cardIds) { ... }
    public Map<String, Object> getCardStatistics() { ... }
}
```

---

## 📊 리팩토링 효과

### 4.1 Before vs After 비교

| 항목 | Before (v1.0) | After (v1.1) | 개선효과 |
|------|---------------|-------------|---------|
| **서비스 클래스** | CardService + AdminCardService | CardService (통합) | 50% 감소 |
| **코드 중복** | Repository 중복 의존 | 단일 의존성 | 중복 제거 |
| **유지보수성** | 두 곳 수정 필요 | 한 곳만 수정 | 2배 향상 |
| **테스트 복잡도** | 별도 테스트 필요 | 통합 테스트 | 단순화 |
| **가독성** | 로직 분산 | 로직 집중화 | 향상 |

### 4.2 성능 개선
- **N+1 쿼리 방지**: batch_fetch_size 설정으로 일관된 최적화
- **중복 쿼리 제거**: 같은 데이터에 대한 별도 조회 로직 통합
- **메모리 효율성**: 중복 서비스 빈 제거

---

## 🏗 구현 가이드라인

### 5.1 리팩토링 단계

#### **Step 1: AdminCardService 분석**
1. AdminCardService의 모든 메서드 목록 작성
2. CardService와 중복되는 기능 식별
3. 관리자 전용 기능과 공통 기능 분류

#### **Step 2: CardService 확장**  
1. AdminCardService의 관리자 전용 메서드를 CardService로 이동
2. 메서드명 표준화 (`findAll()` vs `findAllWithDetails()`)
3. 트랜잭션 어노테이션 추가

#### **Step 3: Controller 통합**
1. CardController에서 AdminCardService 의존성 제거
2. 모든 매핑을 CardService 사용으로 변경
3. 권한 기반 분기 유지

#### **Step 4: 테스트 및 검증**
1. 기존 기능 동작 확인
2. 권한 기반 접근제어 테스트
3. 성능 개선 확인

### 5.2 주의사항

#### **경로 매핑 순서**
```java
// ✅ 올바른 순서 (구체적 → 일반적)
@GetMapping("/admin")     // 1순위: 구체적 경로
@GetMapping("/statistics") // 2순위: 구체적 경로  
@GetMapping("/{id}")      // 3순위: 변수 경로
```

#### **DTO 응답 차별화 유지**
- 일반 사용자: `CardDTO` (기본 정보)
- 관리자: `CardDetailDTO` (상세 정보 + 메타데이터)

---

## 🔮 확장 계획

### 6.1 다른 도메인 적용
- **User/AdminUser** 통합 검토
- **Tag 관리** 시스템 구축 시 동일 패턴 적용
- **Media/File** 관리 도메인 확장

### 6.2 고급 패턴 도입
- **Strategy Pattern**: 권한별 비즈니스 로직 분기
- **Factory Pattern**: DTO 변환 로직 통합
- **Specification Pattern**: 동적 쿼리 구성

---

## 📚 참고자료

- **Query Optimization Guide**: N+1 쿼리 해결 및 batch_fetch_size 설정
- **Architecture v1.0**: 기본 레이어드 아키텍처 구조
- **ERD v0.3**: 데이터베이스 스키마 설계

---

*이 문서는 지속적으로 업데이트되며, 실제 구현 결과를 반영하여 v1.2로 발전됩니다.*