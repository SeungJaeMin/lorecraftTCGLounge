# Lombok 의존성 이슈 보고서
작성일: 2025-01-03

## 발생한 문제

### 1. Docker Maven 빌드 환경에서 Lombok 어노테이션 처리 실패
- **증상**: Docker 컨테이너 내 Maven 빌드 시 Lombok 어노테이션(@Getter, @Setter, @Builder 등)이 코드를 생성하지 않음
- **영향**: 43개 파일에서 수백 개의 컴파일 에러 발생
- **환경**: 
  - Docker Desktop (Windows)
  - Maven 3.9.5
  - Java 17
  - Lombok 1.18.30
  - Spring Boot 3.2.1

### 2. 구체적인 에러 사례
```
[ERROR] /app/src/main/java/com/lorecraft/tcglounge/domain/card/entity/Card.java:[xx,xx] error: cannot find symbol
  symbol:   method getCardId()
  location: variable card of type Card
```

## 근본 원인 분석

1. **Annotation Processing 실패**: Docker 컨테이너 환경에서 Maven의 annotation processor가 제대로 동작하지 않음
2. **의존성 전파 문제**: lombok.config 설정이 컨테이너 빌드 시 제대로 적용되지 않음
3. **JDK 버전 호환성**: Lombok과 JDK 17 사이의 잠재적 호환성 이슈

## 영향받은 파일 분석

### Entity 클래스 (25개)
- Card 도메인: 9개 파일
- Competition 도메인: 7개 파일  
- User 도메인: 9개 파일

### Service 클래스 (6개)
- @RequiredArgsConstructor 사용으로 생성자 주입 실패

### Controller 클래스 (4개)
- @RequiredArgsConstructor 사용으로 의존성 주입 실패

### DTO 클래스 (5개)
- @Data, @Builder 사용

### 기타 (3개)
- Configuration, Exception 클래스

## 장기적 리스크

1. **JDK 버전 업그레이드 시 호환성 문제**
   - 새로운 JDK 버전 출시 시 Lombok 업데이트 대기 필요
   - 프로덕션 환경 업그레이드 지연 가능성

2. **클라우드 네이티브 환경 이슈**
   - 컨테이너화된 빌드 환경에서 불안정
   - CI/CD 파이프라인 구축 시 추가 설정 복잡도

3. **IDE 의존성**
   - 개발자별 IDE 플러그인 설치 필수
   - 원격 개발 환경에서 제약사항

4. **디버깅 어려움**
   - 생성된 코드가 소스에 없어 디버깅 복잡
   - 스택트레이스 해석 어려움

## 해결 방안

### 단기 해결책 (완료)
- 모든 Entity에 명시적 getter/setter 메소드 추가
- @RequiredArgsConstructor 제거 후 명시적 생성자 작성

### 장기 해결책 (진행 예정)
1. **Phase 1**: Service/Controller의 @RequiredArgsConstructor 제거
2. **Phase 2**: Entity의 Lombok 어노테이션 완전 제거
3. **Phase 3**: DTO의 @Builder 패턴을 수동 구현으로 전환

## 권장사항

### 프로덕션 프로젝트에서의 Lombok 사용 가이드라인
1. **보수적 사용 원칙**
   - 필수불가결한 경우에만 제한적 사용
   - @Getter, @Setter, @ToString만 허용
   - experimental 기능 사용 금지

2. **대안 고려**
   - IDE의 코드 생성 기능 활용
   - Records (Java 14+) 사용 검토
   - 명시적 코드 작성 선호

3. **문서화**
   - Lombok 사용 시 명확한 문서화
   - 빌드 환경 설정 가이드 제공

## 결론
Lombok은 개발 편의성을 제공하지만, 컨테이너화된 환경과 장기적 유지보수 관점에서 리스크가 존재합니다. 
특히 Docker 기반 개발/배포 환경에서는 안정성을 위해 Lombok 의존성을 최소화하는 것을 권장합니다.

---
*이 문서는 2025년 1월 3일 Docker 빌드 실패 이슈 해결 과정에서 작성되었습니다.*