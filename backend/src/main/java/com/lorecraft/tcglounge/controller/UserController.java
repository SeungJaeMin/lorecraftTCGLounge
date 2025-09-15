package com.lorecraft.tcglounge.controller;

import com.lorecraft.tcglounge.entity.User;
import com.lorecraft.tcglounge.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
    
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerGamer(@RequestBody Map<String, String> request) {
        try {
            String userid = request.get("userid");
            String password = request.get("password");
            String nickname = request.get("nickname");
            String email = request.get("email");
            
            User user = userService.createGamer(userid, password, nickname, email);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User registered successfully");
            response.put("userId", user.getUid());
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PutMapping("/{id}/profile")
    public ResponseEntity<Map<String, Object>> updateUserProfile(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            String nickname = request.get("nickname");
            String email = request.get("email");
            
            User user = userService.updateUserProfile(id, nickname, email);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Profile updated successfully");
            response.put("user", user);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PutMapping("/{id}/password")
    public ResponseEntity<Map<String, Object>> changePassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            String currentPassword = request.get("currentPassword");
            String newPassword = request.get("newPassword");
            
            if (currentPassword == null || newPassword == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Current password and new password are required");
                return ResponseEntity.badRequest().body(response);
            }
            
            userService.changePassword(id, currentPassword, newPassword);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Password changed successfully");
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
}