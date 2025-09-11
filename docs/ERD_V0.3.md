# [TCG_Lounge] ERD_V0.3

상태: 완료
변경사항: 카드 정보 간소화, 오프라인 대회 예약 시스템 강화

**Lorecraft TCG Lounge - 간소화된 엔티티 구조**

## 📊 User Domain (사용자 도메인)

### 👤 User (기본 사용자)

```
users
├── uid (PK, auto_increment)
├── userid (unique, not null)
├── password (not null)
├── nickname
├── email (unique, not null)
├── phone_number
├── register_date (auto)
├── updated_at (auto)
├── is_active (default: true)
└── user_type (discriminator)
```

### 🎮 Gamer (게이머) - extends User

```
gamers
├── user_id (PK, FK)
├── total_wins (default: 0)
├── total_losses (default: 0)
├── total_draws (default: 0)
├── usable_point // 달란트 서비스 포인트
├── used_point   // 사용한 포인트
├── current_rating (default: 1000)
└── highest_rating (default: 1000)
```

### 🏪 StoreOwner (점주) - extends User

```
store_owners
├── user_id (PK, FK)
├── store_name (not null)
├── store_location (not null)
├── store_address (not null)
├── store_zipcode
├── contact_number
├── business_hours (TEXT) // 영업시간
├── available_seats (default: 0) // 대회 가능 좌석수
└── is_verified (default: false) // 가입승인
```

### Admin (관리자) - extends User

```
admins
├── user_id (PK, FK)
├── admin_level (default: 1)
├── employee_id (unique)
├── can_manage_users (default: false)
├── can_manage_competitions (default: false)
├── can_manage_content (default: false)
└── can_manage_system (default: false)
```

---

## 🃏 Card Domain (카드 도메인)

### 🎴 Card (기본 카드)

```
cards
├── card_id (PK, auto_increment)
├── card_name (not null)
├── card_img
├── description (TEXT)
├── card_color (enum: RED, BLUE, GREEN, YELLOW, BLACK, COLORLESS)
├── rarity (enum: COMMON, RARE, SUPER_RARE, ULTRA_RARE, SECRET_RARE, LEGENDARY)
├── cost
├── card_number
├── card_type (discriminator) // LEADER, UNIT, ITEM, FIELD, SPELL
├── created_at (auto)
└── updated_at (auto)
```

### 👑 Leader (리더) - extends Card

```
leaders (Single Table Inheritance)
├── card_id (PK, FK)
├── leader_skill (TEXT)
├── is_awakened (default: false)
├── burst_slot1 (1~3, not null)
├── burst_slot2 (1~3, not null)
└── burst_slot3 (1~3, not null)
```

### ⚔️ Unit (유닛) - extends Card

```
units (Single Table Inheritance)
├── card_id (PK, FK)
├── power (not null)
└── burst_value (1~3, not null)
```

### 🛡️ Item (아이템) - extends Card

```
items (Single Table Inheritance)
├── card_id (PK, FK)
├── effect (TEXT)
├── activation_condition (TEXT)
├── is_consumable (default: true)
└── burst_value (1~3, not null)
```

### 🌍 Field (필드) - extends Card

```
fields (Single Table Inheritance)
├── card_id (PK, FK)
├── field_effect (TEXT)
├── affected_colors
├── affected_types
└── burst_value (1~3, not null)
```

### 🪄 Spell (주문) - extends Card

```
spells (Single Table Inheritance)
├── card_id (PK, FK)
├── spell_effect (TEXT)
├── target_type
└── burst_value (1~3, not null)
```

### 📦 CardDeck (덱 프리셋)

```
card_decks
├── id (PK, auto_increment)
├── gamer_id (FK, not null)
├── deck_name (not null)
├── description (TEXT)
├── deck_type (enum: STANDARD, EXTENDED, UNLIMITED, CUSTOM)
├── leader_card_id (FK) // 리더 카드
├── is_public (default: false)
├── is_tournament_legal (default: false) // 대회 사용 가능 여부
├── total_cards (default: 0)
├── deck_code (unique) // 공유 코드
├── likes_count (default: 0) // 좋아요 수
├── views_count (default: 0) // 조회수
├── created_at (auto)
└── updated_at (auto)
```

