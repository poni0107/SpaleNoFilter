package com.instagram.like_service.service;

import com.instagram.like_service.model.Like;
import com.instagram.like_service.model.Like.Status;
import com.instagram.like_service.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;

    // ================= Kreiranje lajka =================
    public Like addLike(Long userId, Long postId) {
        if (likeRepository.existsByUserIdAndPostIdAndStatus(userId, postId, Status.ACTIVE)) {
            throw new RuntimeException("User already liked this post");
        }
        Like like = new Like(userId, postId);
        like.setStatus(Status.ACTIVE);
        return likeRepository.save(like);
    }

    // ================= Soft delete lajka =================
    public void removeLike(Long userId, Long postId) {
        likeRepository.findByUserIdAndPostIdAndStatus(userId, postId, Status.ACTIVE)
                .ifPresent(like -> {
                    like.remove();
                    likeRepository.save(like);
                });
    }

    // ================= Dohvatanje lajkova po postu =================
    public List<Like> getLikesByPost(Long postId) {
        return likeRepository.findByPostIdAndStatus(postId, Status.ACTIVE);
    }

    public Page<Like> getLikesByPost(Long postId, Pageable pageable) {
        return likeRepository.findByPostIdAndStatus(postId, Status.ACTIVE, pageable);
    }

    // ================= Brojanje lajkova =================
    public long countLikesByPost(Long postId) {
        return likeRepository.countByPostIdAndStatus(postId, Status.ACTIVE);
    }

    public long countLikesByUser(Long userId) {
        return likeRepository.countByUserIdAndStatus(userId, Status.ACTIVE);
    }

    // ================= Provera da li je korisnik lajkovao =================
    public boolean hasUserLiked(Long userId, Long postId) {
        return likeRepository.isActiveLikeExists(userId, postId);
    }

    // ================= Dohvatanje lajkova po korisniku =================
    public List<Like> getLikesByUser(Long userId) {
        return likeRepository.findByUserIdAndStatus(userId, Status.ACTIVE);
    }

    public Page<Like> getLikesByUser(Long userId, Pageable pageable) {
        return likeRepository.findByUserIdAndStatus(userId, Status.ACTIVE, pageable);
    }

    // ================= Filter po vremenu =================
    public List<Like> getLikesByPostBetween(Long postId, LocalDateTime start, LocalDateTime end) {
        return likeRepository.findByPostIdAndStatusAndCreatedAtBetween(postId, Status.ACTIVE, start, end);
    }

    public List<Like> getLikesByUserBetween(Long userId, LocalDateTime start, LocalDateTime end) {
        return likeRepository.findByUserIdAndStatusAndCreatedAtBetween(userId, Status.ACTIVE, start, end);
    }

    // ================= Batch operacije =================
    public void removeAllLikesByPost(Long postId) {
        likeRepository.findByPostIdAndStatus(postId, Status.ACTIVE)
                .forEach(like -> {
                    like.remove();
                    likeRepository.save(like);
                });
    }

    public void removeAllLikesByUser(Long userId) {
        likeRepository.findByUserIdAndStatus(userId, Status.ACTIVE)
                .forEach(like -> {
                    like.remove();
                    likeRepository.save(like);
                });
    }
}


