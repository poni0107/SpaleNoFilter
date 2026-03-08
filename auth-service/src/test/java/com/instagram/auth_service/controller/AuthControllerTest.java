package com.instagram.auth_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.instagram.auth_service.dto.LoginRequest;
import com.instagram.auth_service.dto.RegisterRequest;
import com.instagram.auth_service.entity.User;
import com.instagram.auth_service.repository.UserRepository;
import com.instagram.auth_service.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController Test")
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthController authController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .build();
    }

    @Nested
    @DisplayName("POST /api/auth/register")
    class Register {

        @Test
        @DisplayName("Uspešna registracija korisnika Anita Mijatovic")
        void register_success() throws Exception {

            RegisterRequest request = new RegisterRequest();
            request.setUsername("anitamijatovic");
            request.setEmail("anita.mijatovic@test.com");
            request.setPassword("password123");
            request.setFname("Anita");
            request.setLname("Mijatovic");

            User savedUser = User.builder()
                    .id(1L)
                    .username("anitamijatovic")
                    .email("anita.mijatovic@test.com")
                    .password("encodedPassword")
                    .build();

            when(userRepository.existsByUsername(any())).thenReturn(false);
            when(userRepository.existsByEmail(any())).thenReturn(false);
            when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
            when(userRepository.save(any())).thenReturn(savedUser);

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.userId").value(1));
        }

        @Test
        @DisplayName("Username već postoji (Mihajlo Spasic)")
        void username_exists() throws Exception {

            RegisterRequest request = new RegisterRequest();
            request.setUsername("mihajlospasic");
            request.setEmail("mihajlo.spasic@test.com");

            when(userRepository.existsByUsername(any())).thenReturn(true);

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.error").value("Корисничко име је већ заузето!"));
        }

        @Test
        @DisplayName("Email već postoji (Mihajlo Spasic)")
        void email_exists() throws Exception {

            RegisterRequest request = new RegisterRequest();
            request.setUsername("mihajlospasic");
            request.setEmail("mihajlo.spasic@test.com");

            when(userRepository.existsByUsername(any())).thenReturn(false);
            when(userRepository.existsByEmail(any())).thenReturn(true);

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.error").value("Email је већ заузет!"));
        }
    }

    @Nested
    @DisplayName("POST /api/auth/login")
    class Login {

        @Test
        @DisplayName("Uspešan login korisnika Anita Mijatovic")
        void login_success() throws Exception {

            LoginRequest request = new LoginRequest();
            request.setUsernameOrEmail("anita.mijatovic@test.com");
            request.setPassword("password123");

            when(jwtUtil.generateToken(any())).thenReturn("jwt-token-anita");

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value("jwt-token-anita"));
        }

        @Test
        @DisplayName("Pogrešni kredencijali (Mihajlo Spasic)")
        void bad_credentials() throws Exception {

            LoginRequest request = new LoginRequest();
            request.setUsernameOrEmail("mihajlo.spasic@test.com");
            request.setPassword("wrongPassword");

            when(authenticationManager.authenticate(any()))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.error")
                            .value("Подаци које сте унели нису исправни."));
        }
    }
}
