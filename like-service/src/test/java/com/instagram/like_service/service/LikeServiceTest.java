package com.instagram.like_service.service;

import com.instagram.like_service.model.Like;
import com.instagram.like_service.model.Like.Status;
import com.instagram.like_service.repository.LikeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LikeServiceTest {

    @InjectMocks
    private LikeService likeService;

    @Mock
    private LikeRepository likeRepository;

    private Like like1;
    private Like like2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        like1 = new Like(1L, 1L);
        like1.setStatus(Status.ACTIVE);

        like2 = new Like(2L, 1L);
        like2.setStatus(Status.REMOVED);
    }

    @Test
    void addLike_ShouldSaveNewLike() {
        when(likeRepository.existsByUserIdAndPostIdAndStatus(1L, 1L, Status.ACTIVE)).thenReturn(false);
        when(likeRepository.save(any(Like.class))).thenReturn(like1);

        Like result = likeService.addLike(1L, 1L);

        assertNotNull(result);
        assertEquals(Status.ACTIVE, result.getStatus());
        verify(likeRepository, times(1)).save(any(Like.class));
    }

    @Test
    void addLike_WhenAlreadyLiked_ShouldThrow() {
        when(likeRepository.existsByUserIdAndPostIdAndStatus(1L, 1L, Status.ACTIVE)).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> likeService.addLike(1L, 1L));

        assertEquals("User already liked this post", exception.getMessage());
        verify(likeRepository, never()).save(any(Like.class));
    }

    @Test
    void removeLike_ShouldSetStatusRemoved() {
        when(likeRepository.findByUserIdAndPostIdAndStatus(1L, 1L, Status.ACTIVE))
                .thenReturn(Optional.of(like1));

        likeService.removeLike(1L, 1L);

        assertEquals(Status.REMOVED, like1.getStatus());
        verify(likeRepository, times(1)).save(like1);
    }

    @Test
    void removeLike_WhenNotFound_ShouldDoNothing() {
        when(likeRepository.findByUserIdAndPostIdAndStatus(1L, 1L, Status.ACTIVE))
                .thenReturn(Optional.empty());

        likeService.removeLike(1L, 1L);

        verify(likeRepository, never()).save(any());
    }

    @Test
    void getLikesByPost_ShouldReturnActiveLikes() {
        when(likeRepository.findByPostIdAndStatus(1L, Status.ACTIVE))
                .thenReturn(Arrays.asList(like1));

        List<Like> result = likeService.getLikesByPost(1L);

        assertEquals(1, result.size());
        assertEquals(Status.ACTIVE, result.get(0).getStatus());
    }

    @Test
    void getLikesByPostWithPagination_ShouldReturnPage() {
        Page<Like> page = new PageImpl<>(Arrays.asList(like1));
        when(likeRepository.findByPostIdAndStatus(1L, Status.ACTIVE, PageRequest.of(0, 10)))
                .thenReturn(page);

        Page<Like> result = likeService.getLikesByPost(1L, PageRequest.of(0, 10));

        assertEquals(1, result.getContent().size());
        assertEquals(Status.ACTIVE, result.getContent().get(0).getStatus());
    }

    @Test
    void countLikesByPost_ShouldReturnCount() {
        when(likeRepository.countByPostIdAndStatus(1L, Status.ACTIVE)).thenReturn(5L);
        assertEquals(5L, likeService.countLikesByPost(1L));
    }

    @Test
    void countLikesByUser_ShouldReturnCount() {
        when(likeRepository.countByUserIdAndStatus(1L, Status.ACTIVE)).thenReturn(3L);
        assertEquals(3L, likeService.countLikesByUser(1L));
    }

    @Test
    void hasUserLiked_ShouldReturnTrueOrFalse() {
        when(likeRepository.isActiveLikeExists(1L, 1L)).thenReturn(true);
        assertTrue(likeService.hasUserLiked(1L, 1L));

        when(likeRepository.isActiveLikeExists(2L, 1L)).thenReturn(false);
        assertFalse(likeService.hasUserLiked(2L, 1L));
    }

    @Test
    void getLikesByUser_ShouldReturnActiveLikes() {
        when(likeRepository.findByUserIdAndStatus(1L, Status.ACTIVE)).thenReturn(Arrays.asList(like1));

        List<Like> result = likeService.getLikesByUser(1L);

        assertEquals(1, result.size());
        assertEquals(Status.ACTIVE, result.get(0).getStatus());
    }

    @Test
    void getLikesByUserWithPagination_ShouldReturnPage() {
        Page<Like> page = new PageImpl<>(Arrays.asList(like1));
        when(likeRepository.findByUserIdAndStatus(1L, Status.ACTIVE, PageRequest.of(0, 10)))
                .thenReturn(page);

        Page<Like> result = likeService.getLikesByUser(1L, PageRequest.of(0, 10));

        assertEquals(1, result.getContent().size());
        assertEquals(Status.ACTIVE, result.getContent().get(0).getStatus());
    }

    @Test
    void getLikesByPostBetween_ShouldReturnFilteredList() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        when(likeRepository.findByPostIdAndStatusAndCreatedAtBetween(1L, Status.ACTIVE, start, end))
                .thenReturn(Arrays.asList(like1));

        List<Like> result = likeService.getLikesByPostBetween(1L, start, end);
        assertEquals(1, result.size());
    }

    @Test
    void getLikesByUserBetween_ShouldReturnFilteredList() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        when(likeRepository.findByUserIdAndStatusAndCreatedAtBetween(1L, Status.ACTIVE, start, end))
                .thenReturn(Arrays.asList(like1));

        List<Like> result = likeService.getLikesByUserBetween(1L, start, end);
        assertEquals(1, result.size());
    }

    @Test
    void removeAllLikesByPost_ShouldSetAllToRemoved() {
        when(likeRepository.findByPostIdAndStatus(1L, Status.ACTIVE))
                .thenReturn(Arrays.asList(like1));

        likeService.removeAllLikesByPost(1L);
        assertEquals(Status.REMOVED, like1.getStatus());
        verify(likeRepository, times(1)).save(like1);
    }

    @Test
    void removeAllLikesByUser_ShouldSetAllToRemoved() {
        when(likeRepository.findByUserIdAndStatus(1L, Status.ACTIVE))
                .thenReturn(Arrays.asList(like1));

        likeService.removeAllLikesByUser(1L);
        assertEquals(Status.REMOVED, like1.getStatus());
        verify(likeRepository, times(1)).save(like1);
    }
}