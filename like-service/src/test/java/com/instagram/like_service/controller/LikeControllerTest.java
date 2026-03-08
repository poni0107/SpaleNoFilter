package com.instagram.like_service.controller;

import com.instagram.like_service.model.Like;
import com.instagram.like_service.service.LikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LikeControllerTest {

    @Mock
    private LikeService likeService;

    @InjectMocks
    private LikeController likeController;

    private Like like;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        like = Like.builder()
                .id(1L)
                .userId(1L)
                .postId(1L)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void addLike_ShouldReturnCreatedLike() {
        when(likeService.addLike(1L, 1L)).thenReturn(like);

        ResponseEntity<Like> response = likeController.addLike(1L, 1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(like, response.getBody());
        verify(likeService, times(1)).addLike(1L, 1L);
    }

    @Test
    void removeLike_ShouldCallServiceAndReturnNoContent() {
        doNothing().when(likeService).removeLike(1L, 1L);

        ResponseEntity<Void> response = likeController.removeLike(1L, 1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(likeService, times(1)).removeLike(1L, 1L);
    }

    @Test
    void getLikesByPost_ShouldReturnListOfLikes() {
        when(likeService.getLikesByPost(1L)).thenReturn(List.of(like));

        ResponseEntity<List<Like>> response = likeController.getLikesByPost(1L);

        assertEquals(1, response.getBody().size());
        assertEquals(like, response.getBody().get(0));
    }

    @Test
    void getLikesByPostPage_ShouldReturnPagedLikes() {
        Page<Like> page = new PageImpl<>(List.of(like));
        when(likeService.getLikesByPost(1L, PageRequest.of(0, 10))).thenReturn(page);

        ResponseEntity<Page<Like>> response = likeController.getLikesByPostPage(1L, 0, 10);

        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(like, response.getBody().getContent().get(0));
    }

    @Test
    void hasUserLiked_ShouldReturnTrueOrFalse() {
        when(likeService.hasUserLiked(1L, 1L)).thenReturn(true);

        ResponseEntity<Boolean> response = likeController.hasUserLiked(1L, 1L);

        assertTrue(response.getBody());
        verify(likeService, times(1)).hasUserLiked(1L, 1L);
    }

    @Test
    void getLikesByUser_ShouldReturnListOfLikes() {
        when(likeService.getLikesByUser(1L)).thenReturn(List.of(like));

        ResponseEntity<List<Like>> response = likeController.getLikesByUser(1L);

        assertEquals(1, response.getBody().size());
        assertEquals(like, response.getBody().get(0));
    }

    @Test
    void getLikesByUserPage_ShouldReturnPagedLikes() {
        Page<Like> page = new PageImpl<>(List.of(like));
        when(likeService.getLikesByUser(1L, PageRequest.of(0, 10))).thenReturn(page);

        ResponseEntity<Page<Like>> response = likeController.getLikesByUserPage(1L, 0, 10);

        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(like, response.getBody().getContent().get(0));
    }

    @Test
    void getLikesByPostBetween_ShouldReturnFilteredLikes() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        when(likeService.getLikesByPostBetween(1L, start, end)).thenReturn(List.of(like));

        ResponseEntity<List<Like>> response = likeController.getLikesByPostBetween(1L, start, end);

        assertEquals(1, response.getBody().size());
    }

    @Test
    void getLikesByUserBetween_ShouldReturnFilteredLikes() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        when(likeService.getLikesByUserBetween(1L, start, end)).thenReturn(List.of(like));

        ResponseEntity<List<Like>> response = likeController.getLikesByUserBetween(1L, start, end);

        assertEquals(1, response.getBody().size());
    }

    @Test
    void removeAllLikesByPost_ShouldCallService() {
        doNothing().when(likeService).removeAllLikesByPost(1L);

        ResponseEntity<Void> response = likeController.removeAllLikesByPost(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(likeService, times(1)).removeAllLikesByPost(1L);
    }

    @Test
    void removeAllLikesByUser_ShouldCallService() {
        doNothing().when(likeService).removeAllLikesByUser(1L);

        ResponseEntity<Void> response = likeController.removeAllLikesByUser(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(likeService, times(1)).removeAllLikesByUser(1L);
    }

    @Test
    void countLikesByPost_ShouldReturnCount() {
        when(likeService.countLikesByPost(1L)).thenReturn(5L);

        ResponseEntity<Long> response = likeController.countLikesByPost(1L);

        assertEquals(5L, response.getBody());
    }

    @Test
    void countLikesByUser_ShouldReturnCount() {
        when(likeService.countLikesByUser(1L)).thenReturn(3L);

        ResponseEntity<Long> response = likeController.countLikesByUser(1L);

        assertEquals(3L, response.getBody());
    }
}