### 🔗 DeckDetail (덱-카드 연결)

```
deck_details
├── deck_id (FK, PK)
├── card_id (FK, PK)
├── quantity (not null)
├── is_sideboard (default: false) // 사이드보드 여부
└── order_index // 정렬 순서
```

### 🌐 CardTranslation (카드 다국어)

```
card_translations
├── card_id (FK, PK)
├── language_code (PK) // 'KR', 'EN', 'JP'
├── card_name_translated
├── description_translated
├── effect_translated // for items/spells/fields
└── leader_skill_translated // for leaders
```

### 🖼️ CardImage (카드 이미지)

```
card_images
├── image_id (PK, auto_increment)
├── card_id (FK, not null)
├── image_type (enum: NORMAL, FOIL, PROMO, ALTERNATE_ART)
├── image_url (not null)
├── resolution (enum: THUMB, MEDIUM, LARGE)
├── is_primary (default: false)
└── uploaded_at (auto)
```

### 📚 CardSet (카드 세트/확장팩)

```
card_sets
├── set_id (PK, auto_increment)
├── set_name (not null)
├── set_code (unique, not null) // "BASE001", "EXP002" 등
├── release_date
├── total_cards
├── set_type (enum: BASE, EXPANSION, PROMO, SPECIAL)
├── description (TEXT)
├── is_legal_standard (default: true)
├── is_legal_extended (default: true)
├── created_at (auto)
└── updated_at (auto)
```

### 🔗 CardSetContent (카드-세트 N:M 연결)

```
card_set_contents
├── card_id (FK, PK)
├── set_id (FK, PK)
├── card_number_in_set // "001/200" 형태
├── rarity_in_set // 세트별로 레어도가 다를 수 있음
├── is_original_print (default: false) // 최초 출시 세트인지
├── print_run (default: 1) // 재록 횟수
└── special_edition (enum: NORMAL, FOIL, ALTERNATE_ART)
```

### ⚖️ DeckRule (덱 빌딩 규칙)

```
deck_rules
├── rule_id (PK, auto_increment)
├── format_type (enum: STANDARD, EXTENDED, UNLIMITED, SEALED, DRAFT)
├── min_deck_size (default: 40)
├── max_deck_size (default: 60)
├── max_same_card (default: 3) // 동일 카드 제한
├── is_active (default: true)
├── effective_date
├── created_at (auto)
└── updated_at (auto)
```

### 🚫 CardRestriction (카드 제한)

```
card_restrictions
├── restriction_id (PK, auto_increment)
├── card_id (FK, not null)
├── format_type (enum: STANDARD, EXTENDED, UNLIMITED)
├── restriction_type (enum: BANNED, LIMITED, SEMI_LIMITED)
├── max_allowed_copies (default: 0) // BANNED=0, LIMITED=1, SEMI_LIMITED=2
├── restriction_reason (TEXT)
├── effective_date
├── is_active (default: true)
├── created_at (auto)
└── updated_at (auto)
```

---

## 🏆 Competition Domain (오프라인 대회 도메인)

### 🏟️ Competition (오프라인 대회)

