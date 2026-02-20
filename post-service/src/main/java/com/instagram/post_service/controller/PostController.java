package com.instagram.post_service.controller;

import com.instagram.post_service.entity.Post;
import com.instagram.post_service.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Tag(name = "Post Service", description = "Upravljanje objavama i medijima")
public class PostController {

    private final PostService postService;

    @Operation(summary = "Kreiranje novog posta sa medijima")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Post createPost(
            @RequestParam("description") String description,
            @RequestParam("userId") Long userId,
            @RequestParam("files") List<MultipartFile> files
    ) throws Exception {
        return postService.createPostWithMedia(description, userId, files);
    }

    @Operation(summary = "Listanje svih postova (feed)")
    @GetMapping
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }

    @Operation(summary = "Listanje postova po korisniku")
    @GetMapping("/user/{userId}")
    public List<Post> getPostsByUserId(@PathVariable Long userId) {
        return postService.getPostsByUserId(userId);
    }

    @Operation(summary = "Brisanje posta po ID-ju")
    @DeleteMapping("/{id}")
    public String deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return "Post sa ID-jem " + id + " je uspešno obrisan.";
    }

    @Operation(summary = "Ažuriranje posta")
    @PutMapping("/{id}")
    public Post updatePost(
            @PathVariable Long id,
            @RequestParam("description") String description,
            @RequestParam(value = "files", required = false) List<MultipartFile> files
    ) throws Exception {
        return postService.updatePost(id, description, files);
    }

}

