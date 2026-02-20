package com.instagram.like_service.repository;

import com.instagram.like_service.model.Like;
import com.instagram.like_service.model.Like.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    // ================= Osnovne metode =================
    Optional<Like> findByUserIdAndPostId(Long userId, Long postId);

    List<Like> findByPostId(Long postId);

    void deleteByUserIdAndPostId(Long userId, Long postId);

    boolean existsByUserIdAndPostId(Long userId, Long postId);

    // ================= Filtriranje po statusu =================
    List<Like> findByPostIdAndStatus(Long postId, Status status);

    List<Like> findByUserIdAndStatus(Long userId, Status status);

    Optional<Like> findByUserIdAndPostIdAndStatus(Long userId, Long postId, Status status);

    boolean existsByUserIdAndPostIdAndStatus(Long userId, Long postId, Status status);

    // ================= Brojanje lajkova =================
    long countByPostId(Long postId);

    long countByPostIdAndStatus(Long postId, Status status);

    long countByUserId(Long userId);

    long countByUserIdAndStatus(Long userId, Status status);

    // ================= Filtriranje po vremenskom periodu =================
    List<Like> findByPostIdAndCreatedAtBetween(Long postId, LocalDateTime start, LocalDateTime end);

    List<Like> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);

    List<Like> findByPostIdAndStatusAndCreatedAtBetween(Long postId, Status status, LocalDateTime start, LocalDateTime end);

    // ================= Paginacija =================
    Page<Like> findByPostId(Long postId, Pageable pageable);

    Page<Like> findByPostIdAndStatus(Long postId, Status status, Pageable pageable);

    Page<Like> findByUserId(Long userId, Pageable pageable);

    Page<Like> findByUserIdAndStatus(Long userId, Status status, Pageable pageable);

    // ================= Batch operacije =================
    void deleteAllByPostId(Long postId);

    void deleteAllByUserId(Long userId);

    void deleteAllByPostIdAndStatus(Long postId, Status status);

    void deleteAllByUserIdAndStatus(Long userId, Status status);

    // ================= Provera aktivnih lajkova =================
    default boolean isActiveLikeExists(Long userId, Long postId) {
        return existsByUserIdAndPostIdAndStatus(userId, postId, Status.ACTIVE);
    }

    List<Like> findByUserIdAndStatusAndCreatedAtBetween(Long userId, Status status, LocalDateTime start, LocalDateTime end);
}