```
competitions
├── cid (PK, auto_increment)
├── host_store_id (FK) // 주최 매장
├── name (not null)
├── type (enum: SWISS_ROUND, SINGLE_ELIMINATION, DOUBLE_ELIMINATION, ROUND_ROBIN, CUSTOM)
├── description (TEXT)
├── event_date (not null) // 대회 날짜
├── start_time (not null) // 시작 시간
├── expected_duration // 예상 소요시간 (hours)
├── check_in_start_time // 체크인 시작
├── check_in_end_time // 체크인 마감
├── enrollment_start (not null)
├── enrollment_end (not null)
├── max_participants (not null)
├── min_participants (default: 8) // 최소 참가자
├── current_participants (default: 0)
├── entry_fee (default: 0)
├── prize_description (TEXT) // 상품 설명
├── format_type (enum: STANDARD, EXTENDED, UNLIMITED, SEALED, DRAFT)
├── deck_submission_required (default: true)
├── deck_submission_deadline
├── location_detail (TEXT) // 매장 내 위치
├── parking_info (TEXT) // 주차 정보
├── status (enum: PLANNED, OPEN_FOR_REGISTRATION, REGISTRATION_CLOSED, CHECK_IN, IN_PROGRESS, COMPLETED, CANCELLED)
├── cancellation_reason (TEXT)
├── created_at (auto)
└── updated_at (auto)
```

### 📝 Enrollment (참가 예약)

```
enrollments
├── id (PK, auto_increment)
├── competition_id (FK, not null)
├── gamer_id (FK, not null)
├── enrollment_number // 예약 번호
├── selected_deck_id (FK) // 사용할 덱
├── payment_status (enum: PENDING, PAID, REFUNDED)
├── payment_method (enum: CASH, CARD, POINT, FREE)
├── paid_amount
├── check_in_status (enum: NOT_CHECKED, CHECKED_IN, NO_SHOW, LATE)
├── check_in_time
├── check_in_method (enum: QR_CODE, MANUAL, SELF)
├── seat_number // 좌석 번호
├── notes (TEXT)
├── emergency_contact // 비상연락처
├── transportation (enum: CAR, PUBLIC, WALK, OTHER) // 교통수단
├── withdrawal_reason (TEXT) // 취소 사유
├── enrolled_at (auto)
├── updated_at (auto)
└── UNIQUE(competition_id, gamer_id)
```

### 📱 CheckInQR (체크인 QR코드)

```
check_in_qrs
├── id (PK, auto_increment)
├── enrollment_id (FK, not null)
├── qr_code (unique, not null)
├── qr_image_url
├── is_used (default: false)
├── used_at
├── expires_at
├── created_at (auto)
└── updated_at (auto)
```

### ⚔️ Match (오프라인 매치)

```
matches
├── match_id (PK, auto_increment)
├── competition_id (FK, not null)
├── player1_enrollment_id (FK, not null)
├── player2_enrollment_id (FK, not null)
├── round_number (not null)
├── table_number // 테이블 번호
├── judge_name // 심판 이름
├── status (enum: SCHEDULED, CALLED, IN_PROGRESS, COMPLETED, NO_SHOW)
├── result (enum: PLAYER1_WIN, PLAYER2_WIN, DRAW, BOTH_NO_SHOW)
├── player1_games_won (default: 0)
├── player2_games_won (default: 0)
├── match_slip_submitted (default: false) // 결과 제출 여부
├── match_slip_image_url // 결과 용지 사진
├── start_time
├── end_time
├── created_at (auto)
└── updated_at (auto)
```

### 📊 CompetitionResult (대회 최종 결과)

```
competition_results
├── id (PK, auto_increment)
├── competition_id (FK, not null)
├── enrollment_id (FK, not null)
├── final_rank
├── total_wins
├── total_losses
├── total_draws
├── match_points
├── game_win_percentage
├── opponent_match_win_percentage
├── prize_received (TEXT)
├── rating_change
└── created_at (auto)
```

---

## 🏪 Store Domain (매장 도메인)

### 📅 StoreSchedule (매장 대회 일정)

```
store_schedules
├── id (PK, auto_increment)
├── store_id (FK, not null)
├── competition_id (FK)
├── event_type (enum: TOURNAMENT, CASUAL_PLAY, SPECIAL_EVENT, MAINTENANCE)
├── event_date (not null)
├── start_time (not null)
├── end_time
├── recurring_type (enum: NONE, WEEKLY, BIWEEKLY, MONTHLY)
├── max_capacity
├── is_reservation_required (default: false)
├── status (enum: SCHEDULED, CONFIRMED, CANCELLED)
├── notes (TEXT)
├── created_at (auto)
└── updated_at (auto)
```

