package com.instagram.auth_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.instagram.auth_service.entity.Role;
import com.instagram.auth_service.entity.User;
import com.instagram.auth_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

class CustomUserDetailsServiceTest {

    private UserRepository userRepository;
    private CustomUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userDetailsService = new CustomUserDetailsService(userRepository);
    }

    @Test
    void testLoadUserByUsername_UserExists() {
        // priprema podataka
        Role role = new Role();
        role.setName("ROLE_USER");
        User user = User.builder()
                .username("anita")
                .password("password123")
                .roles(Set.of(role))
                .build();

        when(userRepository.findByUsernameOrEmail("anita", "anita")).thenReturn(Optional.of(user));

        // akcija
        UserDetails userDetails = userDetailsService.loadUserByUsername("anita");

        // provera
        assertEquals("anita", userDetails.getUsername());
        assertEquals("password123", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        when(userRepository.findByUsernameOrEmail("unknown", "unknown")).thenReturn(Optional.empty());

        // provera izuzetka
        assertThrows(UsernameNotFoundException.class, () ->
                userDetailsService.loadUserByUsername("unknown")
        );
    }
}