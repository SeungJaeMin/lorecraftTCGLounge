package com.lorecraft.tcglounge.security;

import com.lorecraft.tcglounge.service.JwtTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * JWT 인증 필터
 * 모든 요청에 대해 JWT 토큰을 검증하고 SecurityContext를 설정합니다.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    
    private final JwtTokenService jwtTokenService;
    private final SecurityEndpointConfig endpointConfig;
    
    public JwtAuthenticationFilter(JwtTokenService jwtTokenService, 
                                  SecurityEndpointConfig endpointConfig) {
        this.jwtTokenService = jwtTokenService;
        this.endpointConfig = endpointConfig;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        
        log.debug("Processing request: {} {}", method, requestURI);
        
        // 1. 엔드포인트 보안 정책 확인
        SecurityEndpointConfig.EndpointSecurity security = endpointConfig.getEndpointSecurity(method, requestURI);
        
        // 2. 토큰 추출
        String token = extractToken(request);
        
        // 3. 보안 정책에 따른 처리
        switch (security) {
            case PUBLIC:
                // 공개 엔드포인트 - 토큰 없어도 진행
                log.debug("Public endpoint, proceeding without authentication");
                break;
                
            case OPTIONAL:
                // 선택적 인증 - 토큰 있으면 SecurityContext 설정
                if (token != null) {
                    try {
                        setSecurityContext(token);
                        log.debug("Optional auth: Token validated and context set");
                    } catch (Exception e) {
                        log.debug("Optional auth: Invalid token, proceeding as anonymous");
                    }
                }
                break;
                
            case REQUIRED_AUTH:
                // 인증 필수 - 토큰 검증
                if (token == null) {
                    log.warn("Authentication required but no token provided for: {} {}", method, requestURI);
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
                    return;
                }
                
                try {
                    setSecurityContext(token);
                    log.debug("Required auth: Token validated and context set");
                } catch (Exception e) {
                    log.error("Invalid token for protected endpoint: {}", e.getMessage());
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
                    return;
                }
                break;
                
            case REQUIRED_GAMER:
                // GAMER 권한 필수
                if (!validateAndSetContextWithRole(token, "GAMER", response)) {
                    return;
                }
                break;
                
            case REQUIRED_ADMIN:
                // ADMIN 권한 필수
                if (!validateAndSetContextWithRole(token, "ADMIN", response)) {
                    return;
                }
                break;
                
            case REQUIRED_STORE_OWNER:
                // STORE_OWNER 권한 필수
                if (!validateAndSetContextWithRole(token, "STORE_OWNER", response)) {
                    return;
                }
                break;
        }
        
        // 4. 다음 필터로 진행
        filterChain.doFilter(request, response);
        
        // 5. 요청 처리 후 SecurityContext 정리
        SecurityContextHolder.clearContext();
    }
    
    /**
     * Authorization 헤더에서 토큰 추출
     */
    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
    
    /**
     * 토큰 검증 및 SecurityContext 설정
     */
    private void setSecurityContext(String token) {
        if (!jwtTokenService.isTokenValid(token)) {
            throw new RuntimeException("Invalid token");
        }
        
        Long userId = jwtTokenService.extractUserId(token);
        String username = jwtTokenService.extractUsername(token);
        String userType = jwtTokenService.extractUserType(token);
        
        // 권한 설정
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_" + userType)
        );
        
        // Authentication 객체 생성
        UsernamePasswordAuthenticationToken authentication = 
            new UsernamePasswordAuthenticationToken(userId, null, authorities);
        
        // 추가 정보 저장을 위한 details 설정
        authentication.setDetails(new JwtAuthenticationDetails(userId, username, userType));
        
        // SecurityContext에 설정
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    
    /**
     * 특정 역할 검증 및 SecurityContext 설정
     */
    private boolean validateAndSetContextWithRole(String token, String requiredRole, 
                                                  HttpServletResponse response) throws IOException {
        if (token == null) {
            log.warn("{} role required but no token provided", requiredRole);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, 
                             requiredRole + " authentication required");
            return false;
        }
        
        try {
            setSecurityContext(token);
            
            // 역할 검증
            String userType = jwtTokenService.extractUserType(token);
            if (!requiredRole.equals(userType)) {
                log.warn("User type {} tried to access {} endpoint", userType, requiredRole);
                response.sendError(HttpServletResponse.SC_FORBIDDEN, 
                                 "Access denied. " + requiredRole + " role required");
                return false;
            }
            
            return true;
        } catch (Exception e) {
            log.error("Invalid token for {} endpoint: {}", requiredRole, e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
            return false;
        }
    }
    
    /**
     * JWT 인증 상세 정보 클래스
     */
    public static class JwtAuthenticationDetails {
        private final Long userId;
        private final String username;
        private final String userType;
        
        public JwtAuthenticationDetails(Long userId, String username, String userType) {
            this.userId = userId;
            this.username = username;
            this.userType = userType;
        }
        
        public Long getUserId() { return userId; }
        public String getUsername() { return username; }
        public String getUserType() { return userType; }
    }
}