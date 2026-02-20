package com.instagram.like_service.controller;

import com.instagram.like_service.model.Like;
import com.instagram.like_service.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    // ================= Kreiranje lajka =================
    @PostMapping
    public ResponseEntity<Like> addLike(@RequestParam Long userId, @RequestParam Long postId) {
        return ResponseEntity.ok(likeService.addLike(userId, postId));
    }

    // ================= Soft delete lajka =================
    @DeleteMapping
    public ResponseEntity<Void> removeLike(@RequestParam Long userId, @RequestParam Long postId) {
        likeService.removeLike(userId, postId);
        return ResponseEntity.noContent().build();
    }

    // ================= Dohvatanje lajkova po postu =================
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<Like>> getLikesByPost(@PathVariable Long postId) {
        return ResponseEntity.ok(likeService.getLikesByPost(postId));
    }

    // ================= Paginacija =================
    @GetMapping("/post/{postId}/page")
    public ResponseEntity<Page<Like>> getLikesByPostPage(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(likeService.getLikesByPost(postId, PageRequest.of(page, size)));
    }

    // ================= Provera da li korisnik lajkovao =================
    @GetMapping("/check")
    public ResponseEntity<Boolean> hasUserLiked(
            @RequestParam Long userId,
            @RequestParam Long postId
    ) {
        return ResponseEntity.ok(likeService.hasUserLiked(userId, postId));
    }

    // ================= Dohvatanje lajkova po korisniku =================
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Like>> getLikesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(likeService.getLikesByUser(userId));
    }

    @GetMapping("/user/{userId}/page")
    public ResponseEntity<Page<Like>> getLikesByUserPage(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(likeService.getLikesByUser(userId, PageRequest.of(page, size)));
    }

    // ================= Filter po vremenu =================
    @GetMapping("/post/{postId}/between")
    public ResponseEntity<List<Like>> getLikesByPostBetween(
            @PathVariable Long postId,
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end
    ) {
        return ResponseEntity.ok(likeService.getLikesByPostBetween(postId, start, end));
    }

    @GetMapping("/user/{userId}/between")
    public ResponseEntity<List<Like>> getLikesByUserBetween(
            @PathVariable Long userId,
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end
    ) {
        return ResponseEntity.ok(likeService.getLikesByUserBetween(userId, start, end));
    }

    // ================= Batch operacije =================
    @DeleteMapping("/post/{postId}/all")
    public ResponseEntity<Void> removeAllLikesByPost(@PathVariable Long postId) {
        likeService.removeAllLikesByPost(postId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/user/{userId}/all")
    public ResponseEntity<Void> removeAllLikesByUser(@PathVariable Long userId) {
        likeService.removeAllLikesByUser(userId);
        return ResponseEntity.noContent().build();
    }

    // ================= Statistika =================
    @GetMapping("/post/{postId}/count")
    public ResponseEntity<Long> countLikesByPost(@PathVariable Long postId) {
        return ResponseEntity.ok(likeService.countLikesByPost(postId));
    }

    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Long> countLikesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(likeService.countLikesByUser(userId));
    }
}