### 💳 StorePayment (매장 결제)

```
store_payments
├── id (PK, auto_increment)
├── store_id (FK, not null)
├── enrollment_id (FK)
├── payment_type (enum: ENTRY_FEE, PRODUCT_PURCHASE, SERVICE)
├── amount (not null)
├── payment_method (enum: CASH, CARD, TRANSFER, POINT)
├── payment_status (enum: PENDING, COMPLETED, FAILED, REFUNDED)
├── receipt_number
├── transaction_id
├── payment_date
├── notes (TEXT)
├── created_at (auto)
└── updated_at (auto)
```

---

## 📰 Content Domain (콘텐츠 도메인)

### 📄 Content (콘텐츠)

```
contents
├── id (PK, auto_increment)
├── author_id (FK, not null)
├── title (not null)
├── content (LONGTEXT)
├── content_type (enum: NEWS, ANNOUNCEMENT, GUIDE, RULE_CHANGE, EVENT_REPORT, DECK_GUIDE, META_ANALYSIS)
├── related_competition_id (FK) // 관련 대회
├── status (enum: DRAFT, PUBLISHED, ARCHIVED)
├── summary (TEXT)
├── tags (TEXT)
├── featured_image_url
├── view_count (default: 0)
├── is_pinned (default: false)
├── published_at
├── created_at (auto)
└── updated_at (auto)
```

### 📖 Rulebook (룰북)

```
rulebooks
├── id (PK, auto_increment)
├── uploaded_by (FK, not null)
├── title (not null)
├── version (not null)
├── language (enum: KR, EN, JP)
├── description (TEXT)
├── file_url (not null)
├── file_size
├── format_type (enum: STANDARD, EXTENDED, UNLIMITED, SPECIAL)
├── effective_date (not null)
├── is_latest (default: false)
├── change_summary (TEXT)
├── download_count (default: 0)
├── created_at (auto)
└── updated_at (auto)
```

---

## 📱 Notification Domain (알림 도메인)

### 🔔 Notification (알림)

```
notifications
├── id (PK, auto_increment)
├── user_id (FK, not null)
├── type (enum: COMPETITION_REMINDER, CHECK_IN_REMINDER, MATCH_CALLED, RESULT_POSTED, DECK_SUBMISSION_DUE)
├── title (not null)
├── message (TEXT)
├── related_competition_id (FK)
├── related_enrollment_id (FK)
├── is_read (default: false)
├── read_at
├── priority (enum: LOW, NORMAL, HIGH, URGENT)
├── scheduled_for // 예약 발송 시간
├── sent_at
├── created_at (auto)
└── expires_at
```

---

## 🔗 주요 연관관계

### 1:N 관계

- StoreOwner ←→ Competition (점주가 여러 대회 주최)
- Gamer ←→ CardDeck (게이머가 여러 덱 소유)
- Gamer ←→ Enrollment (게이머가 여러 대회 참가)
- Competition ←→ Enrollment (대회에 여러 참가자)
- Competition ←→ Match (대회에 여러 경기)
- CardDeck ←→ DeckDetail (덱에 여러 카드)
- User ←→ Notification (사용자가 여러 알림)

### N:M 관계

- Card ←→ CardDeck (through DeckDetail)

### 주요 변경사항

1. **카드 도메인 간소화**
   - 게임 메커니즘 제거 (효과, 스킬 등)
   - 순수 정보 저장 및 검색용 필드만 유지
   - 덱 빌딩 지원에 집중

2. **오프라인 대회 강화**
   - 체크인 시스템 (QR코드)
   - 좌석 배정
   - 매장 일정 관리
   - 결제 시스템

3. **예약 시스템 개선**
   - 사전 등록 및 결제
   - 체크인 상태 관리
   - 알림 시스템

4. **매장 운영 지원**
   - 대회 일정 관리
   - 결제 내역 관리
   - 좌석/테이블 관리