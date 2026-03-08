package com.instagram.like_service.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LikeTest {

    private Like like;

    @BeforeEach
    void setUp() {
        // Kreiramo Like preko builder-a
        like = Like.builder()
                .id(1L)
                .userId(10L)
                .postId(20L)
                .createdAt(LocalDateTime.now())
                .status(Like.Status.ACTIVE)
                .build();
    }

    @Test
    void testIsActive_WhenStatusActive_ShouldReturnTrue() {
        assertTrue(like.isActive(), "Like should be active initially");
    }

    @Test
    void testRemove_ShouldSetStatusToRemoved() {
        like.remove();
        assertEquals(Like.Status.REMOVED, like.getStatus());
        assertFalse(like.isActive(), "Like should not be active after remove()");
    }

    @Test
    void testConstructorWithUserIdAndPostId_ShouldSetStatusActive() {
        Like newLike = new Like(5L, 10L);
        assertEquals(5L, newLike.getUserId());
        assertEquals(10L, newLike.getPostId());
        assertEquals(Like.Status.ACTIVE, newLike.getStatus());
    }

    @Test
    void testBuilder_ShouldCreateLikeWithCorrectValues() {
        assertEquals(1L, like.getId());
        assertEquals(10L, like.getUserId());
        assertEquals(20L, like.getPostId());
        assertEquals(Like.Status.ACTIVE, like.getStatus());
        assertNotNull(like.getCreatedAt(), "createdAt should not be null");
    }

    @Test
    void testSetters_ShouldUpdateValues() {
        like.setUserId(100L);
        like.setPostId(200L);
        like.setStatus(Like.Status.REMOVED);

        assertEquals(100L, like.getUserId());
        assertEquals(200L, like.getPostId());
        assertEquals(Like.Status.REMOVED, like.getStatus());
    }
}