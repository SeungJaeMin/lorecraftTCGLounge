# 데이터베이스 스키마 문서

## 주요 테이블 구조

### 1. users (사용자)
```sql
CREATE TABLE `users` (
  `user_type` varchar(31) NOT NULL,
  `uid` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `nickname` varchar(255) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `register_date` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `userid` varchar(255) NOT NULL,
  PRIMARY KEY (`uid`),
  UNIQUE KEY `UK_6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UK_jyjiwnaabof8kpd0gclhcj2lh` (`userid`)
) ENGINE=InnoDB AUTO_INCREMENT=133 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
```

### 2. cards (카드)
```sql
CREATE TABLE `cards` (
  `card_id` bigint NOT NULL AUTO_INCREMENT,
  `card_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `card_img` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `card_color` enum('RED','BLUE','GREEN','YELLOW','BLACK','COLORLESS') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `rarity` enum('COMMON','RARE','SUPER_RARE','ULTRA_RARE','SECRET_RARE','LEGENDARY') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cost` int DEFAULT NULL,
  `card_number` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `card_type` varchar(31) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`card_id`),
  KEY `idx_card_type` (`card_type`),
  KEY `idx_card_name` (`card_name`),
  KEY `idx_card_color` (`card_color`),
  KEY `idx_rarity` (`rarity`),
  KEY `idx_cost` (`cost`)
) ENGINE=InnoDB AUTO_INCREMENT=94 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
```

### 3. card_decks (카드덱)
*Note: 3번 항목이 cards 테이블로 잘못 기재된 것 같습니다. 실제 card_decks 스키마가 필요합니다.*

예상 구조:
```sql
CREATE TABLE `card_decks` (
  `deck_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `deck_name` varchar(255) NOT NULL,
  `description` text,
  `deck_type` varchar(50),
  `leader_card_id` bigint,
  `is_public` bit(1) DEFAULT 0,
  `is_tournament_legal` bit(1) DEFAULT 0,
  `total_cards` int DEFAULT 0,
  `deck_code` varchar(255) UNIQUE,
  `likes_count` int DEFAULT 0,
  `views_count` int DEFAULT 0,
  `created_at` timestamp DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`deck_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_deck_name` (`deck_name`),
  KEY `idx_is_public` (`is_public`),
  FOREIGN KEY (`user_id`) REFERENCES `users` (`uid`) ON DELETE CASCADE,
  FOREIGN KEY (`leader_card_id`) REFERENCES `cards` (`card_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
```

### 4. deck_details (덱 카드 상세)
```sql
CREATE TABLE `deck_details` (
  `detail_id` bigint NOT NULL AUTO_INCREMENT,
  `deck_id` bigint NOT NULL,
  `card_id` bigint NOT NULL,
  `quantity` int NOT NULL DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `is_sideboard` bit(1) DEFAULT NULL,
  `order_index` int DEFAULT NULL,
  PRIMARY KEY (`detail_id`),
  UNIQUE KEY `uk_deck_card` (`deck_id`,`card_id`),
  KEY `idx_deck_id` (`deck_id`),
  KEY `idx_card_id` (`card_id`),
  CONSTRAINT `deck_details_ibfk_1` FOREIGN KEY (`deck_id`) REFERENCES `card_decks` (`deck_id`) ON DELETE CASCADE,
  CONSTRAINT `deck_details_ibfk_2` FOREIGN KEY (`card_id`) REFERENCES `cards` (`card_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
```

## 덱 서비스 핵심 기능

### 1. 덱 생성하기
- **플로우**: 사용자 덱 생성 버튼 → 덱 에디터 페이지 → 카드 검색/추가 → 저장
- **데이터**: 덱 정보(card_decks) + 덱 디테일(deck_details) 동시 처리

### 2. 덱 수정하기
- **플로우**: 덱 아이콘 클릭 → 기존 덱 로드 → 카드 수정 → 저장
- **데이터**: 기존 덱 정보 + 덱 디테일 조회 → 수정 → 저장

### 3. 덱 삭제하기
- **플로우**: 삭제 요청 → 덱 삭제 (CASCADE로 deck_details도 자동 삭제)

### 4. 덱 불러오기
- **플로우**: 덱 ID로 조회 → 덱 정보 + 카드 리스트 반환

### 5. 덱 검색하기
- **플로우**: 덱 ID 입력 → 해당 덱 정보 + 카드 리스트 표시

## 관계 설정

- `users` 1:N `card_decks` (한 사용자는 여러 덱 소유)
- `card_decks` 1:N `deck_details` (한 덱은 여러 카드 포함)
- `cards` 1:N `deck_details` (한 카드는 여러 덱에서 사용 가능)
- `cards` 1:N `card_decks` (리더 카드 관계)