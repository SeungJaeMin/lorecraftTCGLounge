# Lorecraft TCG Lounge - 생성된 엔티티 속성 정리

## 📊 User Domain (사용자 도메인)

### 👤 User (기본 사용자)
```sql
users
├── uid (PK, auto_increment)
├── username (unique, not null)
├── password (not null)
├── email (unique, not null)
├── phone_number
├── register_date (auto)
├── updated_at (auto)
├── is_active (default: true)
└── user_type (discriminator)
```

### 🎮 Gamer (게이머) - extends User
```sql
gamers
├── user_id (PK, FK)
├── nickname
├── total_wins (default: 0)
├── total_losses (default: 0)
├── total_draws (default: 0)
├── current_rating (default: 1000)
└── highest_rating (default: 1000)
```

### 🏪 StoreOwner (점주) - extends User
```sql
store_owners
├── user_id (PK, FK)
├── store_name (not null)
├── store_location (not null)
├── store_zipcode
├── business_license (unique)
├── contact_number
├── is_verified (default: false)
└── is_authorized (default: false)
```

### 👑 Admin (관리자) - extends User
```sql
admins
├── user_id (PK, FK)
├── admin_level (default: 1)
├── department
├── employee_id (unique)
├── can_manage_users (default: false)
├── can_manage_competitions (default: false)
├── can_manage_content (default: false)
└── can_manage_system (default: false)
```

### 📋 MatchRecordList (경기 기록)
```sql
match_record_lists
├── id (PK, auto_increment)
├── gamer_id (FK, not null)
├── season (not null)
├── total_matches (default: 0)
├── wins (default: 0)
├── losses (default: 0)
├── draws (default: 0)
├── best_rank
├── current_rank
├── rating_change (default: 0)
├── created_at (auto)
└── updated_at (auto)
```

---

## 🃏 Card Domain (카드 도메인)

### 🎴 Card (기본 카드)
```sql
cards
├── card_id (PK, auto_increment)
├── card_name (not null)
├── card_img
├── description (TEXT)
├── card_color (enum: RED, BLUE, GREEN, YELLOW, WHITE, BLACK, COLORLESS)
├── burst_number
├── rarity (enum: COMMON, RARE, SUPER_RARE, ULTRA_RARE, SECRET_RARE, LEGENDARY)
├── cost
├── is_active (default: true)
├── release_set
├── card_number
├── card_type (discriminator)
├── created_at (auto)
└── updated_at (auto)
```

### 👑 Leader - extends Card
```sql
(Single Table Inheritance)
├── life_points (not null)
├── leader_skill (TEXT)
├── awakening_condition (TEXT)
├── awakened_form_id
└── is_awakened (default: false)
```

### ⚔️ Unit - extends Card
```sql
(Single Table Inheritance)
├── attack_power (not null)
├── defense_power (not null)
├── unit_type
├── tribe
├── special_ability (TEXT)
├── summon_condition (TEXT)
├── can_attack_leader (default: true)
└── can_block (default: true)
```

### 🛡️ Item - extends Card
```sql
(Single Table Inheritance)
├── item_type (enum: EQUIPMENT, SPELL, TRAP, ARTIFACT, CONSUMABLE)
├── effect (TEXT)
├── duration
├── target_type
├── activation_condition (TEXT)
├── is_consumable (default: true)
└── stack_limit
```

### 🌍 Field - extends Card
```sql
(Single Table Inheritance)
├── field_type (enum: ENVIRONMENT, TERRAIN, WEATHER, DIMENSION, BARRIER)
├── field_effect (TEXT)
├── affected_colors
├── affected_types
├── activation_timing
├── is_global (default: false)
├── max_active_count (default: 1)
└── maintenance_cost
```

### 🪄 Spell - extends Card
```sql
(Single Table Inheritance)
├── spell_type (enum: OFFENSIVE, DEFENSIVE, UTILITY, HEALING, BUFF, DEBUFF, SUMMONING, CONTROL, RITUAL)
├── spell_effect (TEXT)
├── target_type
├── cast_timing
├── duration
├── is_instant (default: true)
├── is_counterable (default: true)
├── magic_school
└── power_level
```

