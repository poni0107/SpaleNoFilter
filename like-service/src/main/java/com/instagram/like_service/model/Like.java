package com.instagram.like_service.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entitet koji predstavlja "Like" akciju korisnika na određenom postu.
 * Implementira sve osnovne i napredne atribute za praćenje lajkovanja.
 */
@Entity
@Table(
        name = "likes",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "post_id"})},
        indexes = {
                @Index(name = "idx_like_user", columnList = "user_id"),
                @Index(name = "idx_like_post", columnList = "post_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ID korisnika koji je lajkovao post.
     * Može se zameniti @ManyToOne relacijom prema entitetu User
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * ID posta koji je lajkovan.
     * Može se zameniti @ManyToOne relacijom prema entitetu Post
     */
    @Column(name = "post_id", nullable = false)
    private Long postId;

    /**
     * Datum i vreme kada je like kreiran
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Datum i vreme poslednje izmene lajka (npr. za undo/redo funkcionalnosti)
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Status lajka – koristan za soft delete ili kasnije proširenje
     * ACTIVE – lajkovan, REMOVED – uklonjen
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.ACTIVE;

    /**
     * Default konstruktor sa inicijalizacijom createdAt i status
     */
    public Like(Long userId, Long postId) {
        this.userId = userId;
        this.postId = postId;
        this.status = Status.ACTIVE;
    }

    /**
     * Enum za status lajka
     */
    public enum Status {
        ACTIVE,
        REMOVED
    }

    /**
     * Metoda za soft delete lajka
     */
    public void remove() {
        this.status = Status.REMOVED;
    }

    /**
     * Provera da li je lajka aktivan
     */
    public boolean isActive() {
        return this.status == Status.ACTIVE;
    }

    // ================= Moguće buduće proširenje =================
    // Primer relacije ka User entitetu:
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "user_id", insertable = false, updatable = false)
    // private User user;

    // Primer relacije ka Post entitetu:
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "post_id", insertable = false, updatable = false)
    // private Post post;

    // Dodavanje polja za tracking IP adrese korisnika koji je lajkovao
    // private String ipAddress;

    // Dodavanje polja za geo lokaciju lajka (npr. grad/zemlja)
    // private String location;
}


