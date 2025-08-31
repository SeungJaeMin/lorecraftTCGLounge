package com.lorecraft.tcglounge.domain.user.service;

import com.lorecraft.tcglounge.domain.user.entity.Admin;
import com.lorecraft.tcglounge.domain.user.entity.Gamer;
import com.lorecraft.tcglounge.domain.user.entity.StoreOwner;
import com.lorecraft.tcglounge.domain.user.entity.User;
import com.lorecraft.tcglounge.domain.user.repository.UserRepository;
import com.lorecraft.tcglounge.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public String login(String userid, String password) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(userid, password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtUtil.generateToken(authentication);
    }

    public User registerGamer(String userid, String password, String nickname, String email, String phoneNumber) {
        validateUserRegistration(userid, email);

        Gamer gamer = Gamer.builder()
            .id(userid)
            .pw(passwordEncoder.encode(password))
            .nickname(nickname)
            .email(email)
            .phoneNumber(phoneNumber)
            .registerDate(LocalDateTime.now())
            .isActive(true)
            .userType("GAMER")
            .totalWins(0)
            .totalLosses(0)
            .totalDraws(0)
            .currentRating(1000)
            .highestRating(1000)
            .build();

        return userRepository.save(gamer);
    }

    public User registerStoreOwner(String userid, String password, String email, String phoneNumber,
                                   String storeName, String storeLocation, String storeZipcode,
                                   String businessLicense, String contactNumber) {
        validateUserRegistration(userid, email);

        StoreOwner storeOwner = StoreOwner.builder()
            .id(userid)
            .pw(passwordEncoder.encode(password))
            .email(email)
            .phoneNumber(phoneNumber)
            .registerDate(LocalDateTime.now())
            .isActive(true)
            .userType("STORE_OWNER")
            .storeName(storeName)
            .storeLocation(storeLocation)
            .storeZipcode(storeZipcode)
            .businessLicense(businessLicense)
            .contactNumber(contactNumber)
            .isVerified(false)
            .isAuthorized(false)
            .build();

        return userRepository.save(storeOwner);
    }

    public User registerAdmin(String userid, String password, String email, String phoneNumber,
                             String employeeId, String department) {
        validateUserRegistration(userid, email);

        Admin admin = Admin.builder()
            .id(userid)
            .pw(passwordEncoder.encode(password))
            .email(email)
            .phoneNumber(phoneNumber)
            .registerDate(LocalDateTime.now())
            .isActive(true)
            .userType("ADMIN")
            .adminLevel(1)
            .employeeId(employeeId)
            .department(department)
            .canManageUsers(false)
            .canManageCompetitions(false)
            .canManageContent(false)
            .canManageSystem(false)
            .build();

        return userRepository.save(admin);
    }

    private void validateUserRegistration(String userid, String email) {
        if (userRepository.existsByIdField(userid)) {
            throw new IllegalArgumentException("이미 존재하는 사용자 ID입니다.");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
    }

    @Transactional(readOnly = true)
    public boolean existsByUserid(String userid) {
        return userRepository.existsByIdField(userid);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        user.deactivate();
    }

    public void activateUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        user.activate();
    }
}