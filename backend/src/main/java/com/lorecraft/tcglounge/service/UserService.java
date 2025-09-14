package com.lorecraft.tcglounge.service;

import com.lorecraft.tcglounge.entity.Gamer;
import com.lorecraft.tcglounge.entity.User;
import com.lorecraft.tcglounge.repository.UserRepository;
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
    
    public User updateUserProfile(Long userId, String nickname, String email) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        
        // Check if email is already taken by another user
        if (email != null && !email.equals(user.getEmail())) {
            Optional<User> existingUser = userRepository.findByEmail(email);
            if (existingUser.isPresent() && !existingUser.get().getUid().equals(userId)) {
                throw new RuntimeException("Email already exists: " + email);
            }
            user.setEmail(email);
        }
        
        // Update nickname if provided
        if (nickname != null && !nickname.trim().isEmpty()) {
            user.setNickname(nickname.trim());
        }
        
        User updated = userRepository.save(user);
        log.info("Updated user profile: {}", user.getUserid());
        return updated;
    }
    
    public User changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        
        // In a real application, you would verify the current password with proper hashing
        // For now, we'll do a simple comparison (this should use BCrypt in production)
        if (!user.getPassword().equals(currentPassword)) {
            throw new RuntimeException("Current password is incorrect");
        }
        
        user.setPassword(newPassword);
        User updated = userRepository.save(user);
        log.info("Password changed for user: {}", user.getUserid());
        return updated;
    }
}