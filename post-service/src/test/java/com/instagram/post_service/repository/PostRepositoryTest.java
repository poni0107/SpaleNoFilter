package com.instagram.post_service.repository;

import com.instagram.post_service.entity.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Post post1;
    private Post post2;

    @BeforeEach
    void setUp() {
        // Drop table ako postoji i kreiraj novu za H2
        jdbcTemplate.execute("DROP TABLE IF EXISTS posts");

        jdbcTemplate.execute("""
            CREATE TABLE posts (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                description VARCHAR(255),
                user_id BIGINT NOT NULL,
                is_active BOOLEAN NOT NULL,
                likes INT NOT NULL,
                comments_count INT NOT NULL,
                created_at TIMESTAMP,
                updated_at TIMESTAMP
            )
        """);

        // Kreiranje instanci Post objekata
        post1 = Post.builder()
                .description("Prvi post")
                .userId(1L)
                .isActive(true)
                .likes(0)
                .commentsCount(0)
                .build();

        post2 = Post.builder()
                .description("Drugi post")
                .userId(2L)
                .isActive(true)
                .likes(0)
                .commentsCount(0)
                .build();

        // Sačuvaj u repozitorijum
        postRepository.save(post1);
        postRepository.save(post2);
    }

    @Test
    void testFindByUserId() {
        List<Post> user1Posts = postRepository.findByUserId(1L);
        assertEquals(1, user1Posts.size());
        assertEquals("Prvi post", user1Posts.get(0).getDescription());

        List<Post> user2Posts = postRepository.findByUserId(2L);
        assertEquals(1, user2Posts.size());
        assertEquals("Drugi post", user2Posts.get(0).getDescription());
    }

    @Test
    void testFindAllByOrderByIdDesc() {
        List<Post> posts = postRepository.findAllByOrderByIdDesc();
        assertEquals(2, posts.size());
        assertEquals("Drugi post", posts.get(0).getDescription()); // poslednji po ID-ju
        assertEquals("Prvi post", posts.get(1).getDescription());
    }

    @Test
    void testSaveAndDelete() {
        Post newPost = Post.builder()
                .description("Novi post")
                .userId(1L)
                .isActive(true)
                .likes(0)
                .commentsCount(0)
                .build();
        Post saved = postRepository.save(newPost);

        assertNotNull(saved.getId());

        postRepository.delete(saved);
        assertFalse(postRepository.findById(saved.getId()).isPresent());
    }
}