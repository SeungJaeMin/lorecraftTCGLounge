# Lorecraft TCG Lounge Architecture v1.0
## 프로젝트 아키텍처 및 도메인 설계 보고서

---

## 1. 프로젝트 개요

### 1.1 시스템 소개
Lorecraft TCG Lounge는 Trading Card Game(TCG) 운영 회사가 제공하는 종합 플랫폼으로, 일반 고객, 게이머, 점주에게 각자 필요한 편의성을 제공하는 웹/모바일 통합 애플리케이션입니다.

### 1.2 주요 기능
- 카드 정보 검색 및 관리
- 덱 레시피 작성 및 공유
- 대회/이벤트 참가 예약 및 관리
- 플레이어 전적 및 랭킹 시스템
- 점주 대회 운영 지원
- 실시간 대진표 및 결과 업데이트

### 1.3 대상 사용자
- **비회원**: 일반 정보 열람, 카드 검색
- **회원(Gamer)**: 덱 관리, 대회 참가, 전적 확인
- **점주회원(StoreOwner)**: 대회 운영, 체크인 관리, 카드팩 주문
- **관리자(Admin)**: 콘텐츠 관리, 대회 총괄, 시스템 관리

---

## 2. 시스템 아키텍처

### 2.1 기본 구조
- **Architecture Pattern**: 3-Tier Architecture
- **Backend Framework**: Spring Boot (Java)
- **Database**: MySQL (RDS)
- **Cache**: Redis (ElastiCache)
- **Storage**: AWS S3
- **CDN**: CloudFront
- **Real-time**: WebSocket

### 2.2 클라이언트 지원
- Web Application (React/Vue)
- Mobile Application (iOS/Android)
- 통합 RESTful API 제공

### 2.3 클라우드 인프라 전략

#### Stage 1: MVP (0~6개월)
```
- EC2 t3.small (1대)
- RDS MySQL t3.micro
- S3 + CloudFront
- 예상 비용: 월 5~10만원
```

#### Stage 2: 성장기 (6~12개월)
```
- ALB + EC2 Auto Scaling (1~3대)
- RDS MySQL (읽기 복제본 1대)
- ElastiCache Redis
- API Gateway
- 예상 비용: 월 20~50만원
```

#### Stage 3: 확장기 (12개월~)
```
- EKS/GKE Kubernetes
- Aurora MySQL
- ElastiCache 클러스터
- 실시간 서비스 분리 (WebSocket)
- ElasticSearch (카드 검색)
- 예상 비용: 월 100만원+
```

---

## 3. 도메인 설계

### 3.1 Card Domain (카드/덱 관리)

#### 핵심 엔티티
- **Card**: 카드 정보
  - 속성: card_id, card_name, card_img, description, card_Color, Burst_Number
  - 타입: Leader, Unit, Item, Field
- **CardDeck**: 덱 프리셋
- **DeckCard**: 덱-카드 연결

#### 주요 기능
- 카드 검색 (조건별 필터링)
- 덱 레시피 CRUD
- 덱 프리셋 관리
- 카드 상세정보 조회

#### 기술적 특징
- 높은 읽기 부하 → Redis 캐싱 필수
- CDN으로 카드 이미지 서빙
- ElasticSearch 검색 최적화 (향후)

### 3.2 Competition Domain (대회 관리)

#### 핵심 엔티티
- **Competition**: 대회 정보
  - type: SWISS_ROUND, SINGLE_ELIM, DOUBLE_ELIM
  - deckLimit: 대회별 덱 제출 수량
  - season: 시즌 정보
  - assignedStoreOwner: 지정된 점주
- **Match**: 경기 정보
  - round: 라운드 정보
  - status: PENDING, IN_PROGRESS, COMPLETED
  - result: WIN_P1, WIN_P2, DRAW, REMATCH
- **Enrollment**: 참가 등록
  - checkInStatus: NOT_CHECKED, CHECKED_IN, ABSENT
  - selectedDecks: 선택한 덱 프리셋 리스트
- **MatchResult**: 경기 결과

