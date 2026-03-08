package com.instagram.post_service.service;

import com.instagram.post_service.entity.Post;
import com.instagram.post_service.entity.PostMedia;
import com.instagram.post_service.repository.PostRepository;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostServiceUnitTest {

    private PostRepository postRepository;
    private MinioClient minioClient;
    private PostService postService;

    @BeforeEach
    void setUp() {
        postRepository = mock(PostRepository.class);
        minioClient = mock(MinioClient.class);
        postService = new PostService(postRepository, minioClient);

        // postavimo bucketName i minioUrl
        postService.bucketName = "instagram-media";
        postService.minioUrl = "http://localhost:9000";
    }

    @Test
    void testCreatePostWithMedia() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", new byte[]{1, 2, 3}
        );
        List<MultipartFile> files = List.of(file);

        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Post post = postService.createPostWithMedia("Desc", 1L, files);

        assertEquals("Desc", post.getDescription());
        assertEquals(1, post.getMediaFiles().size());

        // proverimo da li je MinioClient pozvan
        verify(minioClient, times(1)).putObject(any(PutObjectArgs.class));
    }

    @Test
    void testUpdatePostWithNewMedia() throws Exception {
        Post post = Post.builder().id(1L).description("Old").mediaFiles(new ArrayList<>()).build();
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MockMultipartFile file = new MockMultipartFile(
                "file", "update.png", "image/png", new byte[]{1, 2}
        );

        Post updated = postService.updatePost(1L, "New", List.of(file));

        assertEquals("New", updated.getDescription());
        assertEquals(1, updated.getMediaFiles().size());

        verify(minioClient, times(1)).putObject(any(PutObjectArgs.class));
    }

    @Test
    void testDeletePost() throws Exception {
        Post post = Post.builder().id(1L).mediaFiles(new ArrayList<>()).build();
        PostMedia media = PostMedia.builder()
                .fileUrl("http://localhost:9000/instagram-media/file.jpg")
                .build();
        post.getMediaFiles().add(media);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        postService.deletePost(1L);

        verify(minioClient, times(1)).removeObject(any(RemoveObjectArgs.class));
        verify(postRepository, times(1)).delete(post);
    }

    @Test
    void testGetPostByIdExists() {
        Post post = Post.builder().id(1L).description("Hello").build();
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        Post found = postService.getPostById(1L);

        assertEquals("Hello", found.getDescription());
    }

    @Test
    void testGetPostByIdNotExists() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> postService.getPostById(1L));

        assertTrue(ex.getMessage().contains("ne postoji"));
    }

    @Test
    void testCreatePostWithoutMedia() throws Exception {
        // mock save da vrati sam post
        when(postRepository.save(any(Post.class))).thenAnswer(i -> i.getArgument(0));

        Post post = postService.createPostWithMedia("Desc", 1L, List.of());

        assertEquals("Desc", post.getDescription());
        assertTrue(post.getMediaFiles().isEmpty());

        // Minio ne treba da bude pozvan
        verify(minioClient, never()).putObject(any(PutObjectArgs.class));
    }
}