### 📦 CardDeck (덱)
```sql
card_decks
├── id (PK, auto_increment)
├── gamer_id (FK, not null)
├── deck_name (not null)
├── description (TEXT)
├── is_public (default: false)
├── is_valid (default: false)
├── format_type
├── total_cards (default: 0)
├── deck_code (unique)
├── deck_status (enum: DRAFT, COMPLETED, PUBLISHED, ARCHIVED)
├── created_at (auto)
└── updated_at (auto)
```

### 🔗 DeckCard (덱-카드 연결)
```sql
deck_cards
├── id (PK, auto_increment)
├── deck_id (FK, not null)
├── card_id (FK, not null)
├── quantity (not null)
├── order_index
├── notes
├── added_at (auto)
└── UNIQUE(deck_id, card_id)
```

---

## 🏆 Competition Domain (대회 도메인)

### 🏟️ Competition (대회)
```sql
competitions
├── cid (PK, auto_increment)
├── assigned_store_owner_id (FK)
├── name (not null)
├── type (enum: SWISS_ROUND, SINGLE_ELIMINATION, DOUBLE_ELIMINATION, ROUND_ROBIN, BEST_OF_THREE, CUSTOM)
├── description (TEXT)
├── start_date (not null)
├── end_date (not null)
├── enrollment_start (not null)
├── enrollment_end (not null)
├── max_participants (not null)
├── current_participants (default: 0)
├── deck_limit (default: 1)
├── entry_fee (default: 0)
├── prize_pool (default: 0)
├── location
├── season (not null)
├── status (enum: SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED, SUSPENDED)
├── format_rules (TEXT)
├── special_rules (TEXT)
├── created_at (auto)
└── updated_at (auto)
```

### 📝 Enrollment (참가 등록)
```sql
enrollments
├── id (PK, auto_increment)
├── competition_id (FK, not null)
├── gamer_id (FK, not null)
├── selected_deck_id (FK)
├── check_in_status (enum: NOT_CHECKED, CHECKED_IN, ABSENT)
├── check_in_time
├── notes
├── seed_number
├── final_rank
├── prize_awarded
├── is_disqualified (default: false)
├── disqualification_reason
├── enrolled_at (auto)
├── updated_at (auto)
└── UNIQUE(competition_id, gamer_id)
```

### 🎴 EnrollmentDeck (등록 덱)
```sql
enrollment_decks
├── id (PK, auto_increment)
├── enrollment_id (FK, not null)
├── deck_id (FK, not null)
├── deck_order (not null)
├── is_primary (default: false)
├── notes
├── added_at (auto)
└── UNIQUE(enrollment_id, deck_id)
```

### ⚔️ Match (경기)
```sql
matches
├── match_id (PK, auto_increment)
├── competition_id (FK, not null)
├── player1_enrollment_id (FK, not null)
├── player2_enrollment_id (FK, not null)
├── winner_enrollment_id (FK)
├── loser_enrollment_id (FK)
├── round_number (not null)
├── match_number
├── table_number
├── status (enum: SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED, POSTPONED, FORFEIT, NO_SHOW)
├── result (enum: PLAYER1_WIN, PLAYER2_WIN, DRAW, PLAYER1_FORFEIT, PLAYER2_FORFEIT, DOUBLE_FORFEIT, NO_SHOW, DISQUALIFICATION)
├── player1_score (default: 0)
├── player2_score (default: 0)
├── scheduled_time
├── start_time
├── end_time
├── duration_minutes
├── notes (TEXT)
├── referee_notes (TEXT)
├── is_bye (default: false)
├── created_at (auto)
└── updated_at (auto)
```

### 📊 MatchDetailResult (상세 경기 결과)
```sql
match_detail_results
├── id (PK, auto_increment)
├── match_id (FK, not null)
├── gamer_record_id (FK, not null)
├── used_deck_id (FK)
├── individual_result (enum: MatchResult)
├── games_won (default: 0)
├── games_lost (default: 0)
├── rating_change (default: 0)
├── performance_score
├── match_notes
└── recorded_at (auto)
```

---

## 🏪 Store Domain (매장 도메인)

