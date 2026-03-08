package com.instagram.auth_service.service;

import com.instagram.auth_service.AuthService;
import com.instagram.auth_service.security.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
class AuthServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Nested
    @DisplayName("Login functionality")
    class LoginTests {

        @Test
        @DisplayName("Successful login returns JWT token")
        void login_success() {

            // arrange
            when(jwtUtil.generateToken("test")).thenReturn("jwt-token-123");

            // act
            String token = authService.login("test", "test");

            // assert
            assertThat(token).isNotNull();
            assertThat(token).isEqualTo("jwt-token-123");

            verify(jwtUtil).generateToken("test");
        }

        @Test
        @DisplayName("Login fails when username is incorrect")
        void login_wrong_username() {

            assertThatThrownBy(() ->
                    authService.login("AnitaMijatovic", "test")
            )
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Invalid credentials");

            verify(jwtUtil, never()).generateToken(any());
        }

        @Test
        @DisplayName("Login fails when password is incorrect")
        void login_wrong_password() {

            assertThatThrownBy(() ->
                    authService.login("test", "wrongPassword")
            )
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Invalid credentials");

            verify(jwtUtil, never()).generateToken(any());
        }

        @Test
        @DisplayName("Login fails when both username and password are incorrect")
        void login_both_wrong() {

            assertThatThrownBy(() ->
                    authService.login("MihajloSpasic", "123456")
            )
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Invalid credentials");

            verify(jwtUtil, never()).generateToken(any());
        }

        @Test
        @DisplayName("Token is generated exactly once for successful login")
        void login_token_generated_once() {

            when(jwtUtil.generateToken("test")).thenReturn("jwt-token-xyz");

            String token = authService.login("test", "test");

            assertThat(token).isEqualTo("jwt-token-xyz");

            verify(jwtUtil, times(1)).generateToken("test");
        }
    }
}

