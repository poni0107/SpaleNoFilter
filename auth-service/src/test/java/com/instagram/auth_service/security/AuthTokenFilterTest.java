package com.instagram.auth_service.security;

import com.instagram.auth_service.service.CustomUserDetailsService;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthTokenFilter")
class AuthTokenFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private CustomUserDetailsService userDetailsService;

    private AuthTokenFilter filter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain filterChain;

    @BeforeEach
    void setUp() {

        filter = new AuthTokenFilter();

        ReflectionTestUtils.setField(filter, "jwtUtil", jwtUtil);
        ReflectionTestUtils.setField(filter, "userDetailsService", userDetailsService);

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("Without Authorization header")
    class WithoutAuthHeader {

        @Test
        @DisplayName("Does not authenticate user")
        void noHeader() throws ServletException, IOException {

            filter.doFilterInternal(request, response, filterChain);

            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

            verify(jwtUtil, never()).validateJwtToken(anyString());
        }
    }

    @Nested
    @DisplayName("Invalid Authorization header")
    class InvalidHeader {

        @Test
        @DisplayName("Header without Bearer prefix")
        void invalidFormat() throws ServletException, IOException {

            request.addHeader(HttpHeaders.AUTHORIZATION, "Basic token123");

            filter.doFilterInternal(request, response, filterChain);

            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

            verify(jwtUtil, never()).validateJwtToken(anyString());
        }
    }

    @Nested
    @DisplayName("Valid Bearer token")
    class ValidToken {

        @Test
        @DisplayName("Sets authentication in SecurityContext")
        void authenticateUser() throws ServletException, IOException {

            String token = "valid-jwt";

            request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);

            UserDetails userDetails =
                    new User("anita", "password", Collections.emptyList());

            when(jwtUtil.validateJwtToken(token)).thenReturn(true);
            when(jwtUtil.getUsernameFromToken(token)).thenReturn("anita");
            when(userDetailsService.loadUserByUsername("anita"))
                    .thenReturn(userDetails);

            filter.doFilterInternal(request, response, filterChain);

            Authentication auth =
                    SecurityContextHolder.getContext().getAuthentication();

            assertThat(auth).isNotNull();
            assertThat(auth.getPrincipal()).isEqualTo(userDetails);
        }

        @Test
        @DisplayName("Does not authenticate when token invalid")
        void invalidToken() throws ServletException, IOException {

            String token = "invalid";

            request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);

            when(jwtUtil.validateJwtToken(token)).thenReturn(false);

            filter.doFilterInternal(request, response, filterChain);

            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        }
    }

    @Nested
    @DisplayName("Exception handling")
    class ExceptionHandling {

        @Test
        @DisplayName("Returns 401 when exception occurs")
        void returnsUnauthorized() throws ServletException, IOException {

            String token = "broken";

            request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);

            when(jwtUtil.validateJwtToken(token)).thenThrow(new RuntimeException("JWT error"));

            filter.doFilterInternal(request, response, filterChain);

            assertThat(response.getStatus()).isEqualTo(401);
            assertThat(response.getContentAsString()).contains("Unauthorized");
        }
    }
}
