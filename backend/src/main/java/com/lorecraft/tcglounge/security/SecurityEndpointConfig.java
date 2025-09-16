package com.lorecraft.tcglounge.security;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 엔드포인트별 보안 정책 설정
 * URL 패턴과 HTTP 메소드에 따라 필요한 인증/인가 레벨을 정의합니다.
 */
@Component
public class SecurityEndpointConfig {
    
    private final Map<String, EndpointSecurity> endpointSecurityMap;
    
    public SecurityEndpointConfig() {
        this.endpointSecurityMap = new HashMap<>();
        initializeEndpointSecurity();
    }
    
    /**
     * 엔드포인트 보안 레벨 정의
     */
    public enum EndpointSecurity {
        PUBLIC,             // 누구나 접근 가능
        OPTIONAL,           // 선택적 인증 (토큰 있으면 개인화)
        REQUIRED_AUTH,      // 인증 필수 (토큰 필수)
        REQUIRED_GAMER,     // GAMER 역할 필수
        REQUIRED_ADMIN,     // ADMIN 역할 필수
        REQUIRED_STORE_OWNER // STORE_OWNER 역할 필수
    }
    
    /**
     * 엔드포인트별 보안 정책 초기화
     */
    private void initializeEndpointSecurity() {
        // ===== Auth 관련 =====
        addEndpoint("POST", "/api/v1/auth/login", EndpointSecurity.PUBLIC);
        addEndpoint("POST", "/api/v1/auth/register", EndpointSecurity.PUBLIC);
        addEndpoint("POST", "/api/v1/auth/logout", EndpointSecurity.REQUIRED_AUTH);
        addEndpoint("GET", "/api/v1/auth/me", EndpointSecurity.REQUIRED_AUTH);
        
        // ===== Card 관련 =====
        // 카드 조회는 모두 공개
        addEndpoint("GET", "/api/v1/cards", EndpointSecurity.PUBLIC);
        addEndpoint("GET", "/api/v1/cards/*", EndpointSecurity.PUBLIC);
        addEndpoint("GET", "/api/v1/cards/search", EndpointSecurity.PUBLIC);
        
        // 카드 관리는 ADMIN만
        addEndpoint("POST", "/api/v1/cards", EndpointSecurity.REQUIRED_ADMIN);
        addEndpoint("PUT", "/api/v1/cards/*", EndpointSecurity.REQUIRED_ADMIN);
        addEndpoint("DELETE", "/api/v1/cards/*", EndpointSecurity.REQUIRED_ADMIN);
        
        // ===== Deck 관련 =====
        // 공개 덱 조회
        addEndpoint("GET", "/api/v1/decks/public", EndpointSecurity.PUBLIC);
        addEndpoint("GET", "/api/v1/decks/public/*", EndpointSecurity.PUBLIC);
        
        // 덱 레시피 조회 (선택적 인증 - 토큰 있으면 좋아요 여부 등 추가 정보)
        addEndpoint("GET", "/api/v1/decks/recipes", EndpointSecurity.OPTIONAL);
        addEndpoint("GET", "/api/v1/decks/recipes/*", EndpointSecurity.OPTIONAL);
        
        // 내 덱 관리 (GAMER 필수)  
        addEndpoint("GET", "/api/v1/decks/my-decks", EndpointSecurity.REQUIRED_GAMER);
        addEndpoint("POST", "/api/v1/decks", EndpointSecurity.REQUIRED_GAMER);
        addEndpoint("POST", "/api/v1/decks/save", EndpointSecurity.REQUIRED_GAMER);
        addEndpoint("POST", "/api/v1/decks/save-with-cards", EndpointSecurity.REQUIRED_GAMER);
        addEndpoint("PUT", "/api/v1/decks/*", EndpointSecurity.REQUIRED_GAMER);
        addEndpoint("DELETE", "/api/v1/decks/*", EndpointSecurity.REQUIRED_GAMER);
        
        // 덱 카드 관리 (GAMER 필수)
        addEndpoint("POST", "/api/v1/decks/*/cards", EndpointSecurity.REQUIRED_GAMER);
        addEndpoint("DELETE", "/api/v1/decks/*/cards/*", EndpointSecurity.REQUIRED_GAMER);
        addEndpoint("PUT", "/api/v1/decks/*/cards/*", EndpointSecurity.REQUIRED_GAMER);
        
        // ===== Gamer 관련 =====
        addEndpoint("GET", "/api/v1/gamer/profile", EndpointSecurity.REQUIRED_GAMER);
        addEndpoint("GET", "/api/v1/gamer/dashboard", EndpointSecurity.REQUIRED_GAMER);
        addEndpoint("PUT", "/api/v1/gamer/profile", EndpointSecurity.REQUIRED_GAMER);
        
        // ===== User 관련 =====
        addEndpoint("GET", "/api/v1/users", EndpointSecurity.REQUIRED_ADMIN);
        addEndpoint("GET", "/api/v1/users/*", EndpointSecurity.REQUIRED_AUTH);
        addEndpoint("PUT", "/api/v1/users/profile", EndpointSecurity.REQUIRED_AUTH);
        addEndpoint("PUT", "/api/v1/users/password", EndpointSecurity.REQUIRED_AUTH);
        addEndpoint("DELETE", "/api/v1/users/*", EndpointSecurity.REQUIRED_ADMIN);
        
        // ===== Test 관련 (개발용) =====
        addEndpoint("GET", "/api/v1/test/**", EndpointSecurity.PUBLIC);
        addEndpoint("POST", "/api/v1/test/**", EndpointSecurity.PUBLIC);
        
        // ===== Health Check =====
        addEndpoint("GET", "/api/actuator/health", EndpointSecurity.PUBLIC);
    }
    
