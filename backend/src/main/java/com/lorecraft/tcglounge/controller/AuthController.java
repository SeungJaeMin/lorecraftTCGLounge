package com.lorecraft.tcglounge.controller;

import com.lorecraft.tcglounge.domain.user.entity.User;
import com.lorecraft.tcglounge.domain.user.service.AuthService;
import com.lorecraft.tcglounge.dto.auth.JwtResponse;
import com.lorecraft.tcglounge.dto.auth.LoginRequest;
import com.lorecraft.tcglounge.dto.auth.SignupRequest;
import com.lorecraft.tcglounge.dto.common.ApiResponse;
import com.lorecraft.tcglounge.infrastructure.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            String token = authService.login(loginRequest.getUserid(), loginRequest.getPassword());
            
            // 현재 인증된 사용자 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            
            JwtResponse jwtResponse = new JwtResponse(
                token,
                userPrincipal.getId(),
                userPrincipal.getUsername(),
                userPrincipal.getEmail(),
                userPrincipal.getUserType()
            );
            
            return ResponseEntity.ok(ApiResponse.success("로그인 성공", jwtResponse));
            
        } catch (Exception e) {
            log.error("로그인 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("로그인에 실패했습니다: " + e.getMessage()));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<String>> signup(@Valid @RequestBody SignupRequest signupRequest) {
        try {
            User user;
            
            switch (signupRequest.getUserType()) {
                case "GAMER":
                    user = authService.registerGamer(
                        signupRequest.getUserid(),
                        signupRequest.getPassword(),
                        signupRequest.getNickname(),
                        signupRequest.getEmail(),
                        signupRequest.getPhoneNumber()
                    );
                    break;
                    
                case "STORE_OWNER":
                    user = authService.registerStoreOwner(
                        signupRequest.getUserid(),
                        signupRequest.getPassword(),
                        signupRequest.getEmail(),
                        signupRequest.getPhoneNumber(),
                        signupRequest.getStoreName(),
                        signupRequest.getStoreLocation(),
                        signupRequest.getStoreZipcode(),
                        signupRequest.getBusinessLicense(),
                        signupRequest.getContactNumber()
                    );
                    break;
                    
                case "ADMIN":
                    user = authService.registerAdmin(
                        signupRequest.getUserid(),
                        signupRequest.getPassword(),
                        signupRequest.getEmail(),
                        signupRequest.getPhoneNumber(),
                        signupRequest.getEmployeeId(),
                        signupRequest.getDepartment()
                    );
                    break;
                    
                default:
                    return ResponseEntity.badRequest()
                        .body(ApiResponse.error("유효하지 않은 사용자 유형입니다."));
            }
            
            return ResponseEntity.ok(ApiResponse.success(
                "회원가입이 완료되었습니다.", 
                "사용자 ID: " + user.getId()
            ));
            
        } catch (IllegalArgumentException e) {
            log.error("회원가입 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("회원가입 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("회원가입 중 오류가 발생했습니다."));
        }
    }

    @GetMapping("/check/userid/{userid}")
    public ResponseEntity<ApiResponse<Boolean>> checkUseridAvailability(@PathVariable String userid) {
        boolean isAvailable = !authService.existsByUserid(userid);
        return ResponseEntity.ok(ApiResponse.success(
            isAvailable ? "사용 가능한 아이디입니다." : "이미 사용 중인 아이디입니다.",
            isAvailable
        ));
    }

    @GetMapping("/check/email/{email}")
    public ResponseEntity<ApiResponse<Boolean>> checkEmailAvailability(@PathVariable String email) {
        boolean isAvailable = !authService.existsByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(
            isAvailable ? "사용 가능한 이메일입니다." : "이미 사용 중인 이메일입니다.",
            isAvailable
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserPrincipal>> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(ApiResponse.success("사용자 정보 조회 성공", userPrincipal));
    }
}