package com.instagram.post_service.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PostMediaTest {

    @Test
    void testPostMediaBuilderAndDefaults() {
        Post post = Post.builder().userId(3L).description("Post").build();

        PostMedia media = PostMedia.builder()
                .fileName("video.mp4")
                .fileUrl("https://example.com/video.mp4")
                .fileSize(2048L)
                .post(post)
                .build();

        assertThat(media.getFileName()).isEqualTo("video.mp4");
        assertThat(media.getFileUrl()).isEqualTo("https://example.com/video.mp4");
        assertThat(media.getFileSize()).isEqualTo(2048L);
        assertThat(media.isActive()).isTrue();
        assertThat(media.getPost()).isEqualTo(post);
    }
}