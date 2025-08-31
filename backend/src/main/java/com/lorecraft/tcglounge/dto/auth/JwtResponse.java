package com.lorecraft.tcglounge.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    
    private String token;
    private String tokenType = "Bearer";
    private Long id;
    private String userid;
    private String email;
    private String userType;
    
    public JwtResponse(String token, Long id, String userid, String email, String userType) {
        this.token = token;
        this.id = id;
        this.userid = userid;
        this.email = email;
        this.userType = userType;
    }
}