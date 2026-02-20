package com.instagram.post_service;

import com.instagram.post_service.entity.Post;
import com.instagram.post_service.entity.PostMedia;
import com.instagram.post_service.repository.PostRepository;
import com.instagram.post_service.service.PostService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class PostServiceApplicationTests {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private MinioClient minioClient;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this); // AutoCloseable warning uklonjen
    }

    // ==================== TEST 1: Kreiranje posta sa medijima ====================
    @Test
    void testCreatePostWithMedia() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("test.jpg");
        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.getSize()).thenReturn(1024L);
        when(file.getInputStream()).thenAnswer(invocation -> new ByteArrayInputStream(new byte[1024]));

        List<MultipartFile> files = Collections.singletonList(file);

        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        when(postRepository.save(postCaptor.capture())).thenAnswer(i -> i.getArgument(0));

        Post post = postService.createPostWithMedia("Opis testa", 1L, files);

        assertNotNull(post);
        assertEquals("Opis testa", post.getDescription());
        assertEquals(1L, post.getUserId());
        assertEquals(1, post.getMediaFiles().size());

        PostMedia media = post.getMediaFiles().get(0); // getFirst() -> get(0)
        assertEquals("image/jpeg", media.getContentType());
        assertTrue(media.getFileUrl().contains("instagram-media"));
    }

    // ==================== TEST 2: Dobavljanje svih postova ====================
    @Test
    void testGetAllPosts() {
        Post post1 = Post.builder().id(1L).description("Post 1").userId(1L).build();
        Post post2 = Post.builder().id(2L).description("Post 2").userId(2L).build();

        when(postRepository.findAll()).thenReturn(Arrays.asList(post1, post2));

        List<Post> posts = postService.getAllPosts();
        assertEquals(2, posts.size());
    }

    // ==================== TEST 3: Dobavljanje postova po userId ====================
    @Test
    void testGetPostsByUserId() {
        Post post1 = Post.builder().id(1L).description("Post 1").userId(1L).build();
        when(postRepository.findByUserId(1L)).thenReturn(Collections.singletonList(post1));

        List<Post> posts = postService.getPostsByUserId(1L);
        assertEquals(1, posts.size());
        assertEquals(1L, posts.get(0).getUserId()); // getFirst() -> get(0)
    }

    // ==================== TEST 4: Brisanje posta ====================
    @Test
    void testDeletePost() throws Exception {
        Post post = Post.builder()
                .id(1L)
                .description("Test delete")
                .userId(1L)
                .mediaFiles(new ArrayList<>())
                .build();

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        doNothing().when(minioClient).removeObject(any(RemoveObjectArgs.class));
        doNothing().when(postRepository).delete(post);

        assertDoesNotThrow(() -> postService.deletePost(1L));
        verify(postRepository, times(1)).delete(post);
    }

    // ==================== TEST 5: Ažuriranje posta ====================
    @Test
    void testUpdatePost() throws Exception {
        Post post = Post.builder()
                .id(1L)
                .description("Stari opis")
                .userId(1L)
                .mediaFiles(new ArrayList<>())
                .build();

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenAnswer(i -> i.getArgument(0));

        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("nova.jpg");
        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.getSize()).thenReturn(1024L);
        when(file.getInputStream()).thenAnswer(invocation -> new ByteArrayInputStream(new byte[1024]));

        List<MultipartFile> files = Collections.singletonList(file);

        doNothing().when(minioClient).putObject(any(PutObjectArgs.class));
        doNothing().when(minioClient).removeObject(any(RemoveObjectArgs.class));

        Post updatedPost = postService.updatePost(1L, "Novi opis", files);

        assertEquals("Novi opis", updatedPost.getDescription());
        assertEquals(1, updatedPost.getMediaFiles().size());
        assertEquals("image/jpeg",
                updatedPost.getMediaFiles().get(0).getContentType()); // getFirst() -> get(0)
    }
}