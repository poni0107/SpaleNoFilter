package com.instagram.auth_service;

import com.instagram.auth_service.controller.AuthController;
import com.instagram.auth_service.dto.LoginRequest;
import com.instagram.auth_service.dto.RegisterRequest;
import com.instagram.auth_service.entity.User;
import com.instagram.auth_service.repository.UserRepository;
import com.instagram.auth_service.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceApplicationTests {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLogIn_Success() {
        LoginRequest request = new LoginRequest("testuser", "password");

        when(authenticationManager.authenticate(
                ArgumentMatchers.any(UsernamePasswordAuthenticationToken.class)
        )).thenReturn(new UsernamePasswordAuthenticationToken(request.getUsernameOrEmail(), request.getPassword()));

        when(jwtUtil.generateToken(request.getUsernameOrEmail())).thenReturn("fake-jwt-token");

        ResponseEntity<Map<String, Object>> response = (ResponseEntity<Map<String, Object>>) authController.login(request);

        assertEquals(200,
                response.getStatusCode().value());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertTrue(body.containsKey("token"));

        verify(authenticationManager, times(1)).authenticate(any());
        verify(jwtUtil, times(1)).generateToken(any());
    }

    @Test
    void testRegister_Success() {
        RegisterRequest request = new RegisterRequest("newuser", "new@example.com", "John", "Doe", "password");

        when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded-password");

        User savedUser = User.builder()
                .id(1L)
                .username(request.getUsername())
                .email(request.getEmail())
                .fname(request.getFname())
                .lname(request.getLname())
                .password("encoded-password")
                .build();

        when(userRepository.save(ArgumentMatchers.any(User.class))).thenReturn(savedUser);

        ResponseEntity<?> response = authController.register(request);

        assertEquals(201, response.getStatusCode().value());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertNotNull(body);
        assertTrue(((String) body.get("message")).contains("Успешно"));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegister_UsernameTaken() {
        RegisterRequest request = new RegisterRequest("existinguser", "test@example.com", "John", "Doe", "password");

        when(userRepository.existsByUsername(request.getUsername())).thenReturn(true);

        ResponseEntity<?> response = authController.register(request);

        assertEquals(409, response.getStatusCode().value());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertNotNull(body);
        assertEquals("Корисничко име је већ заузето!", body.get("error"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegister_EmailTaken() {
        RegisterRequest request = new RegisterRequest("newuser", "existing@example.com", "John", "Doe", "password");

        when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        ResponseEntity<?> response = authController.register(request);

        assertEquals(409,response.getStatusCode().value());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertNotNull(body);
        assertEquals("Email је већ заузет!", body.get("error"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testLogin_BadCredentials() {
        LoginRequest request = new LoginRequest("wronguser", "wrongpassword");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        ResponseEntity<?> response = authController.login(request);

        assertEquals(401, response.getStatusCode().value());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertNotNull(body);
        assertEquals("Подаци које сте унели нису исправни.", body.get("error"));
        verify(authenticationManager, times(1)).authenticate(any());
    }
}