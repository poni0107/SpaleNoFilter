package com.instagram.post_service.service.impl;

import com.instagram.post_service.entity.Post;
import com.instagram.post_service.repository.PostRepository;
import com.instagram.post_service.service.PostService;
import io.minio.MinioClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class PostServiceImpl extends PostService {

    private final PostRepository postRepository;

    public PostServiceImpl(PostRepository postRepository, MinioClient minioClient, PostRepository postRepository1) {
        super(postRepository, minioClient);
        this.postRepository = postRepository1;
    }

    @Override
    public Post createPostWithMedia(String description, Long userId, List<MultipartFile> files) {
        Post post = Post.builder()
                .description(description)
                .userId(userId)
                .build();
        return postRepository.save(post);
    }

    @Override
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    @Override
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @Override
    public Post getPostById(Long id) {
        Optional<Post> postOpt = postRepository.findById(id);
        return postOpt.orElseThrow(() -> new RuntimeException("Post sa ID-jem " + id + " ne postoji"));
    }
}