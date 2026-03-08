package com.instagram.auth_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Корисничко име је обавезно.")
    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @NotBlank(message = "Email је обавезан.")
    @Email(message = "Email није исправан.")
    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @NotBlank(message = "Име је обавезно.")
    @Column(nullable = false, length = 50)
    private String fname;

    @NotBlank(message = "Презиме је обавезно.")
    @Column(nullable = false, length = 50)
    private String lname;

    @NotBlank(message = "Шифра је обавезна.")
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(nullable = false)
    @Builder.Default
    private boolean blocked = false;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    private LocalDateTime updatedAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Collection<Role> roles = new HashSet<>();

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}