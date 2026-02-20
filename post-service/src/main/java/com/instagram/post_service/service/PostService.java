package com.instagram.post_service.service;

import com.instagram.post_service.entity.Post;
import com.instagram.post_service.entity.PostMedia;
import com.instagram.post_service.repository.PostRepository;
import io.minio.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucketName;

    @Value("${minio.url}")
    private String minioUrl;

    private static final int MAX_FILES = 20;
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024;

    private static final List<String> ALLOWED_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "image/webp",
            "video/mp4"
    );

    // ===================== PUBLIC API =====================

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public List<Post> getPostsByUserId(Long userId) {
        return postRepository.findByUserId(userId);
    }

    @Transactional
    public Post createPostWithMedia(String description, Long userId, List<MultipartFile> files) throws Exception {
        validateFiles(files);
        ensureBucketExists();

        Post post = Post.builder()
                .description(description)
                .userId(userId)
                .mediaFiles(new ArrayList<>())
                .build();

        uploadFiles(files, post);

        return postRepository.save(post);
    }

    @Transactional
    public Post updatePost(Long postId, String description, List<MultipartFile> files) throws Exception {
        Post post = findPost(postId);
        post.setDescription(description);

        if (files != null && !files.isEmpty()) {
            validateFiles(files);

            deleteExistingMedia(post);
            uploadFiles(files, post);
        }

        return postRepository.save(post);
    }

    @Transactional
    public void deletePost(Long postId) {
        Post post = findPost(postId);
        deleteExistingMedia(post);
        postRepository.delete(post);
    }

    // ===================== INTERNAL LOGIC =====================

    private Post findPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post nije pronađen!"));
    }

    private void validateFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) return;

        if (files.size() > MAX_FILES) {
            throw new RuntimeException("Dozvoljeno je maksimalno " + MAX_FILES + " fajlova.");
        }

        for (MultipartFile file : files) {
            if (file.getSize() > MAX_FILE_SIZE) {
                throw new RuntimeException("Fajl " + file.getOriginalFilename() + " prelazi 50MB.");
            }

            if (!ALLOWED_TYPES.contains(file.getContentType())) {
                throw new RuntimeException("Nedozvoljen tip fajla: " + file.getContentType());
            }
        }
    }

    private void ensureBucketExists() throws Exception {
        boolean exists = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(bucketName).build()
        );

        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    private void uploadFiles(List<MultipartFile> files, Post post) throws Exception {
        if (files == null) return;

        for (MultipartFile file : files) {
            String fileName = generateFileName(file);

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            PostMedia media = PostMedia.builder()
                    .fileUrl(minioUrl + "/" + bucketName + "/" + fileName)
                    .contentType(file.getContentType())
                    .post(post)
                    .build();

            post.getMediaFiles().add(media);
        }
    }

    private void deleteExistingMedia(Post post) {
        for (PostMedia media : post.getMediaFiles()) {
            try {
                String fileName = extractFileName(media.getFileUrl());

                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(bucketName)
                                .object(fileName)
                                .build()
                );
            } catch (Exception e) {
                System.err.println("Greška pri brisanju fajla: " + e.getMessage());
            }
        }
        post.getMediaFiles().clear();
    }

    private String generateFileName(MultipartFile file) {
        return UUID.randomUUID() + "_" + Objects.requireNonNull(file.getOriginalFilename());
    }

    private String extractFileName(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }
}

