package com.lorecraft.tcglounge.controller;

import com.lorecraft.tcglounge.dto.auth.LoginRequestDTO;
import com.lorecraft.tcglounge.dto.auth.LoginResponseDTO;
import com.lorecraft.tcglounge.entity.User;
import com.lorecraft.tcglounge.entity.Gamer;
import com.lorecraft.tcglounge.entity.Admin;
import com.lorecraft.tcglounge.entity.StoreOwner;
import com.lorecraft.tcglounge.service.AuthService;
import com.lorecraft.tcglounge.util.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        try {
            User user = authService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
            
            if (user == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "잘못된 사용자명 또는 비밀번호입니다.");
                return ResponseEntity.badRequest().body(response);
            }

            // JWT 토큰 생성 - userid를 사용
            String token = jwtTokenProvider.generateToken(user.getUserid());
            
            // 사용자 타입 결정
            String userType = determineUserType(user);
            
            LoginResponseDTO loginResponse = new LoginResponseDTO(token, user, userType);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "로그인 성공");
            response.put("data", loginResponse);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "로그인 처리 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        try {
            // TODO: JWT 토큰 블랙리스트 처리 (Redis 활용)
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "로그아웃 성공");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "로그아웃 처리 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String token) {
        try {
            // Bearer 토큰에서 실제 토큰 추출
            String jwtToken = token.replace("Bearer ", "");
            String username = jwtTokenProvider.getUsernameFromToken(jwtToken);
            
            if (username == null || !jwtTokenProvider.validateToken(jwtToken)) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "유효하지 않은 토큰입니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            User user = authService.findByUsername(username);
            String userType = determineUserType(user);
            
            Map<String, Object> userData = new HashMap<>();
            userData.put("userId", user.getUid());
            userData.put("username", user.getUserid());
            userData.put("userType", userType);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", userData);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "사용자 정보 조회 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    private String determineUserType(User user) {
        // 데이터베이스의 user_type 컬럼 값을 직접 사용
        String userType = user.getUserType();
        if (userType != null) {
            return userType;
        }
        
        // Fallback: 상속 구조를 확인하여 사용자 타입 결정
        if (user instanceof Gamer) {
            return "GAMER";
        } else if (user instanceof Admin) {
            return "ADMIN";
        } else if (user instanceof StoreOwner) {
            return "STORE_OWNER";
        }
        return "USER";
    }
}