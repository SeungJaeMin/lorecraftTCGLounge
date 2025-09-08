# Backend 실행 가이드

## 🚀 백엔드 실행 방법들

### 1. VS Code Launch 설정 (권장)
`.vscode/launch.json`이 설정되어 있습니다.

**실행 방법:**
- VS Code에서 `F5` 키 또는 Run and Debug 패널 사용
- "Spring Boot - TcgLoungeApplication" 선택
- 자동으로 H2 데이터베이스와 함께 실행

### 2. Maven Wrapper 명령어

**Windows (PowerShell/CMD):**
```bash
cd backend

# 컴파일 및 패키징
.\mvnw.cmd clean package

# Spring Boot 실행
.\mvnw.cmd spring-boot:run

# 특정 프로파일로 실행
.\mvnw.cmd spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=dev

# H2 콘솔 활성화하여 실행
.\mvnw.cmd spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.h2.console.enabled=true"
```

### 3. JAR 파일 직접 실행

**빌드 후 실행:**
```bash
cd backend

# 빌드
.\mvnw.cmd clean package -DskipTests

# JAR 실행
java -jar target\tcg-lounge-1.0.0.jar

# 프로파일 지정
java -jar -Dspring.profiles.active=dev target\tcg-lounge-1.0.0.jar

# JVM 옵션과 함께
java -Xmx512m -Xms256m -jar target\tcg-lounge-1.0.0.jar
```

### 4. IntelliJ IDEA 실행

1. `TcgLoungeApplication.java` 파일 열기
2. 클래스명 옆 녹색 화살표 클릭
3. "Run 'TcgLoungeApplication'" 선택

**환경변수 설정:**
- Run Configuration 편집
- Environment variables에 추가:
  - `SPRING_PROFILES_ACTIVE=dev`
  - `SPRING_DATASOURCE_URL=jdbc:h2:mem:testdb`

### 5. Docker 실행

**개별 컨테이너:**
```bash
# 이미지 빌드
docker build -t tcg-backend ./backend

# 컨테이너 실행 (H2 데이터베이스)
docker run -d \
  --name tcg-backend \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=h2 \
  tcg-backend

# 로그 확인
docker logs -f tcg-backend
```

**Docker Compose:**
```bash
# 전체 스택 실행
docker-compose up -d

# 백엔드만 실행
docker-compose up -d backend

# 빌드 포함 실행
docker-compose up --build -d backend
```

### 6. Gradle 사용 (대안)

Gradle Wrapper가 있다면:
```bash
cd backend

# 실행
./gradlew bootRun

# 프로파일 지정
./gradlew bootRun --args='--spring.profiles.active=dev'
```

## 🔧 환경 변수 설정

### 필수 환경 변수
```properties
# 데이터베이스 (H2 인메모리)
SPRING_DATASOURCE_URL=jdbc:h2:mem:testdb
SPRING_DATASOURCE_USERNAME=sa
SPRING_DATASOURCE_PASSWORD=password

# 프로파일
SPRING_PROFILES_ACTIVE=dev

# H2 콘솔 (선택)
SPRING_H2_CONSOLE_ENABLED=true
SPRING_H2_CONSOLE_PATH=/h2-console

# 서버 포트 (선택)
SERVER_PORT=8080

# 컨텍스트 경로
SERVER_SERVLET_CONTEXT_PATH=/api
```

### Windows 환경 변수 설정
```cmd
# CMD
set SPRING_PROFILES_ACTIVE=dev
set SPRING_DATASOURCE_URL=jdbc:h2:mem:testdb

# PowerShell
$env:SPRING_PROFILES_ACTIVE="dev"
$env:SPRING_DATASOURCE_URL="jdbc:h2:mem:testdb"
```

## 📦 빌드 패스 문제 해결

### Maven 의존성 다운로드
```bash
cd backend

# 의존성 다운로드
.\mvnw.cmd dependency:resolve

# 오프라인용 의존성 다운로드
.\mvnw.cmd dependency:go-offline

# 로컬 저장소 정리
.\mvnw.cmd dependency:purge-local-repository
```

### 클래스패스 설정
```bash
# 클래스패스 출력
.\mvnw.cmd dependency:build-classpath

# Eclipse 프로젝트 생성
.\mvnw.cmd eclipse:eclipse

# IntelliJ 프로젝트 생성
.\mvnw.cmd idea:idea
```

## 🐛 문제 해결

### "빌드 패스가 설정되지 않음" 오류
1. Maven 의존성 재다운로드: `.\mvnw.cmd clean install`
2. IDE 캐시 정리 및 재시작
3. `.m2/repository` 폴더 삭제 후 재다운로드

### "포트 이미 사용 중" 오류
```bash
# 8080 포트 사용 프로세스 확인
netstat -ano | findstr :8080

# 프로세스 종료
taskkill /PID [프로세스ID] /F

# 다른 포트로 실행
.\mvnw.cmd spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

### Docker Desktop 연결 오류
1. Docker Desktop 재시작
2. WSL2 업데이트 확인
3. Docker 서비스 재시작:
```powershell
Restart-Service docker
```

## 🧪 실행 확인

### 헬스체크
```bash
curl http://localhost:8080/api/v1/test/health
```

### H2 콘솔 접속
```
URL: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:testdb
User: sa
Password: password
```

### 로그 확인 위치
- 콘솔 출력
- `backend/logs/` 디렉토리
- Docker: `docker logs tcg-backend`

## 📝 프로파일별 설정

### dev 프로파일 (개발)
- H2 인메모리 데이터베이스
- 디버그 로깅 활성화
- H2 콘솔 활성화

### prod 프로파일 (운영)
- MySQL 데이터베이스
- INFO 레벨 로깅
- 보안 설정 활성화

### test 프로파일 (테스트)
- H2 인메모리 데이터베이스
- 테스트 데이터 자동 생성
- 모든 로깅 활성화