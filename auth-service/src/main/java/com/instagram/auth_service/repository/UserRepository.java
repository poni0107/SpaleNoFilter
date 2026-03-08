package com.instagram.auth_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.instagram.auth_service.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsernameOrEmail(String username, String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    // Aktivni korisnici
    @Query("SELECT u FROM User u WHERE u.isActive = true")
    List<User> findAllActiveUsers();

    // Korisnici koji su blokirani
    @Query("SELECT u FROM User u WHERE u.isBlocked = true")
    List<User> findAllBlockedUsers();

    // Pretraga po username (korisno za follow/search)
    @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%')) AND u.isActive = true")
    List<User> searchByUsername(String query);

    // Provera da li je nalog aktivan po email-u
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email AND u.isActive = true")
    boolean isEmailActive(String email);

}