#### 주요 기능
- 대회 개최 (Admin 권한)
- 점주 지정 (Admin 권한)
- 참가 신청 + 덱 선택
- 체크인 관리 (대회 30분~1시간 전)
- 대진표 자동 생성 (스위스라운드 등)
- 매치 결과 입력 (승/패/무승부/기권)
- 다음 라운드 자동 매칭

#### 기술적 특징
- WebSocket 실시간 업데이트
- 트랜잭션 무결성 중요
- 대회날 트래픽 급증 대응 (Auto Scaling)

### 3.3 User Domain (사용자/인증)

#### 핵심 엔티티
- **User**: 기본 사용자
  - 속성: uid, id, pw, email, phone_number, register_date
- **Gamer**: 일반 회원 (User 상속)
  - 추가: MatchRecordList, CardDeck 소유
- **StoreOwner**: 점주 (User 상속)
  - 추가: store_name, store_location, store_zipcode
- **Admin**: 관리자 (User 상속)

#### 주요 기능
- 회원가입/로그인 (JWT 인증)
- 마이페이지 관리
- 전적 조회
- 랭킹 시스템
- 권한 관리

#### 기술적 특징
- JWT 토큰 기반 인증
- Redis 세션 관리
- Spring Security 권한 체계

### 3.4 Store Domain (점포 관리)

#### 핵심 엔티티
- **Store**: 점포 정보
- **OrderList**: 주문 목록
- **Order**: 주문 상세
- **Item**: 상품 정보
- **CardPack**: 카드팩

#### 주요 기능
- 공인점포 목록 조회
- 카드팩 B2B 주문
- 점포 정보 관리
- 재고 관리

#### 기술적 특징
- 상대적으로 낮은 트래픽
- B2B 트랜잭션 처리

### 3.5 Content Domain (콘텐츠 관리)

#### 핵심 엔티티
- **Content**: 공지사항/뉴스
- **Event**: 이벤트 정보
- **Rulebook**: 룰북 파일
- **Notice**: 공지사항

#### 주요 기능
- CMS 기능 (관리자)
- 파일 업로드/다운로드
- 콘텐츠 CRUD
- 이벤트 관리

#### 기술적 특징
- S3 파일 저장
- CloudFront CDN 배포
- 정적 콘텐츠 캐싱

---

## 4. 비즈니스 규칙

### 4.1 대회 운영 프로세스

#### 대회 생성 및 준비
1. **관리자**가 대회 생성 및 점주 지정
2. 대회 정보 공개 (타입, 덱 제한, 일정)
3. **게이머**가 참가 신청 + 덱 프리셋 선택

#### 대회 당일 프로세스
1. **체크인 단계** (대회 30분~1시간 전)
   - 점주가 체크인 오픈
   - 참가자 현장 도착 확인
   - 미체크인자 불참 처리

2. **대진표 생성**
   - 체크인 완료자만 대진표 포함
   - 대회 타입별 자동 매칭 (스위스라운드 등)

3. **경기 진행**
   - 매치 결과 입력 (승/패/무승부/기권)
   - 무승부시 재경기 (다른 덱 사용 가능)
   - 다음 라운드 자동 생성

4. **실시간 업데이트**
   - WebSocket으로 대진표 실시간 반영
   - 모든 참가자에게 즉시 알림

### 4.2 권한 체계

#### 관리자(Admin)
- 대회 생성/취소/중단
- 점주 권한 부여/회수
- 전체 시스템 모니터링
- 콘텐츠 관리

#### 점주(StoreOwner)
- 체크인 관리
- 대진 결과 입력/수정
- 참가자 관리
- 카드팩 주문

#### 게이머(Gamer)
- 대회 참가
- 덱 관리
- 전적 확인

### 4.3 시즌 관리
- 연간 시즌제 운영
- 시즌별 랭킹 초기화
- 과거 시즌 데이터 아카이빙

---

## 5. 프로젝트 구조