### 📋 OrderList (주문 목록)
```sql
order_lists
├── id (PK, auto_increment)
├── store_owner_id (FK, not null)
├── order_number (unique, not null)
├── status (enum: PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED)
├── total_amount (default: 0)
├── order_date (not null)
├── requested_delivery_date
├── actual_delivery_date
├── notes (TEXT)
├── created_at (auto)
└── updated_at (auto)
```

### 🛒 Order (개별 주문)
```sql
orders
├── id (PK, auto_increment)
├── order_list_id (FK, not null)
├── item_id (FK, not null)
├── quantity (not null)
├── unit_price (not null)
└── notes
```

### 📦 Item (상품)
```sql
items
├── id (PK, auto_increment)
├── item_name (not null)
├── item_code (unique, not null)
├── description (TEXT)
├── item_type (enum: CARD_PACK, BOOSTER_BOX, STARTER_DECK, ACCESSORY, SLEEVE, PLAYMAT, STORAGE, OTHER)
├── unit_price (not null)
├── stock_quantity (default: 0)
├── minimum_order_quantity (default: 1)
├── is_available (default: true)
├── release_date
├── image_url
├── created_at (auto)
└── updated_at (auto)
```

### 🎴 CardPack (카드팩)
```sql
card_packs
├── id (PK, auto_increment)
├── item_id (FK, not null)
├── pack_name (not null)
├── set_code (not null)
├── cards_per_pack (not null)
├── rare_card_guaranteed (default: false)
└── pack_description (TEXT)

card_pack_contents (다대다 관계 테이블)
├── card_pack_id (FK)
└── card_id (FK)
```

---

## 📰 Content Domain (콘텐츠 도메인)

### 📄 Content (콘텐츠)
```sql
contents
├── id (PK, auto_increment)
├── author_id (FK, not null)
├── title (not null)
├── content (LONGTEXT)
├── content_type (enum: NEWS, ANNOUNCEMENT, GUIDE, RULE, EVENT, PRODUCT_INFO, FAQ, BLOG)
├── status (enum: DRAFT, SCHEDULED, PUBLISHED, ARCHIVED)
├── summary
├── tags
├── featured_image_url
├── view_count (default: 0)
├── is_pinned (default: false)
├── published_at
├── created_at (auto)
└── updated_at (auto)
```

### 🎉 Event (이벤트)
```sql
events
├── id (PK, auto_increment)
├── created_by (FK, not null)
├── title (not null)
├── description (TEXT)
├── start_date (not null)
├── end_date (not null)
├── registration_start
├── registration_end
├── location
├── max_participants
├── current_participants (default: 0)
├── event_type (enum: TOURNAMENT, CASUAL_PLAY, WORKSHOP, RELEASE_EVENT, SEASONAL, ONLINE, COMMUNITY)
├── status (enum: SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED)
├── requirements (TEXT)
├── rewards (TEXT)
├── image_url
├── is_featured (default: false)
├── created_at (auto)
└── updated_at (auto)
```

### 📖 Rulebook (룰북)
```sql
rulebooks
├── id (PK, auto_increment)
├── uploaded_by (FK, not null)
├── title (not null)
├── version (not null)
├── description (TEXT)
├── file_url (not null)
├── file_name (not null)
├── file_size
├── file_type (enum: PDF, DOC, DOCX, HTML)
├── status (enum: DRAFT, PUBLISHED, ARCHIVED)
├── effective_date
├── download_count (default: 0)
├── is_latest (default: false)
├── change_notes (TEXT)
├── created_at (auto)
└── updated_at (auto)
```

---

## 🔗 주요 연관관계

### 1:N 관계
- User ←→ CardDeck (Gamer가 여러 덱 소유)
- User ←→ Enrollment (Gamer가 여러 대회 참가)
- Competition ←→ Enrollment (대회에 여러 참가자)
- Competition ←→ Match (대회에 여러 경기)
- CardDeck ←→ DeckCard (덱에 여러 카드)
- StoreOwner ←→ OrderList (점주가 여러 주문)
- OrderList ←→ Order (주문 목록에 여러 상품)

### 1:1 관계
- Gamer ←→ MatchRecordList (게이머당 하나의 기록)
- Item ←→ CardPack (상품과 카드팩)

### N:M 관계
- CardPack ←→ Card (카드팩에 포함 가능한 카드들)

이제 ERD를 확인하시고 수정이 필요한 부분이 있으면 알려주세요!