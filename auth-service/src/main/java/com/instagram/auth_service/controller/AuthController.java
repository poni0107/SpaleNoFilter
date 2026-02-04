package com.instagram.auth_service.controller;

import com.instagram.auth_service.model.AuthUser;
import com.instagram.auth_service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public String login(@RequestBody AuthUser user) {
        return authService.login(user.getUsername(), user.getPassword());
    }
}