    /**
     * 엔드포인트 추가
     */
    private void addEndpoint(String method, String path, EndpointSecurity security) {
        String key = method + ":" + path;
        endpointSecurityMap.put(key, security);
    }
    
    /**
     * 요청에 대한 보안 정책 반환
     */
    public EndpointSecurity getEndpointSecurity(String method, String requestURI) {
        // 정확한 매칭 먼저 시도
        String exactKey = method + ":" + requestURI;
        if (endpointSecurityMap.containsKey(exactKey)) {
            return endpointSecurityMap.get(exactKey);
        }
        
        // 와일드카드 패턴 매칭
        for (Map.Entry<String, EndpointSecurity> entry : endpointSecurityMap.entrySet()) {
            String pattern = entry.getKey();
            if (matchesPattern(method + ":" + requestURI, pattern)) {
                return entry.getValue();
            }
        }
        
        // 매칭되는 패턴이 없으면 기본적으로 인증 필수
        return EndpointSecurity.REQUIRED_AUTH;
    }
    
    /**
     * 와일드카드 패턴 매칭
     * - * : 단일 경로 세그먼트 매칭
     * - ** : 여러 경로 세그먼트 매칭
     */
    private boolean matchesPattern(String uri, String pattern) {
        if (pattern.contains("**")) {
            // ** 패턴: /api/v1/test/** matches /api/v1/test/anything/here
            String prefix = pattern.substring(0, pattern.indexOf("**"));
            return uri.startsWith(prefix);
        } else if (pattern.contains("*")) {
            // * 패턴: /api/v1/cards/* matches /api/v1/cards/123
            String regex = pattern.replace("*", "[^/]+");
            return uri.matches(regex);
        }
        
        return false;
    }
    
    /**
     * 보안 정책 업데이트 (런타임 변경용)
     */
    public void updateEndpointSecurity(String method, String path, EndpointSecurity security) {
        addEndpoint(method, path, security);
    }
    
    /**
     * 특정 엔드포인트가 공개 접근 가능한지 확인
     */
    public boolean isPublicEndpoint(String method, String requestURI) {
        return getEndpointSecurity(method, requestURI) == EndpointSecurity.PUBLIC;
    }
    
    /**
     * 특정 엔드포인트가 선택적 인증인지 확인
     */
    public boolean isOptionalAuthEndpoint(String method, String requestURI) {
        return getEndpointSecurity(method, requestURI) == EndpointSecurity.OPTIONAL;
    }
}