```
lorecraft-tcg/
├── domain/
│   ├── card/
│   │   ├── entity/
│   │   ├── repository/
│   │   └── service/
│   ├── competition/
│   │   ├── entity/
│   │   ├── repository/
│   │   └── service/
│   ├── user/
│   │   ├── entity/
│   │   ├── repository/
│   │   └── service/
│   ├── store/
│   │   ├── entity/
│   │   ├── repository/
│   │   └── service/
│   └── content/
│       ├── entity/
│       ├── repository/
│       └── service/
├── application/
│   ├── orchestration/
│   └── facade/
├── api/
│   ├── v1/
│   │   ├── controller/
│   │   └── dto/
│   └── websocket/
├── infrastructure/
│   ├── config/
│   ├── security/
│   ├── cache/
│   └── storage/
└── common/
    ├── exception/
    ├── dto/
    └── util/
```

### 5.1 네이밍 컨벤션

#### Service 네이밍 규칙
- **패턴**: 동사 + 목적어 + Service
- **예시**: CreateDeckService, UpdateMatchResultService
- **CRUD 동사**: Create, Read, Update, Delete, Search
- **비즈니스 동사**: Register, Authenticate, Enroll, CheckIn, Generate

#### 계층별 책임
- **Domain Service**: 단일 책임, 순수 비즈니스 로직
- **Application Service**: 도메인 조합, 복잡한 워크플로우
- **Infrastructure Service**: 기술적 관심사 (캐시, 파일 처리)

---

## 6. API 설계 원칙

### 6.1 RESTful API 구조
```
/api/v1/gamer/*      - 게이머 전용 API
/api/v1/store-owner/* - 점주 전용 API
/api/v1/admin/*      - 관리자 전용 API
/api/v1/public/*     - 공개 API
```

### 6.2 응답 포맷 표준화
- 웹/모바일 통합 JSON 응답
- 에러 코드 체계 통일
- 페이징 처리 표준화

### 6.3 인증/인가
- JWT Bearer Token
- Role-based Access Control
- API Rate Limiting

---

## 7. 데이터 흐름 예시

### 7.1 대회 참가 플로우
```
1. Gamer → 대회 목록 조회 (Redis 캐시)
2. Gamer → 참가 신청 + 덱 선택 (트랜잭션)
3. System → Enrollment 생성
4. StoreOwner → 체크인 오픈 (대회 30분 전)
5. Gamer → 현장 체크인
6. System → 대진표 자동 생성
7. WebSocket → 실시간 대진 업데이트
8. StoreOwner → 매치 결과 입력
9. System → 다음 라운드 생성
10. System → 랭킹 업데이트 (비동기)
```

---

## 8. 확장 계획

### 8.1 단기 (6개월)
- MVP 출시
- 기본 대회 운영 기능
- 웹 클라이언트 우선

### 8.2 중기 (12개월)
- 모바일 앱 출시
- 실시간 기능 강화
- 고급 통계 기능

### 8.3 장기 (12개월+)
- 글로벌 서비스 확장
- AI 기반 매칭 시스템
- e스포츠 대회 지원

---

## 9. 위험 관리

### 9.1 기술적 위험
- **대회날 트래픽 급증**: Auto Scaling으로 대응
- **실시간 동기화 실패**: WebSocket 재연결 로직
- **데이터 정합성**: 트랜잭션 관리 철저

### 9.2 비즈니스 위험
- **사용자 부족**: 최소 비용 구조로 시작
- **확장성 문제**: 도메인 분리 설계로 MSA 전환 가능

---

## 10. 결론

Lorecraft TCG Lounge Architecture v1은 모놀리식 구조로 시작하되, 도메인 주도 설계를 통해 향후 마이크로서비스로의 전환이 용이하도록 설계되었습니다. 클라우드 네이티브 환경에서 비용 효율적으로 시작하여 성장에 따라 자연스럽게 확장할 수 있는 구조입니다.

### 핵심 강점
- 명확한 도메인 경계
- 확장 가능한 아키텍처
- 비용 효율적 클라우드 전략
- 실시간 처리 지원
- 멀티 클라이언트 지원

---

**작성일**: 2024년
**버전**: 1.0
**작성자**: Lorecraft TCG Development Team