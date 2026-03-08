package com.instagram.post_service.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PostTest {

    @Test
    void testPostBuilderAndDefaults() {
        Post post = Post.builder()
                .userId(1L)
                .description("Test post")
                .build();

        // Provera settera i gettera
        assertThat(post.getUserId()).isEqualTo(1L);
        assertThat(post.getDescription()).isEqualTo("Test post");

        // Provera default vrednosti
        assertThat(post.getLikes()).isEqualTo(0);
        assertThat(post.getCommentsCount()).isEqualTo(0);
        assertThat(post.isActive()).isTrue();

        // Provera liste media
        assertThat(post.getMediaFiles()).isNotNull();
        assertThat(post.getMediaFiles()).isEmpty();
    }

    @Test
    void testAddMediaToPost() {
        Post post = Post.builder().userId(2L).description("With media").build();

        PostMedia media = PostMedia.builder()
                .fileName("image.jpg")
                .fileUrl("https://example.com/image.jpg")
                .fileSize(1024L)
                .post(post)
                .build();

        // Dodavanje media u post
        post.getMediaFiles().add(media);

        assertThat(post.getMediaFiles()).hasSize(1);
        assertThat(post.getMediaFiles().get(0).getFileName()).isEqualTo("image.jpg");
        assertThat(post.getMediaFiles().get(0).getPost()).isEqualTo(post);
    }
}