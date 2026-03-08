package com.instagram.post_service.controller;

import com.instagram.post_service.entity.Post;
import com.instagram.post_service.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    // Kreiranje posta sa fajlovima
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createPost(
            @RequestParam("description") String description,
            @RequestParam("userId") Long userId,
            @RequestParam(value = "files", required = false) List<MultipartFile> files
    ) {
        try {
            Post post = postService.createPostWithMedia(description, userId, files);
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Greška pri kreiranju posta: " + e.getMessage());
        }
    }

    // Brisanje posta po ID-ju
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body("Post sa ID-jem " + id + " je uspešno obrisan.");
    }

    // Dohvatanje svih postova
    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    // Dohvatanje jednog posta po ID-ju
    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(@PathVariable Long id) {
        try {
            Post post = postService.getPostById(id);
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Post nije pronađen: " + e.getMessage());
        }
    }

    // Update posta sa fajlovima
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updatePost(
            @PathVariable Long id,
            @RequestParam("description") String description,
            @RequestParam(value = "files", required = false) List<MultipartFile> files
    ) {
        try {
            Post updatedPost = postService.updatePost(id, description, files);
            return ResponseEntity.ok(updatedPost);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Greška pri ažuriranju posta: " + e.getMessage());
        }
    }
}