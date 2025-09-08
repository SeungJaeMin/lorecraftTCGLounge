# Lorecraft TCG 라운지

Trading Card Game 운영을 위한 종합 관리 플랫폼으로 게이머, 점주, 관리자를 위한 포괄적인 기능을 제공합니다.

## 🎯 프로젝트 개요

Lorecraft TCG 라운지는 다음 기능을 지원하는 웹/모바일 통합 애플리케이션입니다:
- 카드 검색 및 덱 관리
- 대회 운영 및 참가
- 실시간 경기 추적
- 플레이어 랭킹 및 통계
- 점포 관리 및 주문

## 🏗️ 시스템 아키텍처

### 기술 스택
- **백엔드**: Spring Boot 3.x (Java 17)
- **프론트엔드**: React 18 with TypeScript
- **데이터베이스**: MySQL 8.0 with Redis 캐싱
- **API**: RESTful API + WebSocket 실시간 기능
- **문서화**: Swagger/OpenAPI 3.0
- **컨테이너화**: Docker & Docker Compose
- **클라우드**: AWS/GCP 대응 자동 확장 지원

### 프로젝트 구조
```
Workspace_lorecraft_TCG/
├── backend/              # Spring Boot 백엔드 애플리케이션
│   ├── src/main/java/com/lorecraft/tcglounge/
│   │   ├── controller/   # REST API 컨트롤러
│   │   ├── service/      # 비즈니스 로직
│   │   ├── repository/   # 데이터 접근 계층
│   │   ├── entity/       # JPA 엔티티
│   │   ├── dto/          # 데이터 전송 객체
│   │   ├── config/       # 설정 클래스
│   │   └── exception/    # 예외 처리
│   ├── pom.xml
│   └── Dockerfile
├── frontend/             # React 프론트엔드 애플리케이션
│   ├── src/
│   ├── package.json
│   └── Dockerfile
├── docker-compose.yml    # 전체 스택 오케스트레이션
├── docker-compose.dev.yml # 개발 환경
└── README.md
```

## 🏛️ 백엔드 아키텍처

### 레이어드 아키텍처 패턴
- **Controller Layer**: REST API 엔드포인트, HTTP 요청/응답 처리
- **Service Layer**: 비즈니스 로직, 트랜잭션 관리
- **Repository Layer**: 데이터 접근, JPA 쿼리 메서드
- **Entity Layer**: JPA 엔티티, 데이터베이스 스키마 매핑

### 기능 도메인
- **Card Management**: 카드 검색, 덱 관리
- **Competition Management**: 대회 운영, 경기 추적  
- **User Management**: 인증, 프로필, 랭킹
- **Store Management**: 점포 관리, 주문
- **Content Management**: CMS, 이벤트, 문서 관리

## 🚀 시작하기

### 사전 요구사항
- Docker & Docker Compose
- Node.js 18+ (로컬 개발용)
- Java 17+ (로컬 백엔드 개발용)
- MySQL 8.0+ (Docker 사용하지 않는 경우)
- Redis 6.0+ (Docker 사용하지 않는 경우)

### Docker로 빠른 시작

1. 저장소 클론
```bash
git clone https://github.com/SeungJaeMin/lorecraftTCGLounge.git
cd lorecraftTCGLounge
```

2. 환경 변수 복사
```bash
cp .env.example .env
# 설정에 맞게 .env 파일 수정
```

3. 전체 스택 시작
```bash
# 데이터베이스 UI 포함한 개발 환경
docker-compose -f docker-compose.dev.yml up -d

# 또는 프로덕션과 유사한 환경
docker-compose up -d
```

4. 애플리케이션 접속
- 프론트엔드: http://localhost:3000
- 백엔드 API: http://localhost:8080/api
- Swagger UI: http://localhost:8080/api/swagger-ui.html
- Adminer (DB UI): http://localhost:8081
- Redis Commander: http://localhost:8082

### 로컬 개발

#### 백엔드
```bash
cd backend
./mvnw spring-boot:run
```

#### 프론트엔드
```bash
cd frontend
npm install
npm start
```

## 🧪 테스트

### 백엔드 테스트
```bash
cd backend
./mvnw test
./mvnw test jacoco:report
```

### 프론트엔드 테스트
```bash
cd frontend
npm test
npm run test:coverage
```

## 📚 문서

- [아키텍처 v1.0](./Lorecraft_TCGlounge_Architecture_v1.md)
- [ERD 다이어그램](./Lorecraft_TCG_Lounge.drawio)
- [사용 사례 다이어그램](./Lorecraft_Web_USECASE_0802.drawio)

## 🎮 사용자 유형

### 비회원
- 게임 정보 및 공지사항 열람
- 카드 검색
- 공개 콘텐츠 탐색

### 게이머
- 덱 레시피 작성 및 관리
- 대회 및 이벤트 참가
- 개인 경기 기록 및 랭킹 확인
- 대회 체크인

### 점주
- 대회 주최
- 참가자 체크인 관리
- 경기 결과 입력
- 카드팩 주문

### 관리자
- 대회 생성 및 점주 지정
- 콘텐츠 및 공지사항 관리
- 시스템 전체 통계 모니터링
- 사용자 권한 관리

## 🔧 개발

### 백엔드 구조
```
backend/src/main/java/com/lorecraft/tcglounge/
├── domain/          # 도메인 로직 (card, competition, user, store, content)
├── application/     # 애플리케이션 서비스 및 오케스트레이션
├── api/            # REST 컨트롤러 및 WebSocket 핸들러
├── infrastructure/ # 설정, 보안, 캐싱
└── common/         # 공통 유틸리티 및 예외 처리
```

### 프론트엔드 구조
```
frontend/src/
├── components/     # 재사용 가능한 UI 컴포넌트
├── pages/         # 페이지 컴포넌트
├── services/      # API 서비스
├── hooks/         # 커스텀 React 훅
├── utils/         # 유틸리티 함수
├── types/         # TypeScript 타입 정의
└── styles/        # 전역 스타일 및 테마
```

### 명명 규칙
- 백엔드 서비스: `[동사][객체]Service` (예: `CreateDeckService`)
- 백엔드 리포지토리: `[엔티티]Repository`
- 백엔드 컨트롤러: `[사용자]Controller`
- React 컴포넌트: PascalCase (예: `CardList.tsx`)
- React 훅: camelCase with 'use' 접두사 (예: `useAuth.ts`)

## 🤝 기여 방법

1. 저장소를 포크하세요
2. 기능 브랜치를 생성하세요 (`git checkout -b feature/amazing-feature`)
3. 변경 사항을 커밋하세요 (`git commit -m 'Add amazing feature'`)
4. 브랜치에 푸시하세요 (`git push origin feature/amazing-feature`)
5. Pull Request를 열어주세요

## 📄 라이선스

이 프로젝트는 MIT 라이선스를 따릅니다 - 자세한 내용은 [LICENSE](LICENSE) 파일을 참조하세요.

## 📞 연락처

- 프로젝트 팀: Lorecraft 개발팀
- 이메일: contact@lorecraft.com
- 저장소: https://github.com/SeungJaeMin/lorecraftTCGLounge