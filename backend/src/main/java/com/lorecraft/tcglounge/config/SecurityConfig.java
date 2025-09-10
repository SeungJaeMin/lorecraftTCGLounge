package com.lorecraft.tcglounge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                // 인증 API는 모든 사용자 접근 허용 (context-path 제외)
                .requestMatchers("/v1/auth/**").permitAll()
                // 테스트 API 허용
                .requestMatchers("/test/**").permitAll()
                .requestMatchers("/v1/test/**").permitAll()
                // 카드 API 허용 (개발 단계)
                .requestMatchers("/v1/cards/**").permitAll()
                // H2 콘솔 허용 (개발 단계)
                .requestMatchers("/h2-console/**").permitAll()
                // Swagger UI 허용
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // 게이머 API 허용 (개발 단계)
                .requestMatchers("/v1/gamer/**").permitAll()
                // 나머지는 인증 필요
                .anyRequest().authenticated()
            )
            .headers(headers -> headers.frameOptions().disable()); // H2 콘솔을 위해

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}