package com.lorecraft.tcglounge.service;

import com.lorecraft.tcglounge.entity.User;
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

    public User createUser(String username, String password, String email) {
        // 사용자 생성 로직
        User user = new User();
        user.setUserid(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setIsActive(true);
        
        return userRepository.save(user);
    }
}