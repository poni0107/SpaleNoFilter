package com.instagram.auth_service;

import com.instagram.auth_service.security.JwtUtil;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final JwtUtil jwtUtil;

    // Spring će ubaciti JwtUtil ovde automatski
    public AuthService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public String login(String username, String password) {
        // fake check
        if ("test".equals(username) && "test".equals(password)) {
            // pozivamo generateToken preko instance, ne statički
            return jwtUtil.generateToken(username);
        }
        throw new RuntimeException("Invalid credentials");
    }
}