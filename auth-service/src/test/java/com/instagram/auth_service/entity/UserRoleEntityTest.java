package com.instagram.auth_service.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("User and Role Entity Tests")
class UserRoleEntityTest {

    @Test
    @DisplayName("User builder correctly sets fields")
    void userBuilder_setsFieldsCorrectly() {

        User user = User.builder()
                .id(1L)
                .username("anita")
                .email("anita@test.com")
                .fname("Anita")
                .lname("Mijatovic")
                .password("test123")
                .build();

        assertThat(user.getUsername()).isEqualTo("anita");
        assertThat(user.getEmail()).isEqualTo("anita@test.com");
        assertThat(user.getFname()).isEqualTo("Anita");
        assertThat(user.getLname()).isEqualTo("Mijatovic");
        assertThat(user.getPassword()).isEqualTo("test123");
    }

    @Test
    @DisplayName("User has default values for active and createdAt")
    void user_hasDefaultValues() {

        User user = User.builder()
                .username("mihajlo")
                .email("mihajlo@test.com")
                .fname("Mihajlo")
                .lname("Spasic")
                .password("123456")
                .build();

        assertThat(user.isActive()).isTrue();
        assertThat(user.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("User roles collection initialized")
    void user_rolesInitialized() {

        User user = User.builder()
                .username("anita")
                .email("anita@test.com")
                .fname("Anita")
                .lname("Mijatovic")
                .password("test123")
                .build();

        Collection<Role> roles = user.getRoles();

        assertThat(roles).isNotNull();
        assertThat(roles).isEmpty();
    }

    @Test
    @DisplayName("onUpdate sets updatedAt timestamp")
    void onUpdate_setsUpdatedAt() {

        User user = User.builder()
                .username("anita")
                .email("anita@test.com")
                .fname("Anita")
                .lname("Mijatovic")
                .password("test123")
                .build();

        user.onUpdate();

        assertThat(user.getUpdatedAt()).isNotNull();
        assertThat(user.getUpdatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("Role entity stores id and name")
    void role_fieldsWork() {

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");

        assertThat(role.getId()).isEqualTo(1L);
        assertThat(role.getName()).isEqualTo("ROLE_USER");
    }

    @Test
    @DisplayName("User can contain roles")
    void user_canContainRoles() {

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = User.builder()
                .username("mihajlo")
                .email("mihajlo@test.com")
                .fname("Mihajlo")
                .lname("Spasic")
                .password("123456")
                .build();

        user.getRoles().add(role);

        assertThat(user.getRoles()).hasSize(1);
        assertThat(user.getRoles().iterator().next().getName())
                .isEqualTo("ROLE_ADMIN");
    }
}