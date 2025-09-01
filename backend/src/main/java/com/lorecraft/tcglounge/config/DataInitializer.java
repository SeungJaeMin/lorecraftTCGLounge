package com.lorecraft.tcglounge.config;

import com.lorecraft.tcglounge.domain.user.entity.Admin;
import com.lorecraft.tcglounge.domain.user.entity.Gamer;
import com.lorecraft.tcglounge.domain.user.entity.StoreOwner;
import com.lorecraft.tcglounge.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            initializeUsers();
            log.info("테스트 사용자 데이터 초기화 완료");
        }
    }

    private void initializeUsers() {
        // 테스트 게이머 생성
        Gamer gamer = Gamer.builder()
            .id("test_gamer")
            .pw(passwordEncoder.encode("password123"))
            .email("gamer@test.com")
            .phoneNumber("010-1234-5678")
            .nickname("테스트게이머")
            .registerDate(LocalDateTime.now())
            .isActive(true)
            .userType("GAMER")
            .totalWins(5)
            .totalLosses(3)
            .totalDraws(1)
            .currentRating(1200)
            .highestRating(1250)
            .build();

        // 테스트 점주 생성
        StoreOwner storeOwner = StoreOwner.builder()
            .id("test_store")
            .pw(passwordEncoder.encode("password123"))
            .email("store@test.com")
            .phoneNumber("010-2345-6789")
            .storeName("테스트 카드샵")
            .storeLocation("서울시 강남구")
            .storeZipcode("12345")
            .businessLicense("123-45-67890")
            .contactNumber("02-1234-5678")
            .registerDate(LocalDateTime.now())
            .isActive(true)
            .userType("STORE_OWNER")
            .isVerified(true)
            .isAuthorized(true)
            .build();

        // 테스트 관리자 생성
        Admin admin = Admin.builder()
            .id("test_admin")
            .pw(passwordEncoder.encode("password123"))
            .email("admin@test.com")
            .phoneNumber("010-3456-7890")
            .employeeId("EMP001")
            .department("개발팀")
            .registerDate(LocalDateTime.now())
            .isActive(true)
            .userType("ADMIN")
            .adminLevel(5)
            .canManageUsers(true)
            .canManageCompetitions(true)
            .canManageContent(true)
            .canManageSystem(true)
            .build();

        userRepository.save(gamer);
        userRepository.save(storeOwner);
        userRepository.save(admin);
        
        log.info("테스트 사용자 생성 완료:");
        log.info("- 게이머: test_gamer / password123");
        log.info("- 점주: test_store / password123");  
        log.info("- 관리자: test_admin / password123");
    }
}