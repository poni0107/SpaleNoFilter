package com.instagram.auth_service;

import com.instagram.auth_service.security.JwtUtil;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    public String login(String username, String password) {
        // fake check (za faks je OK)
        if ("test".equals(username) && "test".equals(password)) {
            return JwtUtil.generateToken(username);
        }
        throw new RuntimeException("Invalid credentials");
    }
}
