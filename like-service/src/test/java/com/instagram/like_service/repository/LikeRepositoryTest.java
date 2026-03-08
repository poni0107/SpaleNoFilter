package com.instagram.like_service.repository;

import com.instagram.like_service.model.Like;
import com.instagram.like_service.model.Like.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class LikeRepositoryTest {

    @Autowired
    private LikeRepository likeRepository;

    private Like like1;
    private Like like2;

    @BeforeEach
    void setUp() {
        likeRepository.deleteAll();

        like1 = Like.builder()
                .userId(1L)
                .postId(1L)
                .status(Status.ACTIVE)
                .build();

        like2 = Like.builder()
                .userId(2L)
                .postId(1L)
                .status(Status.REMOVED)
                .build();

        likeRepository.save(like1);
        likeRepository.save(like2);
    }

    @Test
    void testFindByUserIdAndPostId() {
        Optional<Like> found = likeRepository.findByUserIdAndPostId(1L, 1L);
        assertTrue(found.isPresent());
        assertEquals(like1.getUserId(), found.get().getUserId());
    }

    @Test
    void testExistsByUserIdAndPostId() {
        assertTrue(likeRepository.existsByUserIdAndPostId(1L, 1L));
        assertFalse(likeRepository.existsByUserIdAndPostId(3L, 1L));
    }

    @Test
    void testIsActiveLikeExists() {
        assertTrue(likeRepository.isActiveLikeExists(1L, 1L));
        assertFalse(likeRepository.isActiveLikeExists(2L, 1L));
    }

    @Test
    void testFindByPostIdAndStatus() {
        List<Like> activeLikes = likeRepository.findByPostIdAndStatus(1L, Status.ACTIVE);
        assertEquals(1, activeLikes.size());
        assertEquals(Status.ACTIVE, activeLikes.get(0).getStatus());
    }

    @Test
    void testFindByUserIdAndStatus() {
        List<Like> removedLikes = likeRepository.findByUserIdAndStatus(2L, Status.REMOVED);
        assertEquals(1, removedLikes.size());
        assertEquals(Status.REMOVED, removedLikes.get(0).getStatus());
    }

    @Test
    void testFindByPostIdAndCreatedAtBetween() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        List<Like> likes = likeRepository.findByPostIdAndCreatedAtBetween(1L, start, end);
        assertEquals(2, likes.size());
    }

    @Test
    void testPagination() {
        Page<Like> page = likeRepository.findByPostId(1L, PageRequest.of(0, 1));
        assertEquals(1, page.getContent().size());
        assertEquals(2, page.getTotalElements());
    }

    @Test
    void testDeleteByUserIdAndPostId() {
        likeRepository.deleteByUserIdAndPostId(1L, 1L);
        assertFalse(likeRepository.existsByUserIdAndPostId(1L, 1L));
    }

    @Test
    void testDeleteAllByPostId() {
        likeRepository.deleteAllByPostId(1L);
        assertEquals(0, likeRepository.findByPostId(1L).size());
    }

    @Test
    void testCountByPostIdAndStatus() {
        long activeCount = likeRepository.countByPostIdAndStatus(1L, Status.ACTIVE);
        assertEquals(1, activeCount);
    }

    @Test
    void testFindByUserIdAndStatusAndCreatedAtBetween() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        List<Like> result = likeRepository.findByUserIdAndStatusAndCreatedAtBetween(1L, Status.ACTIVE, start, end);
        assertEquals(1, result.size());
        assertEquals(Status.ACTIVE, result.get(0).getStatus());
    }
}