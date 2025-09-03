package com.lorecraft.tcglounge.domain.user.service;

import com.lorecraft.tcglounge.domain.user.entity.Gamer;
import com.lorecraft.tcglounge.domain.user.entity.User;
import com.lorecraft.tcglounge.domain.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public User createGamer(String userid, String password, String nickname, String email) {
        if (userRepository.existsByUserid(userid)) {
            throw new RuntimeException("User ID already exists: " + userid);
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists: " + email);
        }
        
        Gamer gamer = new Gamer(userid, password, nickname, email);
        
        User saved = userRepository.save(gamer);
        log.info("Created new gamer: {}", userid);
        return saved;
    }
    
    @Transactional(readOnly = true)
    public Optional<User> findByUserid(String userid) {
        return userRepository.findByUserid(userid);
    }
    
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }
}