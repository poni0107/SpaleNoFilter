package com.instagram.auth_service.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JwtUtil Tests")
class JwtUtilTest {

    private JwtUtil jwtUtil;

    private static final String JWT_SECRET =
            "78ce5c76e64a0982fd941ebf1563a5016e1fdab51d8cbea376771b52138a7cf0";

    private static final int JWT_EXPIRATION = 3600000;

    @BeforeEach
    void setUp() {

        jwtUtil = new JwtUtil();

        ReflectionTestUtils.setField(jwtUtil, "jwtSecret", JWT_SECRET);
        ReflectionTestUtils.setField(jwtUtil, "expirationTime", JWT_EXPIRATION);

        jwtUtil.init();
    }

    @Nested
    @DisplayName("generateToken")
    class GenerateTokenTests {

        @Test
        @DisplayName("generiše token za korisnika")
        void generateToken() {

            String token = jwtUtil.generateToken("anita@test.com");

            assertThat(token).isNotNull();
            assertThat(token).isNotBlank();
        }

        @Test
        @DisplayName("različiti korisnici dobijaju različite tokene")
        void differentUsersDifferentTokens() {

            String token1 = jwtUtil.generateToken("anita@test.com");
            String token2 = jwtUtil.generateToken("mihajlo@test.com");

            assertThat(token1).isNotEqualTo(token2);
        }
    }

    @Nested
    @DisplayName("getUsernameFromToken")
    class UsernameExtractionTests {

        @Test
        @DisplayName("vraća username iz tokena")
        void extractUsername() {

            String token = jwtUtil.generateToken("anita@test.com");

            String username = jwtUtil.getUsernameFromToken(token);

            assertThat(username).isEqualTo("anita@test.com");
        }

        @Test
        @DisplayName("vraća null za nevalidan token")
        void invalidTokenReturnsNull() {

            String username = jwtUtil.getUsernameFromToken("invalid-token");

            assertThat(username).isNull();
        }
    }

    @Nested
    @DisplayName("validateJwtToken")
    class ValidateTokenTests {

        @Test
        @DisplayName("validan token vraća true")
        void validToken() {

            String token = jwtUtil.generateToken("anita@test.com");

            boolean valid = jwtUtil.validateJwtToken(token);

            assertThat(valid).isTrue();
        }

        @Test
        @DisplayName("nevalidan token vraća false")
        void invalidToken() {

            boolean valid = jwtUtil.validateJwtToken("fake.token.value");

            assertThat(valid).isFalse();
        }

        @Test
        @DisplayName("manipulisan token nije validan")
        void manipulatedToken() {

            String token = jwtUtil.generateToken("anita@test.com");

            String tamperedToken = token + "tamper";

            boolean valid = jwtUtil.validateJwtToken(tamperedToken);

            assertThat(valid).isFalse();
        }

        @Test
        @DisplayName("istekao token nije validan")
        void expiredToken() {

            Key key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes());

            String expiredToken = Jwts.builder()
                    .setSubject("anita@test.com")
                    .setIssuedAt(new Date(System.currentTimeMillis() - 20000))
                    .setExpiration(new Date(System.currentTimeMillis() - 10000))
                    .signWith(key)
                    .compact();

            boolean valid = jwtUtil.validateJwtToken(expiredToken);

            assertThat(valid).isFalse();
        }
    }
}