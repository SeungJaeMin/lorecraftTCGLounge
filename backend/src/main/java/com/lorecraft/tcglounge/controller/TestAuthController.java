package com.lorecraft.tcglounge.controller;

import com.lorecraft.tcglounge.entity.User;
import com.lorecraft.tcglounge.entity.Gamer;
import com.lorecraft.tcglounge.entity.Admin;
import com.lorecraft.tcglounge.entity.StoreOwner;
import com.lorecraft.tcglounge.repository.UserRepository;
import com.lorecraft.tcglounge.repository.GamerRepository;
import com.lorecraft.tcglounge.repository.AdminRepository;
import com.lorecraft.tcglounge.repository.StoreOwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/test")
@CrossOrigin(origins = "http://localhost:3000")
public class TestAuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GamerRepository gamerRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private StoreOwnerRepository storeOwnerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/create-test-users")
    public Map<String, Object> createTestUsers() {
        try {
            // 테스트 게이머 1
            if (!userRepository.existsByUserid("testgamer")) {
                Gamer gamer = new Gamer();
                gamer.setUserid("testgamer");
                gamer.setPassword(passwordEncoder.encode("123"));
                gamer.setEmail("testgamer@example.com");
                gamer.setIsActive(true);
                gamer.setNickname("테스트게이머");
                gamer.setPhoneNumber("010-1234-5678");
                gamer.setUserType(User.UserType.GAMER.name());
                gamerRepository.save(gamer);
            }

            // 테스트 게이머 2
            if (!userRepository.existsByUserid("progamer")) {
                Gamer gamer = new Gamer();
                gamer.setUserid("progamer");
                gamer.setPassword(passwordEncoder.encode("123"));
                gamer.setEmail("progamer@example.com");
                gamer.setIsActive(true);
                gamer.setNickname("프로게이머");
                gamer.setPhoneNumber("010-2345-6789");
                gamer.setUserType(User.UserType.GAMER.name());
                gamerRepository.save(gamer);
            }

            // 관리자 계정 생성
            if (!userRepository.existsByUserid("admin")) {
                Admin admin = new Admin();
                admin.setUserid("admin");
                admin.setPassword(passwordEncoder.encode("123"));
                admin.setEmail("admin@example.com");
                admin.setIsActive(true);
                admin.setNickname("관리자");
                admin.setPhoneNumber("010-9999-0000");
                admin.setEmployeeId("EMP001");
                admin.setDepartment("IT관리부");
                admin.setUserType(User.UserType.ADMIN.name());
                adminRepository.save(admin);
            }

            // 매장 관리자 계정 생성
            if (!userRepository.existsByUserid("store01")) {
                StoreOwner storeOwner = new StoreOwner();
                storeOwner.setUserid("store01");
                storeOwner.setPassword(passwordEncoder.encode("123"));
                storeOwner.setEmail("store01@example.com");
                storeOwner.setIsActive(true);
                storeOwner.setNickname("매장관리자");
                storeOwner.setPhoneNumber("010-8888-0000");
                storeOwner.setStoreName("ESTELA 강남점");
                storeOwner.setStoreLocation("서울시 강남구");
                storeOwner.setContactNumber("02-1234-5678");
                storeOwner.setUserType(User.UserType.STORE_OWNER.name());
                storeOwnerRepository.save(storeOwner);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "테스트 사용자 생성 완료");
            response.put("accounts", Map.of(
                "gamer1", "testgamer / 123",
                "gamer2", "progamer / 123", 
                "admin", "admin / 123",
                "storeOwner", "store01 / 123"
            ));

            return response;

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "테스트 사용자 생성 실패: " + e.getMessage());
            return response;
        }
    }

    @GetMapping("/users")
    public Map<String, Object> getAllUsers() {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("users", userRepository.findAll());
            return response;
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return response;
        }
    }
}