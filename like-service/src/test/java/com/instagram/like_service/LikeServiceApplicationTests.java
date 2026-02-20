package com.instagram.like_service;

import com.instagram.like_service.model.Like;
import com.instagram.like_service.model.Like.Status;
import com.instagram.like_service.repository.LikeRepository;
import com.instagram.like_service.service.LikeService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LikeServiceTest {

	@Autowired
	private LikeService likeService;

	@Autowired
	private LikeRepository likeRepository;

	private final Long userId1 = 1L;
	private final Long userId2 = 2L;
	private final Long postId1 = 1L;
	private final Long postId2 = 2L;


	@BeforeEach
	void setup() {
		// Očistimo sve lajkove pre svakog testa
		likeRepository.deleteAll();
	}

	@Test
	@DisplayName("Test addLike i hasUserLiked")
	void testAddAndCheckLike() {
		Like like = likeService.addLike(userId1, postId1);
		assertNotNull(like.getId());
		assertEquals(Status.ACTIVE, like.getStatus());
		assertTrue(likeService.hasUserLiked(userId1, postId1));
	}

	@Test
	@DisplayName("Test removeLike (soft delete)")
	void testRemoveLike() {
		likeService.addLike(userId1, postId1);
		assertTrue(likeService.hasUserLiked(userId1, postId1));

		likeService.removeLike(userId1, postId1);
		assertFalse(likeService.hasUserLiked(userId1, postId1));

		// Provera statusa u bazi
		Like like = likeRepository.findByUserIdAndPostId(userId1, postId1).orElse(null);
		assertNotNull(like);
		assertEquals(Status.REMOVED, like.getStatus());
	}

	@Test
	@DisplayName("Test countLikesByPost i countLikesByUser")
	void testCountLikes() {
		likeService.addLike(userId1, postId1);
		likeService.addLike(userId2, postId1);
		likeService.addLike(userId1, postId2);

		assertEquals(2, likeService.countLikesByPost(postId1));
		assertEquals(1, likeService.countLikesByPost(postId2));

		assertEquals(2, likeService.countLikesByUser(userId1));
		assertEquals(1, likeService.countLikesByUser(userId2));
	}

	@Test
	@DisplayName("Test getLikesByPost i getLikesByUser")
	void testGetLikesLists() {
		likeService.addLike(userId1, postId1);
		likeService.addLike(userId2, postId1);

		List<Like> postLikes = likeService.getLikesByPost(postId1);
		assertEquals(2, postLikes.size());

		List<Like> userLikes = likeService.getLikesByUser(userId1);
		assertEquals(1, userLikes.size());
	}

	@Test
	@DisplayName("Test pagination")
	void testPagination() {
		for (long i = 1; i <= 15; i++) {
			likeService.addLike(i, postId1);
		}

		Page<Like> page1 = likeService.getLikesByPost(postId1, PageRequest.of(0, 10));
		Page<Like> page2 = likeService.getLikesByPost(postId1, PageRequest.of(1, 10));

		assertEquals(10, page1.getContent().size());
		assertEquals(5, page2.getContent().size());
		assertEquals(15, page1.getTotalElements());
	}

	@Test
	@DisplayName("Test filter by date")
	void testFilterByDate() throws InterruptedException {
		LocalDateTime before = LocalDateTime.now();
		Thread.sleep(100); // mala pauza da timestampovi budu različiti
		Like like1 = likeService.addLike(userId1, postId1);
		Thread.sleep(100);
		LocalDateTime after = LocalDateTime.now();

		List<Like> likesBetween = likeService.getLikesByPostBetween(postId1, before, after);
		assertEquals(1, likesBetween.size());
		assertEquals(like1.getId(), likesBetween.get(0).getId());
	}

	@Test
	@DisplayName("Test batch removeAllLikesByPost i removeAllLikesByUser")
	void testBatchOperations() {
		likeService.addLike(userId1, postId1);
		likeService.addLike(userId2, postId1);
		likeService.addLike(userId1, postId2);

		// Batch remove by post
		likeService.removeAllLikesByPost(postId1);
		assertEquals(0, likeService.countLikesByPost(postId1));
		assertEquals(1, likeService.countLikesByPost(postId2));

		// Batch remove by user
		likeService.removeAllLikesByUser(userId1);
		assertEquals(0, likeService.countLikesByUser(userId1));
		assertEquals(0, likeService.countLikesByUser(userId2)); // user2 još nije uklonjen
	}

	@Test
	@DisplayName("Test duplicate like throws exception")
	void testDuplicateLikeException() {
		likeService.addLike(userId1, postId1);
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> likeService.addLike(userId1, postId1));
		assertEquals("User already liked this post", exception.getMessage());
	}
}

