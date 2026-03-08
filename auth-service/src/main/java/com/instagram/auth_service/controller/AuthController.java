package com.instagram.auth_service.controller;

import com.instagram.auth_service.dto.LoginRequest;
import com.instagram.auth_service.dto.RegisterRequest;
import com.instagram.auth_service.entity.User;
import com.instagram.auth_service.repository.UserRepository;
import com.instagram.auth_service.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
                          UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            // autentifikacija
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsernameOrEmail(),
                            request.getPassword()
                    )
            );

            // generisanje JWT tokena preko instance
            String token = jwtUtil.generateToken(request.getUsernameOrEmail());

            return ResponseEntity.ok(Map.of("token", token));

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Подаци које сте унели нису исправни."));
        } catch (Exception ex) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Дошло је до грешке."));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        // provera da li je username zauzet
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.status(409)
                    .body(Map.of("error", "Корисничко име је већ заузето!"));
        }

        // provera da li je email zauzet
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(409)
                    .body(Map.of("error", "Email је већ заузет!"));
        }

        // enkodiranje lozinke
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // kreiranje i čuvanje korisnika
        User newUser = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .fname(request.getFname())
                .lname(request.getLname())
                .password(encodedPassword)
                .build();

        User savedUser = userRepository.save(newUser);

        return ResponseEntity.status(201)
                .body(Map.of("message", "Успешно сте регистровани!", "userId", savedUser.getId()));
    }
}