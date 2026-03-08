package com.instagram.auth_service.repository;

import com.instagram.auth_service.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("UserRepository (mocked)")
class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    private User activeUser;
    private User blockedUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        activeUser = User.builder()
                .id(1L)
                .username("anita123")
                .email("anita@test.com")
                .fname("Anita")
                .lname("Mijatović")
                .password("encoded123")
                .active(true)
                .blocked(false)
                .build();

        blockedUser = User.builder()
                .id(2L)
                .username("blockedUser")
                .email("blocked@test.com")
                .fname("Blocked")
                .lname("User")
                .password("encoded456")
                .active(true)
                .blocked(false)
                .build();
    }

    @Nested
    @DisplayName("findByEmail")
    class FindByEmail {

        @Test
        @DisplayName("vraća User kada email postoji")
        void whenEmailExists_returnsUser() {
            when(userRepository.findByEmail("anita@test.com")).thenReturn(Optional.of(activeUser));

            Optional<User> result = userRepository.findByEmail("anita@test.com");

            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(activeUser);
        }

        @Test
        @DisplayName("vraća empty kada email ne postoji")
        void whenEmailNotExists_returnsEmpty() {
            when(userRepository.findByEmail("ne.postoji@test.com")).thenReturn(Optional.empty());

            Optional<User> result = userRepository.findByEmail("ne.postoji@test.com");

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsByEmail")
    class ExistsByEmail {

        @Test
        @DisplayName("vraća true kada email postoji")
        void whenEmailExists_returnsTrue() {
            when(userRepository.existsByEmail("anita@test.com")).thenReturn(true);

            assertThat(userRepository.existsByEmail("anita@test.com")).isTrue();
        }

        @Test
        @DisplayName("vraća false kada email ne postoji")
        void whenEmailNotExists_returnsFalse() {
            when(userRepository.existsByEmail("ne.postoji@test.com")).thenReturn(false);

            assertThat(userRepository.existsByEmail("ne.postoji@test.com")).isFalse();
        }
    }

    @Nested
    @DisplayName("findAllActiveUsers & findAllBlockedUsers")
    class ActiveBlockedUsers {

        @Test
        @DisplayName("vraća samo aktivne korisnike")
        void findAllActiveUsers_returnsActiveOnly() {
            when(userRepository.findAllActiveUsers()).thenReturn(List.of(activeUser));

            List<User> activeUsers = userRepository.findAllActiveUsers();
            assertThat(activeUsers).contains(activeUser).doesNotContain(blockedUser);
        }

        @Test
        @DisplayName("vraća samo blokirane korisnike")
        void findAllBlockedUsers_returnsBlockedOnly() {
            when(userRepository.findAllBlockedUsers()).thenReturn(List.of(blockedUser));

            List<User> blockedUsers = userRepository.findAllBlockedUsers();
            assertThat(blockedUsers).contains(blockedUser).doesNotContain(activeUser);
        }
    }

    @Nested
    @DisplayName("searchByUsername")
    class SearchByUsername {

        @Test
        @DisplayName("pronađe korisnika po delu username-a")
        void searchByUsername_returnsMatchingUser() {
            when(userRepository.searchByUsername("anita")).thenReturn(List.of(activeUser));

            List<User> results = userRepository.searchByUsername("anita");
            assertThat(results).contains(activeUser).doesNotContain(blockedUser);
        }
    }

    @Nested
    @DisplayName("isEmailActive")
    class IsEmailActive {

        @Test
        @DisplayName("vraća true za aktivan email")
        void activeEmail_returnsTrue() {
            when(userRepository.isEmailActive("anita@test.com")).thenReturn(true);
            assertThat(userRepository.isEmailActive("anita@test.com")).isTrue();
        }

        @Test
        @DisplayName("vraća false za neaktivan email")
        void inactiveEmail_returnsFalse() {
            when(userRepository.isEmailActive("blocked@test.com")).thenReturn(false);
            assertThat(userRepository.isEmailActive("blocked@test.com")).isFalse();
        }

        @Test
        @DisplayName("vraća false za nepostojeći email")
        void nonExistentEmail_returnsFalse() {
            when(userRepository.isEmailActive("nepostoji@test.com")).thenReturn(false);
            assertThat(userRepository.isEmailActive("nepostoji@test.com")).isFalse();
        }
    }
}
