package com.lorecraft.tcglounge.controller;

import com.lorecraft.tcglounge.config.ErrorCode;
import com.lorecraft.tcglounge.config.MessageConstants;
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
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            String token = authService.login(loginRequest.getUserid(), loginRequest.getPassword());
            
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            
            JwtResponse jwtResponse = new JwtResponse(
                token,
                userPrincipal.getId(),
                userPrincipal.getUsername(),
                userPrincipal.getEmail(),
                userPrincipal.getUserType()
            );
            
            return ResponseEntity.ok(ApiResponse.success(MessageConstants.Success.LOGIN, jwtResponse));
            
        } catch (Exception e) {
            log.error(MessageConstants.Error.LOGIN_FAILED + e.getMessage());
            return ResponseEntity.status(ErrorCode.AUTH_LOGIN_FAILED.getHttpStatus())
                .body(ApiResponse.error(MessageConstants.Error.LOGIN_FAILED + e.getMessage()));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<String>> signup(@Valid @RequestBody SignupRequest signupRequest) {
        try {
            User user;
            
            switch (signupRequest.getUserType()) {
                case MessageConstants.UserType.GAMER:
                    user = authService.registerGamer(
                        signupRequest.getUserid(),
                        signupRequest.getPassword(),
                        signupRequest.getNickname(),
                        signupRequest.getEmail(),
                        signupRequest.getPhoneNumber()
                    );
                    break;
                    
                case MessageConstants.UserType.STORE_OWNER:
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
                    
                case MessageConstants.UserType.ADMIN:
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
                    return ResponseEntity.status(ErrorCode.USER_INVALID_TYPE.getHttpStatus())
                        .body(ApiResponse.error(MessageConstants.Error.INVALID_USER_TYPE));
            }
            
            return ResponseEntity.ok(ApiResponse.success(
                MessageConstants.Success.SIGNUP, 
                MessageConstants.Field.USER_ID + ": " + user.getId()
            ));
            
        } catch (IllegalArgumentException e) {
            log.error(MessageConstants.Error.SIGNUP_FAILED + e.getMessage());
            return ResponseEntity.status(ErrorCode.VALIDATION_FAILED.getHttpStatus())
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error(MessageConstants.Error.SIGNUP_FAILED + e.getMessage());
            return ResponseEntity.status(ErrorCode.USER_REGISTRATION_FAILED.getHttpStatus())
                .body(ApiResponse.error(MessageConstants.Error.INTERNAL_ERROR));
        }
    }

    @GetMapping("/check/userid/{userid}")
    public ResponseEntity<ApiResponse<Boolean>> checkUseridAvailability(@PathVariable String userid) {
        boolean isAvailable = !authService.existsByUserid(userid);
        String message = isAvailable ? MessageConstants.Success.USER_ID_AVAILABLE : MessageConstants.Success.USER_ID_UNAVAILABLE;
        return ResponseEntity.ok(ApiResponse.success(message, isAvailable));
    }

    @GetMapping("/check/email/{email}")
    public ResponseEntity<ApiResponse<Boolean>> checkEmailAvailability(@PathVariable String email) {
        boolean isAvailable = !authService.existsByEmail(email);
        String message = isAvailable ? MessageConstants.Success.EMAIL_AVAILABLE : MessageConstants.Success.EMAIL_UNAVAILABLE;
        return ResponseEntity.ok(ApiResponse.success(message, isAvailable));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserPrincipal>> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(ErrorCode.UNAUTHORIZED.getHttpStatus()).build();
        }
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.Success.USER_INFO_RETRIEVED, userPrincipal));
    }
}