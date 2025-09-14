package com.lorecraft.tcglounge.service;

import com.lorecraft.tcglounge.entity.User;
import com.lorecraft.tcglounge.entity.Gamer;
import com.lorecraft.tcglounge.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtTokenService jwtTokenService;

    public User authenticate(String username, String password) {
        // userid 컬럼으로 사용자 조회
        Optional<User> userOpt = userRepository.findByUserid(username);
        
        if (!userOpt.isPresent()) {
            return null;
        }
        
        User user = userOpt.get();
        
        // 비밀번호 검증 - password 필드 사용
        if (passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        
        return null;
    }

    public User findByUsername(String username) {
        // userid 컬럼으로 사용자 조회
        Optional<User> userOpt = userRepository.findByUserid(username);
        return userOpt.orElse(null);
    }

    public User findByUid(Long uid) {
        // UID로 사용자 조회
        Optional<User> userOpt = userRepository.findById(uid);
        return userOpt.orElse(null);
    }

    public User createUser(String username, String password, String email) {
        // 사용자 생성 로직
        User user = new User();
        user.setUserid(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setIsActive(true);
        
        return userRepository.save(user);
    }

    public String generateJwtToken(User user) {
        String userType = (user instanceof Gamer) ? "GAMER" : "USER";
        return jwtTokenService.generateToken(user.getUid(), user.getUserid(), userType);
    }

    public Long getGamerIdFromToken(String authHeader) {
        try {
            // JWT 토큰에서 Bearer 제거
            String token = authHeader.replace("Bearer ", "");
            
            // JWT 토큰 유효성 검증
            if (!jwtTokenService.isTokenValid(token)) {
                throw new RuntimeException("Invalid or expired token");
            }
            
            // JWT에서 UID 추출
            Long userId = jwtTokenService.extractUserId(token);
            
            // UID로 사용자 조회
            User user = findByUid(userId);
            if (user == null || !(user instanceof Gamer)) {
                throw new RuntimeException("Gamer not found or not authorized");
            }
            
            return user.getUid();
        } catch (Exception e) {
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
    }
    
    public Long getUserIdFromToken(String authHeader) {
        try {
            // JWT 토큰에서 Bearer 제거
            String token = authHeader.replace("Bearer ", "");
            
            // JWT 토큰 유효성 검증
            if (!jwtTokenService.isTokenValid(token)) {
                throw new RuntimeException("Invalid or expired token");
            }
            
            // JWT에서 UID 추출
            return jwtTokenService.extractUserId(token);
        } catch (Exception e) {
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
    }
}